package br.gov.lexml.madoc.catalog.store;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RewriterFilter permite a reescrita de uma URI.
 * O rewriter filter ao receber uma requisição de documento com uma certa URI, primeiro ele
 * faz a reescrita dessa URI usando o rewriter e se essa reescrita for bem sucedida, ele
 * repassa a URI reescrita ao documentstore subjacente. 
 * @author lauroa
 *
 */
public class RewriterFilter implements DocumentStore {
	private static final Logger log = LoggerFactory.getLogger(RewriterFilter.class);

	private final Rewriter rewriter;
			
	private final DocumentStore store;
		
	public RewriterFilter(DocumentStore store,Rewriter rewriter) {
		super();
		this.rewriter = rewriter;
		this.store = store;
	}

	@Override
	public InputStream getDocument(String docUri) throws IOException {
		String newDocUri = rewriter.rewriteUri(docUri);
		if(log.isDebugEnabled()) {
			log.debug("getDocument: docUri = " + docUri + ", newDocUri = " + newDocUri);
		}
		if(newDocUri != null) {
			return store.getDocument(newDocUri);
		} else {
			return null;
		}
		
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("rewriter",rewriter)
			.append("store",store)
			.toString();
	}
}
