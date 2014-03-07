package br.gov.lexml.madoc.catalog;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.transform.URIResolver;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.gov.lexml.madoc.MadocException;
import br.gov.lexml.madoc.catalog.store.DocumentStore;
import br.gov.lexml.madoc.schema.Constants;
import br.gov.lexml.madoc.schema.entity.CatalogItemType;
import br.gov.lexml.madoc.schema.entity.CatalogType;
import br.gov.lexml.madoc.schema.entity.MadocDocumentType;
import br.gov.lexml.madoc.schema.entity.MadocLibraryType;
import br.gov.lexml.madoc.schema.entity.MadocSkeletonType;
import br.gov.lexml.madoc.schema.entity.MetadataType;
import br.gov.lexml.madoc.schema.entity.ResourceEntity;
import br.gov.lexml.madoc.schema.parser.ParseException;
import br.gov.lexml.madoc.schema.parser.SchemaParser;

/**
 * It is not thread safe.
 * @author lauroa
 *
 */
public class DefaultCatalogService extends AbstractCatalogService implements CatalogService {

	private static final Logger log = LoggerFactory.getLogger(DefaultCatalogService.class);

	private final String catalogUri;
	private final DocumentStore store;
	private final UriBuilder uriBuilder;
	private URIResolver uriResolver;
	
	private CatalogType catalog;
	private long catalogTime;
	private long maxCatalogAgeMillis = Constants.CATALOG_DEFAULT_MAX_AGE_MILLIS; 
	
	public DefaultCatalogService(String catalogUri, DocumentStore store,
			UriBuilder uriBuilder) {
		super();
		this.catalogUri = catalogUri;
		this.store = store;
		this.uriBuilder = uriBuilder;
	}

	@Override
	public void setURIResolver(URIResolver uriResolver){
		this.uriResolver = uriResolver;
	}
	
	@Override
	public URIResolver getURIResolver() {
		return uriResolver;
	}
	
	@Override
	public void fetchAll() throws MadocException {
		log.debug("Fetching all avaiable models");
		
		// preparing list of ModelInfo without EventDispacher info
		List<ModelInfo> models = new ArrayList<ModelInfo>();
		for (CatalogItemType cit : createCatalogItemTypeSet()){
			if (!cit.isObsolete()){
				models.add(
						new ModelInfo(
							uriBuilder.buildUri(cit.getMetadata().getId(), cit.getResourceName(), cit.getVersion()), 
							cit.getMetadata().getId(), 
							cit.getVersion(), 
							cit.getMetadata(),
							cit));
			}
		}
		
		for (ModelInfo mi : models){
			try {
				log.debug("Fetching model id="+mi.getModelId()+" version="+mi.getModelVersion());

				store.getDocument(mi.getUri());
			} catch (IOException e) {
				CatalogException ce = new CatalogException(
						"IOException ocurred while obtaining model (" + mi.getModelId() + "/" + mi.getModelVersion() + ")", e);
				log.error(ce.getMessage(), ce);
				throw ce;
			}
		}
	}
	
	@Override
	public void fetchModel(String modelId, String version)
			throws CatalogException {
		ModelInfo mi = getCatalogItemModelInfo(modelId, version);
		
		if (mi!= null){
			try {
				log.debug("Fetching model id="+modelId+" version="+version);

				store.getDocument(mi.getUri());
			} catch (IOException e) {
				CatalogException ce = new CatalogException(
						"IOException ocurred while obtaining model (" + modelId + "/" + version + ")", e);
				log.error(ce.getMessage(), ce);
				throw ce;
			}
		}
	}
	
	@Override
	public List<ModelInfo> getAvailableMadocDocumentModels() throws MadocException {

		CatalogType catalog;
		try {
			catalog = getCatalog();
		} catch (IOException e) {
			CatalogException ce = new CatalogException(
					"Error openning catalog " + e.getMessage(), e);
			log.error(ce.getMessage(), ce);
			throw ce;
		}

		List<ModelInfo> models = new ArrayList<ModelInfo>(catalog.getMadocDocuments().getMadocDocument().size());
		for (CatalogItemType item : catalog.getMadocDocuments().getMadocDocument()) {
			
			String id = item.getMetadata().getId();
			String version = item.getVersion();
			
			String uri = uriBuilder.buildUri(id, item.getResourceName(), version);
			
			ModelInfo mi = new ModelInfo(uri, id, version, item.getMetadata(), item);
			models.add(mi);
		}
		return models;
	}

	@Override
	public String resolveMadocToUrn(String modelId, String version) {
		try {
			ModelInfo modelInfo = getCatalogItemModelInfo(modelId, version);
			if (modelInfo != null) {
				return modelInfo.getUri();
			}
		} catch (MadocException ex) {
			log.warn("Erro durante a resolução de modelId = " + modelId + ", version = " + version, ex);
		}
		return null;
	}

