package br.gov.lexml.madoc.catalog.store;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URLDocumentStore implements DocumentStore {

	private static final Logger log = LoggerFactory.getLogger(URLDocumentStore.class);
	
	private final ConnectionConfigurator configurator;
	
	public URLDocumentStore(ConnectionConfigurator configurator) {
		this.configurator = configurator;
	}
	
	public URLDocumentStore() {
		this(new DefaultConfigurator());		
	}
	
	@Override
	public InputStream getDocument(String docUri) throws IOException {
		if(log.isDebugEnabled()) {
		log.debug("getDocument: docUri = " + docUri);
		}				
		URL u = new URL(docUri);
		if(log.isDebugEnabled()) {
			log.debug("getDocument: url = " + u);
		}
		URLConnection c = u.openConnection();
		configurator.configure(docUri,c);			
		if(log.isDebugEnabled()) {
			log.debug("getDocument: c = " + c);
		}
		InputStream is = c.getInputStream();
		if(log.isDebugEnabled()) {
			log.debug("getDocument: result = " + is);
		}
		return is;
	}	

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("configurator",configurator)
			.toString();
	}
}
