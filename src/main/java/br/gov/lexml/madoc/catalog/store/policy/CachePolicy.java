package br.gov.lexml.madoc.catalog.store.policy;

public interface CachePolicy {
	PolicyDecision choosePolicy(String docUri, Long ageMillis);
}
