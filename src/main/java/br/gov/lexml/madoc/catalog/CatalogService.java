package br.gov.lexml.madoc.catalog;

import java.util.List;

import javax.xml.transform.URIResolver;

import br.gov.lexml.madoc.MadocException;
import br.gov.lexml.madoc.catalog.sdleg.MadocUrnResolver;

public interface CatalogService extends MadocUrnResolver {
	
	/**
	 * Returns the list of ModelInfo containing catalog's MadocDocumentType information. The models' version are the latest version<br>
	 * available in the catalog, in other words, it has the same version got by getLatestVersion(modelId).    
	 * @return
	 * @throws MadocException
	 */
	List<ModelInfo> getAvailableMadocDocumentModels() throws MadocException;

	/**
	 * Fetch all items non-obsoletes from repository to cache.
	 */
	void fetchAll() throws MadocException;
	
	/**
	 * Fetch a model from repository to cache.
	 * @param modelId
	 * @param version
	 * @throws MadocException
	 */
	void fetchModel(String modelId, String version) throws CatalogException;
	
	/**
	 * Returns the latest version of a modelID apart of override information
	 * @param modelId
	 * @return
	 * @throws MadocException
	 */
	String getLatestVersion(String modelId) throws MadocException;
	
	/**
	 * Returns the version of a modelID. The value is the same as getLatestVersion if there is no override information for modelId informed. 
	 * @param modelId
	 * @return
	 * @throws MadocException
	 */
	String getVersion(String modelId) throws MadocException;
	
	MadocDocumentModelData getMadocDocumentModel(String modelId, String modelVersion)
			throws CatalogException;

	MadocDocumentModelData getMadocDocumentModel(String modelId)
			throws CatalogException;

	MadocSkeletonModelData getMadocSkeletonModel(String modelId, String modelVersion)
			throws CatalogException;

	MadocSkeletonModelData getMadocSkeletonModel(String modelId)
			throws CatalogException;
	
	MadocLibraryModelData getMadocLibraryModel(String modelId, String modelVersion)
			throws CatalogException;

	MadocLibraryModelData getMadocLibraryModel(String modelId)
			throws CatalogException;
	
	ResourceEntityModelData getResourceModel(String modelId, String modelVersion)
			throws CatalogException;
	
	/*
	 * URIResolver methods
	 */
	
	void setURIResolver(URIResolver uriResolver);
	
	URIResolver getURIResolver();
	
	/*
	 * Event Listener methods
	 */
	
	void addCatalogEventListener(CatalogEventListener cel);
	
	void removeCatalogEventListener(CatalogEventListener cel);
	
	/*
	 * Version Override methods
	 */
	
	void addModelVersionOverride(String modelId, String modelVersion);
	
	void removeModelVersionOverride(String modelId);
	
	void clearModelVersionOverride();
}
