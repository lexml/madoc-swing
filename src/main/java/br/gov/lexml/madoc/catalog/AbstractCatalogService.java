package br.gov.lexml.madoc.catalog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.gov.lexml.madoc.schema.entity.CatalogItemType;

public abstract class AbstractCatalogService implements CatalogService{

	private final Set<CatalogEventListener> eventListenersSet = new HashSet<CatalogEventListener>(); 
	
	protected static abstract class EventDispatcher {
		public abstract void dispatch(CatalogEventListener listener);
	}
	
	private final Map<String, String> modelVersionOverrides = new HashMap<String,String>();

	
	@Override
	public MadocSkeletonModelData getMadocSkeletonModel(String modelId)
			throws CatalogException {
		return getMadocSkeletonModel(modelId, null);

	}
	
	@Override
	public MadocDocumentModelData getMadocDocumentModel(String modelId) throws CatalogException {
		return getMadocDocumentModel(modelId, null);
	}
	
	@Override
	public MadocLibraryModelData getMadocLibraryModel(String modelId)
			throws CatalogException {
		return getMadocLibraryModel(modelId, null);
	}

	
	// Version Override
	
	@Override
	public void addModelVersionOverride(String modelId, String modelVersion) {
		modelVersionOverrides.put(modelId, modelVersion);
	}

	@Override
	public void removeModelVersionOverride(String modelId) {
		modelVersionOverrides.remove(modelId);
	}

	@Override
	public void clearModelVersionOverride() {
		modelVersionOverrides.clear();
	}
	
	/**
	 * Returns the overridden version of the item or the current version if no override information is present. 
	 * @param item
	 * @return
	 */
	protected String getVersionFor(CatalogItemType item, boolean alwaysLatest) {
		if (alwaysLatest){
			return item.getVersion();
		}
		String versionOverride = getVersionOverrideFor(item.getMetadata().getId());
		if(versionOverride == null) {
			return item.getVersion();
		} 
		return versionOverride;
	}
	
	/**
	 * Returns the overridden version of a modelId or null if no override information is present.
	 * @param modelId
	 * @return
	 */
	private String getVersionOverrideFor(String modelId) {
		if (modelVersionOverrides.containsKey(modelId)) {
			return modelVersionOverrides.get(modelId);
		} 
		return null;		
	}
	
	// Listeners
	
	@Override
	public synchronized void addCatalogEventListener(CatalogEventListener cel){
		eventListenersSet.add(cel);
	}
	
	@Override
	public synchronized void removeCatalogEventListener(CatalogEventListener cel){
		eventListenersSet.remove(cel);
	}
	
	protected void dispatchEvent(EventDispatcher dispatcher) {
		final Set<CatalogEventListener> listeners = new HashSet<CatalogEventListener>();
		synchronized(this) {
			listeners.addAll(this.eventListenersSet);
		}
		for(CatalogEventListener l : listeners) {
			dispatcher.dispatch(l);
		}
	}
	
}
