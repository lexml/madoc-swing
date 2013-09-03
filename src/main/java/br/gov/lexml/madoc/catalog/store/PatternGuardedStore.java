package br.gov.lexml.madoc.catalog.store;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class PatternGuardedStore implements DocumentStore {

	private final Pattern pat;
	
	private final DocumentStore ds;
	
	public PatternGuardedStore(String pattern, DocumentStore ds) {
		super();
		this.pat = Pattern.compile(pattern);
		this.ds = ds;
	}

	@Override
	public InputStream getDocument(String docUri) throws IOException {
		if(pat.matcher(docUri).matches()) {
			return ds.getDocument(docUri);
		} else {
			return null;
		}		
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("pattern",pat.pattern())
			.append("ds",ds)
			.toString();
	}
}
