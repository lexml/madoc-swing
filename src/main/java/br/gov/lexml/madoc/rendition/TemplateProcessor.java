package br.gov.lexml.madoc.rendition;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.gov.lexml.madoc.MadocException;
import br.gov.lexml.madoc.catalog.CatalogService;
import br.gov.lexml.madoc.catalog.MadocSkeletonModelData;
import br.gov.lexml.madoc.execution.hosteditor.HostEditor;
import br.gov.lexml.madoc.schema.Constants;
import br.gov.lexml.madoc.schema.entity.MadocAnswerType;
import br.gov.lexml.madoc.schema.entity.MadocDocumentType;
import br.gov.lexml.madoc.util.XMLUtil;

class TemplateProcessor {

	private static final Logger log = LoggerFactory.getLogger(TemplateProcessor.class);
	
	private final MadocDocumentType madocDocument;
	private final MadocAnswerType madocAnswer;
	private final CatalogService catalogService;
	private final HostEditor hostEditor;

	private String velocityResult;
	
	TemplateProcessor(CatalogService catalogService, MadocAnswerType madocAnswer, MadocDocumentType madocDocument) {
		this(catalogService, madocAnswer, madocDocument, null);
	}

	TemplateProcessor(CatalogService catalogService, MadocAnswerType madocAnswer, MadocDocumentType madocDocument, HostEditor hostEditor) {
		this.catalogService = catalogService;
		this.hostEditor = hostEditor;
		
		this.madocAnswer = madocAnswer;
		
		this.madocDocument = madocDocument;
	}
	
	/**
	 * Process a Velocity template. Returns a FOP pure code.
	 * 
	 * @return a final FO code processed by Velocity
	 */
	public String getTemplateResult() {

		if (velocityResult == null) {

			// get the skeleton from madocDocument
			String finalTemplate = getSkeletonContent();
			
			//REPLACEMENTS
			VelocityTemplateProcessorLanguageExpansion vtple = new VelocityTemplateProcessorLanguageExpansion(madocDocument, hostEditor);
			finalTemplate = vtple.doExpansions(finalTemplate);
			
			if (log.isDebugEnabled()){
				log.debug("finalTemplate: " + finalTemplate);
			}
			
			// processing velocity
			velocityResult = getVelocityResult(finalTemplate);
		}
		
		return velocityResult;
		
		/*
		try {
			FileUtils.write(new File("/tmp/saida_final.txt"), finalTemplate);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	

	/**
	 * Returns skeleton content
	 * @return
	 */
	private String getSkeletonContent() {
		try {
			
			MadocSkeletonModelData modelData;
			if (madocDocument.getTemplates().isSetMadocSkeletonVersion()){
				modelData = catalogService.getMadocSkeletonModel(
								madocDocument.getTemplates().getMadocSkeletonId(), 
								madocDocument.getTemplates().getMadocSkeletonVersion());
			} else {
				modelData = catalogService.getMadocSkeletonModel(
								madocDocument.getTemplates().getMadocSkeletonId());
			}
			
			if (modelData == null){
				throw new MadocException("Madoc Skeleton does not exist or has not been found.");
			}
			
			String skeleton = XMLUtil.xmlToString(modelData.getMadocSkeleton().getSkeleton().getAny());
			
			if (log.isDebugEnabled()){
				log.debug("Skeleton: " + skeleton);
			}
			
			return skeleton;
		} catch (MadocException e) {
			throw new RuntimeException("Error loading Madoc Skeleton. " + e.getMessage(), e);
		}
	}
	
	/**
	 * Returns an FO code from a template
	 * @param template an string that contains skeleton and all templates from current MadocDocument
	 * @return final FO code
	 */
	private String getVelocityResult(String template) {
		
		VelocityEngine ve = new VelocityEngine();

	    ve.setProperty( RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
	    	      "org.apache.velocity.runtime.log.Log4JLogChute" );
	    ve.setProperty("runtime.log.logsystem.log4j.logger", getClass().getName());
	    	    
		ve.init();

		VelocityContext ctx = getContextFromMadoc();
		VelocityExtension madoc = (VelocityExtension) ctx.get("madoc");
		madoc.setVelocityEngine(ve);
		
	    StringWriter w = new StringWriter();
		ve.evaluate(ctx, w, "defaultTemplate", template);
		String result = w.toString();

		if (StringUtils.isEmpty(result)){
			return "";
		}
		
		result = result.replaceAll("\\s{2,}", " ");
		result = result.replaceAll("\\s([.,;:!?])", "$1");
		
		log.debug("getVelocityResult: " + result);

		return result;
	}

	/**
	 * Return a Velocity context based on madocDocument and madocAnswer
	 * 
	 * @return
	 */
	private VelocityContext getContextFromMadoc() {

		VelocityContext velocityContext = new VelocityContext();

		ContextCollection contextCollection = new ContextCollection(madocAnswer, madocDocument);
		
		//put datasets
		velocityContext.put("ds", contextCollection.getDataSets());
		
		//put metadata
		velocityContext.put("metadata", contextCollection.getMetadataCollectionVale());
		
		// put master objects
		velocityContext.put("MadocDocument", madocDocument);
		velocityContext.put("MadocAnswer", madocAnswer);
		
		// put answers in a map by id
		velocityContext.put("answers", contextCollection.getAnswersMap());
		
		// put answers options in a map by id
		velocityContext.put("options", contextCollection.getAnswersOptionsMap());
		
		// put variables by name
		velocityContext.put("vars", contextCollection.getVariablesMap());

		// put questions in a map by id
		velocityContext.put("questions", contextCollection.getQuestionsMap());

		// put questionsOptions in a map by id
		velocityContext.put("questionsOptions", contextCollection.getQuestionsOptionsMap());

		// put constants
		Map<String, String> constants = new HashMap<String, String>();
		constants.put("DATE_FORMAT", Constants.DATE_FORMAT);
		constants.put("FULL_DATE_FORMAT", Constants.FULL_DATE_FORMAT);
		constants.put("SPLIT_TOKEN_VALUES", Constants.SPLIT_TOKEN_VALUES);
		velocityContext.put("consts", constants);

		// put util
		velocityContext.put("madoc", new VelocityExtension(contextCollection, velocityContext));
		
		return velocityContext;
	}
}

