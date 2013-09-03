package br.gov.lexml.madoc.catalog.store;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingDocumentStoreFilter implements DocumentStore {
	private static final Logger log = LoggerFactory
			.getLogger(LoggingDocumentStoreFilter.class);

	private final String prefix;
	private final DocumentStore underlyingStore;
	private final boolean dump;
	
	public LoggingDocumentStoreFilter(String prefix, boolean dump,
			DocumentStore underlyingStore) {
		super();
		this.prefix = prefix;
		this.dump = dump;
		this.underlyingStore = underlyingStore;
	}


	@Override
	public InputStream getDocument(String docUri) throws IOException {
		log.debug(prefix + ": Requesting: " + docUri);
		// TODO Auto-generated method stub
		InputStream res;
		try {
			res = underlyingStore.getDocument(docUri);
		} catch(IOException ex) {
			log.debug(prefix + ": Exception: ",ex);
			throw ex;
		}
		if (res == null){
			log.debug(prefix + ": documentStore failed");
		} else {
			log.debug(prefix +": returning a definite value.");
			
			if (dump){
				byte[] barray = IOUtils.toByteArray(res);
				
				log.debug(prefix +": dump="+new String(barray));
				
				res = new ByteArrayInputStream(barray);
			}
		}
		
		return res;
	}

}
