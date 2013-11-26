package br.gov.lexml.madoc.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.gov.lexml.madoc.catalog.CatalogService;
import br.gov.lexml.madoc.components.rules.RulesVisitor;
import br.gov.lexml.madoc.execution.hosteditor.HostEditor;
import br.gov.lexml.madoc.execution.hosteditor.HostEditorReplacer;
import br.gov.lexml.madoc.schema.entity.MadocAnswerType;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerType;
import br.gov.lexml.madoc.schema.entity.VariableAnswerType;
import br.gov.lexml.madoc.schema.entity.VariablesAnswersType;

/**
 * QuestionComponent controller
 * @author lauro
 *
 */
public class ComponentController {

	private static final Logger log = LoggerFactory.getLogger(ComponentController.class);

	private final Map<String, BaseWizardComponent<?, ?>> baseWizardComponentsMap = 
			new HashMap<String, BaseWizardComponent<?, ?>>();
	
	private final List<QuestionComponent<?, ?>> questionList = new ArrayList<QuestionComponent<?, ?>>();
	
	private final List<ParentComponent<?,?>> parentComponentList = new ArrayList<ParentComponent<?,?>>();

	private Map<String, String> variables = new HashMap<String, String>();
	private HostEditorReplacer hostEditorReplacer = new HostEditorReplacer();
	private HostEditor hostEditor;
	private CatalogService catalogService;

	/*
	 * CatalogService methods
	 */
	
	public void setCatalogService(CatalogService catalogService){
		this.catalogService = catalogService;
	}
	
	public CatalogService getCatalogService() {
		return catalogService;
	}
	
	/*
	 * HostEditor and HostEditorReplacer methods
	 */
	
	public HostEditorReplacer getHostEditorReplacer() {
		return hostEditorReplacer;
	}
	public HostEditor getHostEditor() {
		return hostEditor;
	}
	public void setHostEditor(HostEditor hostEditor){
		this.hostEditor = hostEditor;
		this.hostEditorReplacer = new HostEditorReplacer(hostEditor);
	}

	
	/*
	 * Main controller methods
	 */

	/**
	 * Register a BaseWizardComponent or a QuestionComponent in this controller
	 * @param baseWizardComponent
	 */
	public void register(BaseWizardComponent<?, ?> baseWizardComponent){

		if (!(baseWizardComponent instanceof QuestionComponent<?, ?>)){
		
			baseWizardComponentsMap.put(baseWizardComponent.getId(), baseWizardComponent);

		} else {
			
			QuestionComponent<?, ?> questionComponent = (QuestionComponent<?, ?>) baseWizardComponent;
			
			//preparing a map of id, QuestionComponent
			for (String id: questionComponent.getIds()){
				baseWizardComponentsMap.put(id, questionComponent);
			}
			
			//preparing a list of QuestionComponent
			if (!questionList.contains(questionComponent)){
				questionList.add(questionComponent);
			}
		}
		
		if (baseWizardComponent instanceof ParentComponent){
			parentComponentList.add((ParentComponent<?,?>)baseWizardComponent);
		}
	}

	
	/**
	 * Notify this controller when some children is created or removed
	 * @param baseWizardComponent
	 */
	public void notifyChange(BaseWizardComponent<?, ?> baseWizardComponent){
		
		List<String> ids = new ArrayList<String>();
		
		for (Map.Entry<String, ?> e : baseWizardComponentsMap.entrySet()) {
			if (e.getValue() == baseWizardComponent){
				ids.add(e.getKey());
			}
		}
		
		for (String s: ids){
			baseWizardComponentsMap.remove(s);
		}
		
		register(baseWizardComponent);
	}
	
	/*
	 * SET methods
	 */
	
	/**
	 * Set question value
	 * @param questionId
	 * @param value
	 */
	public void setQuestionValue(String questionId, String value){
		QuestionComponent<?,?> q = getQuestionComponentById(questionId);
		if (q!= null){
			String oldValue = q.getValue();
			
			q.setValue(value);
			
			if (oldValue == null || !oldValue.equals(q.getValue())) {
				executeOnChangeRules(q);
			}
			
			log.debug("setQuestionValue; questionId: "+questionId+"; oldValue: "+oldValue+ ";newValue: "+value+"; question found");
		} else {
			log.debug("setQuestionValue; questionId: "+questionId+"; question not found");
		}
	}
	
	/**
	 * Set question default value
	 * @param questionId
	 * @param value
	 */
	public void setQuestionDefaultValue(String questionId, String value) {
		QuestionComponent<?,?> q = getQuestionComponentById(questionId);
		if (q!= null){
			String oldValue = q.getValue();
			
			q.setDefaultValue(value);
			
			if (oldValue == null || !oldValue.equals(q.getValue())) {
				executeOnChangeRules(q);
			}
			
			log.debug("setQuestionDefaultValue; questionId: "+questionId+"; oldValue: "+oldValue+ ";newValue: "+value+"; question found");
		} else {
			log.debug("setQuestionDefaultValue; questionId: "+questionId+"; question not found");
		}
	}
	
