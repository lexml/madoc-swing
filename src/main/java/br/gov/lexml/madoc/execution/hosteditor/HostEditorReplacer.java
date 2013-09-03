package br.gov.lexml.madoc.execution.hosteditor;

import br.gov.lexml.madoc.schema.Constants;
import br.gov.lexml.madoc.util.MultipleStringReplacer;

public class HostEditorReplacer {
	
	private final MultipleStringReplacer replacer;
	
	public HostEditorReplacer(){
		this(null);
	}
	
	public HostEditorReplacer(HostEditor hostEditor){
		if (hostEditor!= null){
			replacer = new MultipleStringReplacer(hostEditor.getProperties(), Constants.REPLACEMENT_PREFIX, Constants.REPLACEMENT_SUFFIX);
		} else {
			replacer = null;
		}
	}
	
	/**
	 * Replaces all occurrences of hosteditor's properties  
	 * @param src
	 * @return
	 */
	public String replaceString(String src) {
		if (replacer == null || src == null || src.equals("")) {
			return src;
		}
		return replacer.replace(src);
	}

}
