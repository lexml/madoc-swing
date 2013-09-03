package br.gov.lexml.madoc.catalog.store.policy;

import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class RegexPolicy implements CachePolicy {

	private final Pattern pat;
		
	private final CachePolicy policy;
			
	public RegexPolicy(String pat, CachePolicy policy) {
		this.pat = Pattern.compile(pat);
		this.policy = policy;
	}

	@Override
	public PolicyDecision choosePolicy(String docUri, Long ageMillis) {
		if(pat.matcher(docUri).matches()) {
			return policy.choosePolicy(docUri, ageMillis);
		}
		return PolicyDecision.INDIFERENT;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("pat",pat.pattern())
			.append("policy",policy)
			.toString();
	}
}
