package br.gov.lexml.madoc.catalog.store;

import java.net.URLConnection;

public interface ConnectionConfigurator {
	void configure(String docUri, URLConnection con);
}
