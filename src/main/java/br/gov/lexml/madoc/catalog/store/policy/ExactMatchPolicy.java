package br.gov.lexml.madoc.catalog.store.policy;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ExactMatchPolicy implements CachePolicy {

	private final String docUri;
	private final CachePolicy policy;
			
	public ExactMatchPolicy(String docUri,CachePolicy policy) {
		super();
		this.docUri = docUri;
		this.policy = policy;
	}

	@Override
	public PolicyDecision choosePolicy(String docUri, Long ageMillis) {
		if(docUri.equals(this.docUri)) {
			return policy.choosePolicy(docUri, ageMillis);
		}
		return PolicyDecision.INDIFERENT;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("docUri",docUri)
			.append("policy",policy)
			.toString();
	}

}