	@Override
	public MadocDocumentModelData getMadocDocumentModel(final String modelId,
			final String modelVersion) throws CatalogException {

		try{
			// getting modelInfo
			ModelInfo modelInfo = getCatalogItemModelInfo(modelId, modelVersion);
			if (modelInfo == null){
				return null;
			}

			// getting modelData
			InputStream is = store.getDocument(modelInfo.getUri());
			MadocDocumentType doc = SchemaParser.loadMadocDocument(is, uriResolver);
			return new MadocDocumentModelData(modelInfo.getUri(), modelInfo.getModelId(), modelInfo.getModelVersion(), modelInfo.getMetadata(), doc);
				
		} catch (IOException e) {
			CatalogException ce = new CatalogException(
					"IOException ocurred while obtaining model (" + modelId + "/" + modelVersion + ")", e);
			log.error(ce.getMessage(), ce);
			throw ce;
	
		} catch (ParseException e) {
			CatalogException ce = new CatalogException(
				"ParseException ocurred while obtaining model (" + modelId + "/" + modelVersion + ")", e);
			log.error(ce.getMessage(), ce);
			throw ce;
		}
	}
	
	@Override
	public MadocSkeletonModelData getMadocSkeletonModel(final String modelId, final String modelVersion) throws CatalogException {
		try {
			// getting modelInfo
			ModelInfo modelInfo = getCatalogItemModelInfo(modelId, modelVersion);
			if (modelInfo == null){
				return null;
			}

			// getting modelData
			InputStream is = store.getDocument(modelInfo.getUri());
			MadocSkeletonType doc = SchemaParser.loadMadocSkeleton(is, uriResolver);
			return new MadocSkeletonModelData(modelInfo.getUri(), modelInfo.getModelId(),
				modelInfo.getModelVersion(), modelInfo.getMetadata(), doc);
			
		} catch (IOException e) {
			CatalogException ce = new CatalogException(
					"IOException ocurred while obtaining model (" + modelId + "/" + modelVersion + ")", e);
			log.error(ce.getMessage(), ce);
			throw ce;
		} catch (ParseException e) {
			CatalogException ce = new CatalogException(
					"ParseException ocurred while obtaining model (" + modelId + "/" + modelVersion + ")", e);
				log.error(ce.getMessage(), ce);
				throw ce;
		}
	}
	
	@Override
	public MadocLibraryModelData getMadocLibraryModel(String modelId,
			String modelVersion) throws CatalogException {
		try {
			// getting modelInfo
			ModelInfo modelInfo = getCatalogItemModelInfo(modelId, modelVersion);
			if (modelInfo == null){
				return null;
			}

			// getting modelData
			InputStream is = store.getDocument(modelInfo.getUri());
			MadocLibraryType doc = SchemaParser.loadMadocLibrary(is, uriResolver);
			return new MadocLibraryModelData(modelInfo.getUri(), modelInfo.getModelId(),
				modelInfo.getModelVersion(), modelInfo.getMetadata(), doc);
			
		} catch (IOException e) {
			CatalogException ce = new CatalogException(
					"IOException ocurred while obtaining model (" + modelId + "/" + modelVersion + ")", e);
			log.error(ce.getMessage(), ce);
			throw ce;
		} catch (ParseException e) {
			CatalogException ce = new CatalogException(
					"ParseException ocurred while obtaining model (" + modelId + "/" + modelVersion + ")", e);
				log.error(ce.getMessage(), ce);
				throw ce;
		}
	}
	
	@Override
	public ResourceEntityModelData getResourceModel(final String modelId, final String modelVersion) throws CatalogException{
		try {
			// getting modelInfo
			ModelInfo modelInfo = getCatalogItemModelInfo(modelId, modelVersion);
			if (modelInfo == null){
				return null;
			}

			// getting Model
			ResourceEntity resource = new ResourceEntity(
					store.getDocument(modelInfo.getUri()), 
					modelInfo.getCatalogItem().getMimetype(),
					modelInfo.getCatalogItem().getFileSuffix());

			return new ResourceEntityModelData(modelInfo.getUri(), modelInfo.getModelId(),
					modelInfo.getModelVersion(), modelInfo.getMetadata(), resource);
			
		} catch (IOException e) {
			CatalogException ce = new CatalogException(
					"IOException ocurred while obtaining model (" + modelId + "/" + modelVersion + ")", e);
			log.error(ce.getMessage(), ce);
			throw ce;
		}
	}

	@Override
	public String getLatestVersion(String modelId) throws CatalogException {
		ModelInfo modelInfo = getCatalogItemModelInfo(modelId, null, true);
		if (modelInfo == null){
			return null;
		}
		return modelInfo.getModelVersion();
	}
	
