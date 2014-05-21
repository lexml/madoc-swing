package br.gov.lexml.madoc.rendition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fop.apps.MimeConstants;
import org.w3c.dom.Element;

import br.gov.lexml.madoc.catalog.CatalogException;
import br.gov.lexml.madoc.catalog.CatalogService;
import br.gov.lexml.madoc.execution.CatalogEventListenerExecution;
import br.gov.lexml.madoc.execution.hosteditor.HostEditor;
import br.gov.lexml.madoc.schema.entity.CatalogItemType;
import br.gov.lexml.madoc.schema.entity.MadocAnswerType;
import br.gov.lexml.madoc.schema.entity.MadocDocumentType;
import br.gov.lexml.madoc.schema.entity.MadocReferencesAnswersType;
import br.gov.lexml.madoc.util.XMLUtil;
import br.gov.lexml.pdfa.PDFAttachmentFile;

/**
 * Printing a MadocAnswerType and MadocDocumentType pair.
 * 
 * @author lauro
 * 
 */
public class Rendition {

	private final MadocDocumentType madocDocument;
	private final MadocAnswerType madocAnswer;
	private final CatalogService catalogService;
	protected CatalogEventListenerExecution catalogEventListenerExecution;
	private HostEditor hostEditor;
	
	private TemplateProcessor processor;

	private boolean attachMadocAnswer = true;
	
	private List<PDFAttachmentFile> attachments = new ArrayList<PDFAttachmentFile>();
	
	private static final Log log = LogFactory.getLog(Rendition.class);

	public Rendition(CatalogService catalogService, MadocAnswerType madocAnswer) throws CatalogException {
		this.madocAnswer = madocAnswer;
		this.catalogService = catalogService;
		this.hostEditor = null;
		this.madocDocument = loadMadocDocumentFromAnswer(catalogService, madocAnswer);
	}

	public Rendition(CatalogService catalogService, MadocAnswerType madocAnswer, HostEditor hostEditor) throws CatalogException {
		this.madocAnswer = madocAnswer;
		this.catalogService = catalogService;
		this.hostEditor = hostEditor;
		this.madocDocument = loadMadocDocumentFromAnswer(catalogService, madocAnswer);
	}

	public Rendition(CatalogService catalogService, MadocAnswerType madocAnswer, MadocDocumentType madocDocument) {
		this(catalogService, madocAnswer, madocDocument, null);
	}

	public Rendition(CatalogService catalogService, MadocAnswerType madocAnswer, MadocDocumentType madocDocument, HostEditor hostEditor) {
		setupCatalogService(madocAnswer.getMadocReferences());
		this.madocAnswer = madocAnswer;
		this.catalogService = catalogService;
		this.hostEditor = hostEditor;
		this.madocDocument = madocDocument;
	}
	
	/**
	 * Prepare Listeners and overridden versions on CatalogService 
	 */
	private void setupCatalogService(MadocReferencesAnswersType madocReferences){
		
		if(catalogService != null) {
			catalogService.clearModelVersionOverride();
			
			//add EmptyVersionItemsIncludedFromCatalog
			if (madocReferences != null && madocReferences.getEmptyVersionItemsIncludedFromCatalog() != null) {
				for (CatalogItemType cit : madocReferences.getEmptyVersionItemsIncludedFromCatalog().getCatalogItem()){
					catalogService.addModelVersionOverride(cit.getMetadata().getId(), cit.getVersion());
				}
			}
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		catalogService.removeCatalogEventListener(catalogEventListenerExecution);
		catalogEventListenerExecution = null;
	}


	
	public MadocDocumentType loadMadocDocumentFromAnswer(CatalogService catalogService, MadocAnswerType madocAnswer) throws CatalogException{
		try {
			setupCatalogService(madocAnswer.getMadocReferences());
			
			String id = madocAnswer.getMadocReferences().getMadocDocument().getId();
			String version = madocAnswer.getMadocReferences().getMadocDocument().getVersion();
			if(version == null) {
				return catalogService.getMadocDocumentModel(id).getMadocDocument();
			}
			return catalogService.getMadocDocumentModel(id, version).getMadocDocument();
		} catch (NullPointerException e){
			throw new CatalogException("MadocDocument not found in catalogService.", e);
		}
	}
	
	public void setHostEditor(HostEditor hostEditor){
		this.hostEditor = hostEditor;
	}

	public void saveToPDF(File file) {
		saveToFile(file, MimeConstants.MIME_PDF);
	}

	public void saveToPDF(OutputStream out) {
		saveToStream(out, MimeConstants.MIME_PDF);
	}
	
	public void saveToTXT(File file) {
		saveToFile(file, MimeConstants.MIME_PLAIN_TEXT);
	}

	public void saveToTXT(OutputStream out) {
		saveToStream(out, MimeConstants.MIME_PLAIN_TEXT);
	}
	
	public void saveToRTF(File file) {
		saveToFile(file, MimeConstants.MIME_RTF);
	}
	
	public void saveToRTF(OutputStream out) {
		saveToStream(out, MimeConstants.MIME_RTF);
	}
	
	/**
	 * Returns the FOP template result
	 */
	public String getTemplateResult() {
		if (processor == null){
			processor = new TemplateProcessor(catalogService, madocAnswer, madocDocument, hostEditor);
		}
		return processor.getTemplateResult();
	}
	
	/**
	 * Returns the FOP template result as a DOM element
	 * @return
	 */
	public Element getTemplateResultAsDOMElement(){
		return XMLUtil.convertXMLStringToDocument(getTemplateResult()).getDocumentElement();
	}


	/**
	 * Generate a file processed by Velocity and FOP
	 * 
	 * @see http://xmlgraphics.apache.org/fop/1.0/embedding.html
	 * @param file
	 * @throws
	 */
	private void saveToFile(File file, String mimeConstantsMime) {
		// process FOP
		try {
			saveToStream(new FileOutputStream(file), mimeConstantsMime);
		} catch (FileNotFoundException e) {
			log.error("FileNotFoundException saving rendition to file "+file.getPath() + ": "+e.getMessage(), e);
		}
	}

	private void saveToStream(OutputStream out, String mimeConstantsMime) {
		
		// doing replacements, expansions and process Velocity
		// templateResult is a FOP pure code
		String templateResult = getTemplateResult();
        templateResult = templateResult.replaceAll("</fo:inline> </fo:inline>", "</fo:inline></fo:inline>");
		
		if (log.isDebugEnabled()) {
			log.debug("templateResult before FOPProcessor: "+templateResult);
		}
		
		// process FOP
		FOPProcessor fopp = new FOPProcessor(catalogService);
		for(PDFAttachmentFile file: attachments) {
			fopp.addAttachment(file);
		}
		fopp.setAttachMadocAnswer(attachMadocAnswer);
		fopp.processFOP(out, mimeConstantsMime, templateResult, madocAnswer);
		
	}
	
	public void addAttachment(PDFAttachmentFile file) {
		attachments.add(file);
	}
	
	public void setAttachMadocAnswer(boolean attachMadocAnswer) {
		this.attachMadocAnswer = attachMadocAnswer;
	}
	
	public boolean isAttachMadocAnswer() {
		return attachMadocAnswer;
	}
	
}