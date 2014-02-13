package br.gov.lexml.madoc.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.gov.lexml.madoc.MadocException;
import br.gov.lexml.madoc.catalog.CatalogException;
import br.gov.lexml.madoc.catalog.CatalogService;
import br.gov.lexml.madoc.catalog.MadocDocumentModelData;
import br.gov.lexml.madoc.components.BaseWizardComponent;
import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.components.PagesComponent;
import br.gov.lexml.madoc.components.QuestionComponent;
import br.gov.lexml.madoc.components.rules.RulesVisitor;
import br.gov.lexml.madoc.execution.hosteditor.HostEditor;
import br.gov.lexml.madoc.schema.entity.CatalogItemType;
import br.gov.lexml.madoc.schema.entity.DataSetMapType;
import br.gov.lexml.madoc.schema.entity.MadocAnswerType;
import br.gov.lexml.madoc.schema.entity.MadocDocumentType;
import br.gov.lexml.madoc.schema.entity.MadocInfoAnswersType;
import br.gov.lexml.madoc.schema.entity.MadocReferencesAnswersType;
import br.gov.lexml.madoc.schema.entity.MadocReferencesAnswersType.EmptyVersionItemsIncludedFromCatalog;
import br.gov.lexml.madoc.schema.entity.ObjectFactory;
import br.gov.lexml.madoc.schema.entity.QuestionsAnswersType;

public abstract class AbstractWizardExecution<P extends PagesComponent<?, ? extends C>, C> implements WizardExecution<P, C> {
	
	private static final Logger log = LoggerFactory.getLogger(AbstractWizardExecution.class);
	
	protected final MadocAnswerType originalAnswer;
	protected final String documentVersion;
	protected final CatalogService catalogService;
	protected final ComponentController controller = new ComponentController();

	private final MadocDocumentType currentMadocDocument;
	private final MadocDocumentType originalMadocDocument;
	
	protected HostEditor hostEditor;
	protected P pagesComponent;
	protected CatalogEventListenerExecution catalogEventListenerExecution;
	protected DataSetMapType answerMetadata;

	private QuestionsAnswersType questionsAnswersForChangedComparison;

	/**
	 * This constructor starts a new Madoc execution based the last version of a model. No data are load.
	 * @param catalogService
	 * @param madocId
	 * @throws MadocException
	 */
	public AbstractWizardExecution(
			CatalogService catalogService,
			String madocId) throws MadocException {
		this(catalogService, madocId, null);
	}

	/**
	 * This constructor starts a new Madoc execution based an specifiec version of a model. No data are load.
	 * @param catalogService
	 * @param madocId
	 * @param documentVersion
	 * @throws MadocException
	 */
	public AbstractWizardExecution(
			CatalogService catalogService,
			String madocId, 
			String documentVersion) throws MadocException {
		
		this.catalogService = catalogService;
		setupCatalogService();
		
		this.originalAnswer = null;
		
		if (documentVersion == null){
			documentVersion = catalogService.getVersion(madocId);
		}
		
		this.documentVersion = documentVersion;

		// getting MadocDocument from CatalogService
		MadocDocumentModelData md = catalogService.getMadocDocumentModel(madocId, documentVersion);
		if(md == null) {
			throw new CatalogException("Document not found in catalog: id=" + madocId + ", version=" + documentVersion);
		}
		this.originalMadocDocument = md.getMadocDocument();
		
		// expanding options
		this.currentMadocDocument = cloneAndExpandMadocDocument(originalMadocDocument);
		
		controller.setCatalogService(catalogService);
	}
	
	/**
	 * This constructor starts a new Madoc execution based on an answer set.
	 * @param catalogService
	 * @param originalAnswer
	 * @throws MadocException
	 */
	public AbstractWizardExecution(
			CatalogService catalogService,
			MadocAnswerType originalAnswer) throws MadocException {

		this.catalogService = catalogService;
		setupCatalogService(originalAnswer.getMadocReferences());
		
		this.documentVersion = originalAnswer.getMadocReferences().getMadocDocument().getVersion();
		this.originalAnswer = originalAnswer;
		
		// getting original MadocDocument
		this.originalMadocDocument = 
			catalogService.getMadocDocumentModel(
					originalAnswer.getMadocReferences().getMadocDocument().getId(), 
					originalAnswer.getMadocReferences().getMadocDocument().getVersion())
					.getMadocDocument();
		
		// expanding options
		this.currentMadocDocument = cloneAndExpandMadocDocument(originalMadocDocument);
		
		// setting answer metadata
		setAnswerMedatada(originalAnswer.getMetadata());
		
		controller.setCatalogService(catalogService);
	}
	
