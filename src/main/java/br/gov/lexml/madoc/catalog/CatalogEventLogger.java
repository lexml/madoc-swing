package br.gov.lexml.madoc.catalog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.gov.lexml.madoc.schema.entity.CatalogItemType;

public class CatalogEventLogger implements CatalogEventListener {

	private static final Logger log = LoggerFactory.getLogger(CatalogEventLogger.class);
	
	@Override
	public void versionResolved(CatalogItemType catalogItem) {
		log.debug("itemRequested: resourceName="+catalogItem.getResourceName()+"; modelVersion="+catalogItem.getVersion());
	}
	@Override
	public void itemRequested(CatalogItemType catalogItem) {
		log.debug("itemRequested: resourceName="+catalogItem.getResourceName()+"; modelVersion="+catalogItem.getVersion());
	}

}
