package br.gov.lexml.madoc.catalog.sdleg;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.gov.lexml.madoc.catalog.CatalogException;
import br.gov.lexml.madoc.catalog.CatalogService;
import br.gov.lexml.madoc.catalog.CatalogServiceFactory;
import br.gov.lexml.madoc.catalog.DefaultCatalogService;
import br.gov.lexml.madoc.catalog.UriBuilder;
import br.gov.lexml.madoc.catalog.store.ConnectionConfigurator;
import br.gov.lexml.madoc.catalog.store.DocumentStore;
import br.gov.lexml.madoc.catalog.store.FirstSuccessful;
import br.gov.lexml.madoc.catalog.store.FirstSucessfulRewriter;
import br.gov.lexml.madoc.catalog.store.LocalCacheDocumentStore;
import br.gov.lexml.madoc.catalog.store.PatternGuardedStore;
import br.gov.lexml.madoc.catalog.store.RegexRewriter;
import br.gov.lexml.madoc.catalog.store.Rewriter;
import br.gov.lexml.madoc.catalog.store.RewriterFilter;
import br.gov.lexml.madoc.catalog.store.URIResolverAdapter;
import br.gov.lexml.madoc.catalog.store.URLDocumentStore;
import br.gov.lexml.madoc.catalog.store.policy.CachePolicy;
import br.gov.lexml.madoc.catalog.store.policy.ExactMatchPolicy;
import br.gov.lexml.madoc.catalog.store.policy.FirstMatchPolicy;
import br.gov.lexml.madoc.catalog.store.policy.FixedPolicy;
import br.gov.lexml.madoc.catalog.store.policy.RegexPolicy;
import br.gov.lexml.madoc.schema.Constants;
import br.gov.lexml.madoc.util.DefaultTrustManager;

/**
 * CatalogServiceBuild for SDLeg (Senado Federal)
 * @author lauro
 *
 */
public class SDLegCatalogServiceBuilder implements CatalogServiceFactory {

	private static final Logger log = LoggerFactory.getLogger(SDLegCatalogServiceBuilder.class);

	//SDLEG configurations
	private String versionedUrlPattern;
	private String versionLessUrlPattern;	
	private String catalogResourceName;

	private long maxCatalogAgeMillis = Constants.CATALOG_DEFAULT_MAX_AGE_MILLIS; 	

	private File cacheDir = FileUtils.getTempDirectory();

	private static final UriBuilder urnBuilder = new SDLegURNBuilder();
	
	private DocumentStore ds;
	
	private static class SDLegURNBuilder implements UriBuilder {
		@Override
		public String buildUri(String modelId, String resourceName,
				String modelVersion) {
			return resourceName + (modelVersion == null || modelVersion == "" ? "" : ":versao;" + modelVersion);
		}
		@Override
		public String toString() {
			return "SDLegURNBuilder";
		}
	}
	
	private static class MyMadocUrnResolverHolder implements MadocUrnResolverHolder {
		
		private MadocUrnResolver resolver;
		
		@Override
		public MadocUrnResolver getResolver() {
			return resolver;
		}

		public void setResolver(MadocUrnResolver resolver) {
			this.resolver = resolver;
		}			
		
	}
	
