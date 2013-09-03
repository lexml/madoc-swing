package br.gov.lexml.madoc.catalog.store;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirstSuccessful implements DocumentStore {
	
	private static final Logger log = LoggerFactory.getLogger(FirstSuccessful.class);

	private final List<DocumentStore> stores;
			
	public FirstSuccessful(DocumentStore... stores) {
		super();
		this.stores = Arrays.asList(stores);
	}

	@Override
	public InputStream getDocument(String docUri) throws IOException {
		for(DocumentStore ds : stores) {
			try {
				InputStream is = ds.getDocument(docUri);
				if(is != null) {
					return is;
				}
			} catch(IOException ex) {
				log.warn("getDocument: store = " + ds,ex);
			}
		}		
		return null;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("stores",stores)
			.toString();
	}

}
