package br.gov.lexml.madoc.catalog;



public interface CatalogServiceFactory {
	
	CatalogService createCatalogService() throws CatalogException;
	
}
