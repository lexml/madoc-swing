package br.gov.lexml.madoc.catalog.store;

import java.io.IOException;
import java.io.InputStream;

public interface DocumentStore {
	
	/**
	 * Returns a InputStream of a docUri
	 * @param docUri
	 * @return
	 * @throws IOException
	 */
	InputStream getDocument(String docUri) throws IOException;

}