	/**
	 * This constructor starts a new Madoc execution based on MadocReferences.
	 * @param catalogService
	 * @param madocReferences
	 * @throws MadocException
	 */
	public AbstractWizardExecution(
			CatalogService catalogService,
			MadocReferencesAnswersType madocReferences) throws MadocException {
		
		this.catalogService = catalogService;
		setupCatalogService(madocReferences);
		
		this.documentVersion = madocReferences.getMadocDocument().getVersion();
		this.originalAnswer = null;
		
		// getting original MadocDocument
		this.originalMadocDocument = 
				catalogService.getMadocDocumentModel(
						madocReferences.getMadocDocument().getId(), 
						madocReferences.getMadocDocument().getVersion())
						.getMadocDocument();
		
		// expanding options
		this.currentMadocDocument = cloneAndExpandMadocDocument(originalMadocDocument);
		
		controller.setCatalogService(catalogService);
	}
	
	/**
	 * Prepare Listeners on CatalogService
	 */
	private void setupCatalogService(){
		setupCatalogService(null);
	}
	
	/**
	 * Prepare Listeners and overridden versions on CatalogService 
	 */
	private void setupCatalogService(MadocReferencesAnswersType madocReferences){
		if (catalogService!= null){
			catalogEventListenerExecution = new CatalogEventListenerExecution();
			catalogService.addCatalogEventListener(catalogEventListenerExecution);
			catalogService.clearModelVersionOverride();
			
			//add EmptyVersionItemsIncludedFromCatalog
			if (madocReferences != null){
				for (CatalogItemType cit : madocReferences.getEmptyVersionItemsIncludedFromCatalog().getCatalogItem()){
					catalogService.addModelVersionOverride(cit.getMetadata().getId(), cit.getVersion());
				}
				
				//add skeleton
				catalogService.addModelVersionOverride(madocReferences.getMadocSkeleton().getId(), madocReferences.getMadocSkeleton().getVersion()); 
			}
		}
	}
	
	/**
	 * Clone MadocDocumentType and expand options
	 * @param mdt
	 * @return
	 */
	private MadocDocumentType cloneAndExpandMadocDocument(MadocDocumentType mdt){
		return new OptionsExpansion().cloneAndExpandOptions(mdt);
	}
	
	/**
	 * Returns a QuestionsAnswersType from the PagesComponent (P)
	 * @param pages
	 * @return
	 */
	private QuestionsAnswersType getQuestionsAnswersFromPagesComponent(){
		QuestionsAnswersType questionsAnswers = new ObjectFactory().createQuestionsAnswersType();
		questionsAnswers.getQuestionAnswer().addAll(pagesComponent.getQuestionsAnswers());
		questionsAnswers.setRequiredQuestionsAnswered(isRequiredQuestionsAnswered(false));
		return questionsAnswers;
	}
	
	/**
	 * Creates the platform specific visual component
	 * @return
	 * @throws MadocException
	 */
	protected abstract P createComponent() throws MadocException;
	
	@Override
	public void setHostEditor(HostEditor hostEditor){
		this.hostEditor = hostEditor;
		controller.setHostEditor(hostEditor);
	}
	
	@Override
	public HostEditor getHostEditor() {
		return hostEditor;
	}
	
	@Override
	public C getComponent() throws MadocException {
		if (pagesComponent == null){
			
			if (getCurrentMadocDocument().getWizard() == null 
					|| getCurrentMadocDocument().getWizard().getPages() == null
					|| getCurrentMadocDocument().getWizard().getPages().getPage().isEmpty()){
				throw new MadocException("There are no pages in MadocWizard.");
			}

			pagesComponent = createComponent();
			
			if (originalAnswer== null){
				// process the rules from WizardType
				if (getCurrentMadocDocument().getWizard()!= null
						&& getCurrentMadocDocument().getWizard().getOnLoadRules()!= null){
					getCurrentMadocDocument().getWizard().getOnLoadRules().accept(new RulesVisitor(controller));
				}
				// process the rules from questions 
				pagesComponent.processOnLoadRulesFromQuestions();
			} else {
				// process original answers if it was used to start this execution
				// inform answers value
				informMadocAnswer(originalAnswer);
			}

			// processing onChange rules from all questions 
			pagesComponent.processOnChangeRulesFromQuestions();
		}

		C component = pagesComponent.getComponent();
		
		// getting questionsAnswersForChangedComparison
		questionsAnswersForChangedComparison = getQuestionsAnswersFromPagesComponent();
		
		return component;
	}
	
	@Override
	public boolean isChanged() {
		return (questionsAnswersForChangedComparison== null) ? true : !questionsAnswersForChangedComparison.equals(getQuestionsAnswersFromPagesComponent());
	}

	@Override
	public void informMadocAnswer(MadocAnswerType madocAnswer) {
		pagesComponent.informMadocAnswer(madocAnswer);
		
		if (madocAnswer!= null){
			questionsAnswersForChangedComparison = madocAnswer.getQuestionsAnswers();
		}
	}
	
