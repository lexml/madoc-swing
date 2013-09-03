package br.gov.lexml.madoc.execution;

import java.util.Set;
import java.util.TreeSet;

import br.gov.lexml.madoc.catalog.DefaultCatalogEventListener;
import br.gov.lexml.madoc.schema.comparators.CatalogItemTypeComparator;
import br.gov.lexml.madoc.schema.entity.CatalogItemType;

public class CatalogEventListenerExecution extends DefaultCatalogEventListener {

	private final Set<CatalogItemType> items = new TreeSet<CatalogItemType>(new CatalogItemTypeComparator());
	
	/**
	 * Add CatalogItem with no entries
	 */
	@Override
	public void versionResolved(CatalogItemType catalogItem) {
		CatalogItemType citCloned = (CatalogItemType)catalogItem.clone();
		citCloned.getMetadata().getEntry().clear();
		
		items.add(citCloned);
	}
	
	public Set<CatalogItemType> getItemsVersionResolved(){
		return items;
	}
	
}
