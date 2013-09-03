package br.gov.lexml.madoc.rendition;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import br.gov.lexml.madoc.execution.hosteditor.HostEditor;
import br.gov.lexml.madoc.execution.hosteditor.HostEditorReplacer;
import br.gov.lexml.madoc.schema.Constants;
import br.gov.lexml.madoc.schema.entity.MadocDocumentType;
import br.gov.lexml.madoc.schema.entity.TemplateType;
import br.gov.lexml.madoc.util.MultipleStringReplacer;

class VelocityTemplateProcessorLanguageExpansion {
	
	private final MadocDocumentType madocDocument;
	private final HostEditor hostEditor;
	
	VelocityTemplateProcessorLanguageExpansion(MadocDocumentType madocDocument, HostEditor hostEditor){
		this.madocDocument = madocDocument;
		this.hostEditor = hostEditor;
	}
	
	/**
	 * Do the language expansion
	 * @param templateString
	 * @param useFindHintTag
	 * @return
	 */
	String doExpansions(String templateString){
		return cleanEmptyTemplateReplacers(
				languageExtensionReplacements(
					hostEditorReplacements(
						templatesReplacements(templateString))));
	}
	
	
	/*
	 * Transformation methods
	 */
	
	/**
	 * Clean replacers weren't used by template
	 * @param template
	 * @return
	 */
	private String cleanEmptyTemplateReplacers(String template){
		return template.replaceAll("@@.*?(@@)", "");
	}
	
	/**
	 * For each template from madocDocument.getTemplates(), replace content in templateString
	 * @param templateString
	 * @param useFindHintTag
	 * @return
	 */
	private String templatesReplacements(String templateString){
		Map<String, String> replacements = new HashMap<String, String>();
		
		for (TemplateType template : madocDocument.getTemplates().getTemplate()) {
			
			String replaceName = template.getReplaceName();
			replaceName = StringUtils.removeStart(StringUtils.removeEnd(replaceName, Constants.REPLACEMENT_SUFFIX), Constants.REPLACEMENT_PREFIX);
			
			//removing line breaks (Velocity doesn't like them)
			String content = "";
			if (template!= null && template.getContent()!= null){
				content = template.getContent().trim().replaceAll("[\\r\\n]", "");
			}
			
			replacements.put(replaceName, content);
		}
		
		// do replacements
		MultipleStringReplacer r = new MultipleStringReplacer(replacements, Constants.REPLACEMENT_PREFIX, Constants.REPLACEMENT_SUFFIX);
		return r.replace(templateString);
	}
	
	/**
	 * Uses $.*$ as prefix to replace contents from HostEditor
	 * @param templateString
	 * @return
	 */
	private String hostEditorReplacements(String templateString){
		HostEditorReplacer her = new HostEditorReplacer(hostEditor);
		return her.replaceString(templateString);
	}
	
	/**
	 * Translate the Velocity language expansion
	 * @param templateString
	 * @return
	 */
	private String languageExtensionReplacements(String templateString){
		Map<String, String> replacements = new HashMap<String, String>();
		replacements.put("#&amp;&amp;", "&&");
		replacements.put("#and", "&&");
		replacements.put("#&&", "&&");
		replacements.put("#||", "||");
		replacements.put("#or", "||");
		replacements.put("#!", "!");
		replacements.put("#not", "!");
		
		return StringUtils.replaceEachRepeatedly(templateString, 
				replacements.keySet().toArray(new String[0]), 
				replacements.values().toArray(new String[0]));
	}
}
