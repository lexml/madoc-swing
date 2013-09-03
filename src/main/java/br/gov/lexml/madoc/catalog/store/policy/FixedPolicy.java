package br.gov.lexml.madoc.catalog.store.policy;

import org.apache.commons.lang3.builder.ToStringBuilder;

public final class FixedPolicy implements CachePolicy {

	public static final CachePolicy INDIFERENT = new FixedPolicy(PolicyDecision.INDIFERENT);
	public static final CachePolicy CACHE_FIRST = new FixedPolicy(PolicyDecision.CACHE_FIRST);
	public static final CachePolicy STORE_FIRST = new FixedPolicy(PolicyDecision.STORE_FIRST);
	public static final CachePolicy BYPASS = new FixedPolicy(PolicyDecision.BYPASS);
	
	private final PolicyDecision decision;
	
	private FixedPolicy(PolicyDecision decision) {
		this.decision = decision;
	}
	
	@Override
	public PolicyDecision choosePolicy(String docUri, Long ageMillis) {
		return decision;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("decision",decision)
			.toString();
	}
}
