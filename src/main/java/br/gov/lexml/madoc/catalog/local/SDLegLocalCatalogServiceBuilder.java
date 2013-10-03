package br.gov.lexml.madoc.catalog.local;

import java.io.File;

import br.gov.lexml.madoc.catalog.CatalogException;
import br.gov.lexml.madoc.catalog.CatalogService;
import br.gov.lexml.madoc.catalog.CatalogServiceFactory;
import br.gov.lexml.madoc.catalog.DefaultCatalogService;
import br.gov.lexml.madoc.catalog.UriBuilder;
import br.gov.lexml.madoc.catalog.sdleg.SDLegUriBuilder;
import br.gov.lexml.madoc.catalog.store.DirectoryCatalogStore;
import br.gov.lexml.madoc.catalog.store.DocumentStore;
import br.gov.lexml.madoc.catalog.store.FirstSuccessful;
import br.gov.lexml.madoc.catalog.store.FirstSucessfulRewriter;
import br.gov.lexml.madoc.catalog.store.RegexRewriter;
import br.gov.lexml.madoc.catalog.store.Rewriter;
import br.gov.lexml.madoc.catalog.store.RewriterFilter;
import br.gov.lexml.madoc.catalog.store.URLDocumentStore;

public class SDLegLocalCatalogServiceBuilder implements CatalogServiceFactory {

	public static final String DEFAULT_CATALOG_URI = "urn:sf:sistema;sdleg:id;9999999999";
	public static final UriBuilder DEFAULT_URI_BUILDER = new SDLegUriBuilder();

	private final String versionedUrlPattern = "%s/$1-$2.xml";
	private final String unversionedUrlPattern = "%s/$1.xml";

	private final File directory;
	private final UriBuilder uriBuilder;
	private final String catalogUri;

	public SDLegLocalCatalogServiceBuilder(File directory,
			UriBuilder uriBuilder, String catalogUri) {
		super();
		this.directory = directory;
		this.uriBuilder = uriBuilder;
		this.catalogUri = catalogUri;
	}

	public SDLegLocalCatalogServiceBuilder(File directory) {
		this(directory, DEFAULT_URI_BUILDER, DEFAULT_CATALOG_URI);
	}

	@Override
	public CatalogService createCatalogService() throws CatalogException {

		//setando o diretório
		String versionedUrlPattern1 = String.format(versionedUrlPattern,
				directory.toURI().toString());

		Rewriter versionedUrnToUrl = new RegexRewriter(
				"^urn:sf:sistema;sdleg:id;([^:]*):versao;([^:]*)$",
				versionedUrlPattern1);


		//setando o diretório
		String unversionedUrlPattern1 = String.format(unversionedUrlPattern,
				directory.toURI().toString());

		Rewriter unversionedUrnToUrl = new RegexRewriter(
				"^urn:sf:sistema;sdleg:id;([^:]*)$",
				unversionedUrlPattern1);
		
		
		DocumentStore store = 
				new FirstSuccessful(
						new DirectoryCatalogStore(catalogUri, directory),
						new RewriterFilter(
								new URLDocumentStore(), new FirstSucessfulRewriter(unversionedUrnToUrl, versionedUrnToUrl)));
		
		
		//new FirstSucessfulRewriter(unversionedUrnToUrl, versionedUrnToUrl)
		

		return new DefaultCatalogService(catalogUri, store, uriBuilder);
	}

}