	@Override
	public MadocAnswerType createMadocAnswer() {
		
		ObjectFactory objFactory = new ObjectFactory();

		MadocAnswerType answer = objFactory.createMadocAnswerType();
		
		// setting QuestionsAnswers
		answer.setQuestionsAnswers(getQuestionsAnswersFromPagesComponent());
		
		// setting VariablesAnswers
		answer.setVariables(pagesComponent.getVariablesAnswers());

		// setting answer's metadata
		if (getAnswerMetadata()!= null){
			answer.setMetadata(getAnswerMetadata());
		}

		if (catalogService!= null){
			
			// setting Skeleton version
			String skeletonVersion = "";
			if (currentMadocDocument!= null &&
				currentMadocDocument.getTemplates()!= null){
				if (currentMadocDocument.getTemplates().isSetMadocSkeletonVersion()){
					skeletonVersion = currentMadocDocument.getTemplates().getMadocSkeletonVersion();
				} else if (currentMadocDocument.getTemplates().isSetMadocSkeletonId()){
					try {
						skeletonVersion = catalogService.getVersion(currentMadocDocument.getTemplates().getMadocSkeletonId());
					} catch (MadocException e) {
						log.warn("Error getting skeleton version. Skeleton id="+currentMadocDocument.getTemplates().getMadocSkeletonId(), e);
					}
				}
			}
			
			// preparing references
			
			//   preparing MadocDocumentInfo
			MadocInfoAnswersType madocDocInfo = objFactory.createMadocInfoAnswersType(); 
			madocDocInfo.setId(currentMadocDocument.getMetadata().getId());
			madocDocInfo.setVersion(documentVersion);
			
			//   preparing MadocSkeletonInfo
			MadocInfoAnswersType madocSkeletonInfo = objFactory.createMadocInfoAnswersType();
			madocSkeletonInfo.setId(currentMadocDocument.getTemplates().getMadocSkeletonId());
			madocSkeletonInfo.setVersion(skeletonVersion);
			
			//   preparing EmptyVersionItemsIncludedFromCatalog
			EmptyVersionItemsIncludedFromCatalog eviifc = objFactory.createMadocReferencesAnswersTypeEmptyVersionItemsIncludedFromCatalog();
			eviifc.getCatalogItem().addAll(catalogEventListenerExecution.getItemsVersionResolved());
			
			//   setting References
			MadocReferencesAnswersType references = objFactory.createMadocReferencesAnswersType();
			references.setMadocDocument(madocDocInfo);
			references.setMadocSkeleton(madocSkeletonInfo);
			references.setEmptyVersionItemsIncludedFromCatalog(eviifc);
			answer.setMadocReferences(references);
		}
		
		return answer;
	}
	
	@Override
	public boolean isRequiredQuestionsAnswered(boolean highlight) {
		QuestionComponent<?, ?> firstQuestion = null;
		
		//for each questionComponent
		for (QuestionComponent<?, ?> questionComponent : pagesComponent.getQuestionComponents()){
			
			if (questionComponent.isRequiredValueSet()){
				questionComponent.setHighlight(false);
			} else {	
				//save first question
				if (firstQuestion== null){
					firstQuestion = questionComponent;
				}
				
				//change panel color
				if (highlight){
					questionComponent.setHighlight(true);
				}
			}
			
		}
		
		// show page of the first question with no RequiredValuesSet
		if (highlight && firstQuestion != null) {
			pagesComponent.scrollToQuestion(firstQuestion);
		}
		
		return firstQuestion == null;		
	}
	
	@Override
	public void setQuestionValue(String questionId, String value){
		controller.setQuestionValue(questionId, value);
	}
	
	@Override
	public void addQuestionValue(String questionId, String value){
		controller.addQuestionValue(questionId, value);
	}
	
	@Override
	public BaseWizardComponent<?, ?> getBaseWizardComponentById(String id) {
		return controller.getBaseWizardComponentById(id);
	}
	
	@Override
	public MadocDocumentType getCurrentMadocDocument() {
		return currentMadocDocument;
	}
	
	@Override
	public MadocDocumentType getOriginalMadocDocument(){
		return originalMadocDocument;
	}

	@Override
	public boolean isRequiredQuestionsAnswered() {
		return isRequiredQuestionsAnswered(true);
	}
	
	@Override
	public void setAnswerMedatada(DataSetMapType metadata) {
		this.answerMetadata = metadata;
	}
	
	@Override
	public DataSetMapType getAnswerMetadata() {
		return answerMetadata;
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		catalogService.removeCatalogEventListener(catalogEventListenerExecution);
		catalogEventListenerExecution = null;
	}
	

}
