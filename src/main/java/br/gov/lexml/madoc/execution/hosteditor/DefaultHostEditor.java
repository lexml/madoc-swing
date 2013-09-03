package br.gov.lexml.madoc.execution.hosteditor;

import java.io.File;
import java.util.Map;

public class DefaultHostEditor implements HostEditor {

	private Map<String, String> properties;
	private File spellcheckBaseDir;
	
	@Override
	public Map<String, String> getProperties() {
		return properties;
	}
	
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	@Override
	public File getSpellcheckBaseDir() {
		return spellcheckBaseDir;
	}

	public void setSpellcheckBaseDir(File spellcheckBaseDir) {
		this.spellcheckBaseDir = spellcheckBaseDir;
	}
	

}