	@Override
	public CatalogService createCatalogService() throws CatalogException {
		
		final String catalogUrn = urnBuilder.buildUri(null,
				catalogResourceName, null);				
		
		log.debug("createCatalogService: catalogUrn="+catalogUrn);
		
		Rewriter unversionedUrnToUrl = new RegexRewriter("^urn:sf:sistema;sdleg:id;([^:]*)$",versionLessUrlPattern);
		
		Rewriter versionedUrnToUrl = new RegexRewriter("^urn:sf:sistema;sdleg:id;([^:]*):versao;([^:]*)$",versionedUrlPattern);
		
		MyMadocUrnResolverHolder resolverHolder = new MyMadocUrnResolverHolder(); 
		
		Rewriter madocToSdLegUrn = new MadocToSDLegRewriter(resolverHolder);
		
		Rewriter removeNoCache = new RegexRewriter("^nocache:(.*)","$1");
		
		CachePolicy policy = buildPolicy(catalogUrn);

		DocumentStore sdLegStore = new URLDocumentStore(
				new ConnectionConfigurator() {
					@Override
					public void configure(String docUri, URLConnection con) {
						con.setUseCaches(!docUri.equals(catalogUrn));
						if(con instanceof HttpsURLConnection) {
							
							HttpsURLConnection scon = (HttpsURLConnection)con;

							scon.setHostnameVerifier(new HostnameVerifier() {
								@Override
								public boolean verify(String hostname, SSLSession session) {
									return true;
								}
							});

							try {
								SSLContext ctx = SSLContext.getInstance("TLS");
								ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
								scon.setSSLSocketFactory(ctx.getSocketFactory());
							} catch (Exception e) {
								log.error(e.getLocalizedMessage(), e);
							}
							
							
						}
					}

					@Override
					public String toString() {
						return "SDLegConnectionConfigurator";
					}
				});		
		
		DocumentStore ds1 =
				new FirstSuccessful(
						new PatternGuardedStore("^http:.*",sdLegStore),
						new RewriterFilter(
								sdLegStore,
								new FirstSucessfulRewriter(unversionedUrnToUrl, versionedUrnToUrl))
				);
		DocumentStore ds2 = new FirstSuccessful(
				new RewriterFilter(ds1,removeNoCache),
				ds1
				);
		
		DocumentStore cachedSdLegStore;
		try {
			cachedSdLegStore = new LocalCacheDocumentStore(ds2,cacheDir,policy);
		} catch (IOException e) {
			throw new CatalogException("Could not create Local Cache on " + cacheDir + ": "+ e, e);
		}
		
		DocumentStore madocStore = new RewriterFilter(cachedSdLegStore,madocToSdLegUrn);
		
		ds = new FirstSuccessful(madocStore,cachedSdLegStore);
		
		DefaultCatalogService service = new DefaultCatalogService(catalogUrn, ds, urnBuilder); 
		
		resolverHolder.setResolver(service);
		
		//set default URIResolver
		service.setURIResolver(new URIResolverAdapter(ds));
		
		service.setMaxCatalogAgeMillis(maxCatalogAgeMillis);
		
		return service;
	}
	
	public DocumentStore getDocumentStore() {
		return ds;
	}

	private CachePolicy buildPolicy(String catalogUrn) {
		return new FirstMatchPolicy(		
				new RegexPolicy("^nocache:", FixedPolicy.BYPASS),
				new ExactMatchPolicy(catalogUrn,
						FixedPolicy.STORE_FIRST),						
				FixedPolicy.CACHE_FIRST);
	}

	public long getMaxCatalogAgeMillis() {
		return maxCatalogAgeMillis;
	}

	public void setMaxCatalogAgeMillis(long maxCatalogAgeMillis) {
		this.maxCatalogAgeMillis = maxCatalogAgeMillis;
	}

	public String getCatalogResourceName() {
		return catalogResourceName;
	}

	public File getCacheDir() {
		return cacheDir;
	}

	public String getVersionedUrlPattern() {
		return versionedUrlPattern;
	}

	public SDLegCatalogServiceBuilder setVersionedUrlPattern(String versionedUrlPattern) {
		this.versionedUrlPattern = versionedUrlPattern;
		return this;
	}

	public String getVersionLessUrlPattern() {
		return versionLessUrlPattern;
	}

	public SDLegCatalogServiceBuilder setVersionLessUrlPattern(String versionLessUrlPattern) {
		this.versionLessUrlPattern = versionLessUrlPattern;
		return this;
	}
		
	public static UriBuilder getUrnbuilder() {
		return urnBuilder;
	}
	
	public SDLegCatalogServiceBuilder setCacheDir(File cacheDir) {
		this.cacheDir = cacheDir;
		return this;
	}
	public SDLegCatalogServiceBuilder setCatalogResourceName(String catalogResourceName) {
		this.catalogResourceName = catalogResourceName;
		return this;
	}


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
