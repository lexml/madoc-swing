package br.gov.lexml.madoc.execution.hosteditor;

import java.io.File;
import java.util.Map;

public interface HostEditor {

	Map<String, String> getProperties();
	
	File getSpellcheckBaseDir();
	
}