	/**
	 * Add question value
	 * @param questionId
	 * @param value
	 */
	public void addQuestionValue(String questionId, String value){
		QuestionComponent<?,?> q = getQuestionComponentById(questionId);
		if (q!= null){
			String oldValue = q.getValue();

			q.addValue(value);
			
			if (!oldValue.equals(value)){
				executeOnChangeRules(q);
			}
			
			log.debug("addQuestionValue; questionId: "+questionId+"; oldValue: "+oldValue+ ";newValue: "+value+"; question found");
		} else {
			log.debug("addQuestionValue; questionId: "+questionId+"; question not found");
		}
	}
	
	public void selectOption(String optionId){
		
		//try get a question from this optionId
		QuestionComponent<?,?> q = getQuestionComponentById(optionId);
		
		log.debug("selectOption('"+ optionId + "')" + q == null ? "; question not found" : "; question found");

		if(q == null) {
			return;
		}
		
		if (q instanceof QuestionWithOptionComponent<?,?>){
			QuestionWithOptionComponent<?,?> qwoc = (QuestionWithOptionComponent<?,?>)q;
			String oldId = qwoc.getSelectedOptionId();
			qwoc.selectOption(optionId);
			
			if(oldId == null || !oldId.equals(optionId)) {
				executeOnChangeRules(qwoc);
			}
		}
		else {
			log.debug("selectOption('"+ optionId + "') wrong questionType (it should be QuestionWithOptionComponent)");
		}
		
	}
	
	/**
	 * Process onChange rules
	 * @param q
	 */
	public void executeOnChangeRules(QuestionComponent<?,?> q){
		
		if (q.getWizardElement().isSetOnChangeRules()){
			RulesVisitor rules = new RulesVisitor(q, this);
			q.getWizardElement().getOnChangeRules().accept(rules);
		}
		
		log.debug("executeOnChangeRules; questionId: "+q.getId());
	}
	
	/*
	 * GET methods
	 */
	
	/**
	 * Returns the parent's list of the BaseWizardComponent
	 * @return
	 */
	public List<BaseWizardComponent<?, ?>> getParentsOf(QuestionComponent<?, ?> component){
		
		List<BaseWizardComponent<?, ?>> res = new ArrayList<BaseWizardComponent<?, ?>>();
		
		for (ParentComponent<?,?> p : parentComponentList){
			if (p.containsThisQuestionComponent(component)){
				res.add(p);
			}
		}
		
		return res;
	}
	
	/**
	 * Return a BaseWizardComponent by an id
	 * @param id
	 * @return
	 */
	public BaseWizardComponent<?, ?> getBaseWizardComponentById(String id){
		return baseWizardComponentsMap.get(id);
	}
	
	/**
	 * Return a QuestionComponent by an id 
	 */
	public QuestionComponent<?, ?> getQuestionComponentById(String id){
		BaseWizardComponent<?, ?> bwc = getBaseWizardComponentById(id);
		
		log.debug(bwc == null ? "BaseWizardComponent id="+id+" not found" : "BaseWizardComponent id="+id+" found");
		
		if (bwc != null && bwc instanceof QuestionComponent<?,?>){
			
			log.debug(bwc == null ? "BaseWizardComponent id="+id+" is not a question" : "BaseWizardComponent id="+id+" is a question");
			
			return (QuestionComponent<?,?>) bwc;
		}
		
		return null;
	}

	
	/**
	 * Return a list of QuestionComponent from BaseWizardComponent map
	 * @return
	 */
	public List<QuestionComponent<?, ?>> getQuestionComponents(){
		return questionList;
	}
	
	/**
	 * Process all answers 
	 * @param madocAnswer
	 */
	public void informMadocAnswers(MadocAnswerType madocAnswer){
		//processing answers
		for (QuestionAnswerType qat : madocAnswer.getQuestionsAnswers().getQuestionAnswer()){
			QuestionComponent<?, ?> question = getQuestionComponentById(qat.getId());
			if (question!= null){
				question.setAnswer(qat);
			}
		}
		
		processVariables(madocAnswer.getVariables());
	}
	
	/**
	 * Extract variables from VariablesAnswersType
	 * @param vat
	 */
	private void processVariables(VariablesAnswersType vat){
		Map<String, String> m = new HashMap<String, String>();
		
		if (vat!= null){
			for (VariableAnswerType v : vat.getVariableAnswer()){
				m.put(v.getVariableName(), v.getValue());
			}
		}
		
		this.variables = m;
	}
	
	
	/*
	 * Variables
	 */
	
	public void setVariable(String variableName, String value) {
		variables.put(variableName, value);
	}

	public Map<String, String> getVariables() {
		return variables;
	}

}