package br.gov.lexml.madoc.catalog.store.policy;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

public final class FirstMatchPolicy implements CachePolicy {

	private final List<CachePolicy> policies;
			
	public FirstMatchPolicy(CachePolicy... policies) {
		super();
		this.policies = Arrays.asList(policies);
	}

	@Override
	public PolicyDecision choosePolicy(String docUri, Long ageMillis) {
		for(CachePolicy policy : policies) {
			PolicyDecision d = policy.choosePolicy(docUri,ageMillis);
			if(d != PolicyDecision.INDIFERENT) {
				return d;
			}
		}
		return PolicyDecision.INDIFERENT;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("policies",policies)
			.toString();
	}

}