	@Override
	public String getVersion(String modelId) throws MadocException {
		ModelInfo modelInfo = getCatalogItemModelInfo(modelId, null, false);
		if (modelInfo == null){
			return null;
		}
		return modelInfo.getModelVersion();
	}
	
	/**
	 * Returns catalog
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	private synchronized CatalogType getCatalog() throws IOException, ParseException {
		if(catalog !=null && catalogTime >= (System.currentTimeMillis() - maxCatalogAgeMillis)) {
			return catalog;
		}
		InputStream is = store.getDocument(catalogUri);
		if (is == null) {
			throw new ParseException(this + ": Catalog cannot be obtained!");
		}
		catalog =  SchemaParser.loadCatalog(is);
		
		catalogTime = System.currentTimeMillis();
		
		return catalog;
	}
	
	/**
	 * Resolves modelId and modelVersion to a ModelInfo based on Catalog and override information 
	 * @param modelId
	 * @param modelVersion
	 * @return
	 */
	private ModelInfo getCatalogItemModelInfo(final String modelId, final String modelVersion) throws CatalogException{
		return getCatalogItemModelInfo(modelId, modelVersion, false);
	}
	
	/**
	 * Create a new set of CatalogItemType with all items in catalog.
	 * @return
	 * @throws CatalogException
	 */
	private Set<CatalogItemType> createCatalogItemTypeSet() throws CatalogException{
		try{
			CatalogType catalog = getCatalog();
	
			Set<CatalogItemType> items = new HashSet<CatalogItemType>();
			
			// preparing items
			if (catalog.isSetMadocDocuments() && catalog.getMadocDocuments().isSetMadocDocument()){
				items.addAll(catalog.getMadocDocuments().getMadocDocument());
			}
			if (catalog.isSetMadocSkeletons() && catalog.getMadocSkeletons().isSetMadocSkeleton()){
				items.addAll(catalog.getMadocSkeletons().getMadocSkeleton());
			}
			if (catalog.isSetMadocLibraries() && catalog.getMadocLibraries().isSetMadocLibrary()){
				items.addAll(catalog.getMadocLibraries().getMadocLibrary());
			}
			if (catalog.isSetResources() && catalog.getResources().isSetResource()){ 
				items.addAll(catalog.getResources().getResource());
			}
			return items;
			
		} catch (ParseException e) {
			CatalogException ce = new CatalogException(
					"ParseException ocurred while creating CatalogItemType set", e);
			log.error(ce.getMessage(), ce);
			throw ce;
		} catch (IOException e) {
			CatalogException ce = new CatalogException(
					"ParseException ocurred while creating CatalogItemType set", e);
			log.error(ce.getMessage(), ce);
			throw ce;
		}
	}
	
	/**
	 * Resolves modelId and modelVersion to a ModelInfo based on Catalog and override information. <br>
	 * When alwaysLatest is false, it resolves to the  
	 *   
	 * @param modelId
	 * @param modelVersion
	 * @return
	 */
	private ModelInfo getCatalogItemModelInfo(final String modelId, final String modelVersion, boolean alwaysLatest) throws CatalogException{
				
		Set<CatalogItemType> items = createCatalogItemTypeSet();
			
		//traversing items
		for (CatalogItemType originalItem : items) {
			
			MetadataType md = originalItem.getMetadata();
				
			if (md.getId().equals(modelId)) {
					
				final CatalogItemType item = (CatalogItemType) originalItem.clone();
				
				final String effectiveVersion;
					
				if (modelVersion != null) {
					effectiveVersion = modelVersion;
					item.setVersion(effectiveVersion);
				} else {
					effectiveVersion = getVersionFor(item, alwaysLatest);
					item.setVersion(effectiveVersion);
						
					//dispatching event
					dispatchEvent(new EventDispatcher() {
						@Override
						public void dispatch(CatalogEventListener listener) {
							listener.versionResolved(item);							
						}
					});
				}
					
				//dispatching event
				dispatchEvent(new EventDispatcher() {
					@Override
					public void dispatch(CatalogEventListener listener) {
						listener.itemRequested(item);							
					}
				});

				String uri = uriBuilder.buildUri(modelId, item.getResourceName(), effectiveVersion);
					
				ModelInfo modelInfo = new ModelInfo(uri, modelId, effectiveVersion, md, item);
				modelInfo.setCatalogItem(item);
					
				return modelInfo;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("catalogUri", catalogUri)
				.append("store", store).append("uriBuilder", uriBuilder)
				.toString();
	}

	public long getMaxCatalogAgeMillis() {
		return maxCatalogAgeMillis;
	}

	public void setMaxCatalogAgeMillis(long maxCatalogAgeMillis) {
		this.maxCatalogAgeMillis = maxCatalogAgeMillis;
	}
	
	
	
}