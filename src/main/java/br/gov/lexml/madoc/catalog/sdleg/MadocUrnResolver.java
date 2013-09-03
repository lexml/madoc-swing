package br.gov.lexml.madoc.catalog.sdleg;

public interface MadocUrnResolver {
	String resolveMadocToUrn(String modelId, String version);

}
