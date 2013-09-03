package br.gov.lexml.madoc.catalog;

import br.gov.lexml.madoc.schema.entity.CatalogItemType;

public interface CatalogEventListener {

	void versionResolved(CatalogItemType catalogItem);
	
	void itemRequested(CatalogItemType catalogItem);
	
}
