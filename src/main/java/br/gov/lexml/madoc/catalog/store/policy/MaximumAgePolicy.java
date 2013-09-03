package br.gov.lexml.madoc.catalog.store.policy;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class MaximumAgePolicy implements CachePolicy {

	private final long maxAgeMillis;
	
	private final CachePolicy underAgePolicy;
	
	private final CachePolicy afterAgePolicy;
	
	private final CachePolicy missPolicy;
	
		
	public MaximumAgePolicy(long maxAgeMillis, CachePolicy underAgePolicy,
			CachePolicy afterAgePolicy, CachePolicy missPolicy) {
		super();
		this.maxAgeMillis = maxAgeMillis;
		this.missPolicy = missPolicy;
		this.underAgePolicy = underAgePolicy;
		this.afterAgePolicy = afterAgePolicy;
	}
	
	public MaximumAgePolicy(long maxAgeMillis) {
		this(maxAgeMillis,FixedPolicy.CACHE_FIRST,FixedPolicy.STORE_FIRST,
				FixedPolicy.STORE_FIRST);
	}

	@Override
	public PolicyDecision choosePolicy(String docUri, Long ageMillis) {
		if(ageMillis == null) {
			return missPolicy.choosePolicy(docUri, ageMillis);
		} else if(ageMillis <= maxAgeMillis) {
			return underAgePolicy.choosePolicy(docUri, ageMillis);
		} else {
			return afterAgePolicy.choosePolicy(docUri, ageMillis);
		}
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("maxAgeMillis",maxAgeMillis)
			.append("underAgePolicy",underAgePolicy)
			.append("afterAgePolicy",afterAgePolicy)
			.append("missPolicy",missPolicy)
			.toString();
	}
}
