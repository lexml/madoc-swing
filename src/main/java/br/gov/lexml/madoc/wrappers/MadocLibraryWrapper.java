package br.gov.lexml.madoc.wrappers;

import br.gov.lexml.madoc.catalog.CatalogException;
import br.gov.lexml.madoc.catalog.CatalogService;
import br.gov.lexml.madoc.data.DataSets;
import br.gov.lexml.madoc.schema.entity.MadocLibraryType;

/**
 * Helper class with useful methods on MadocLibraryType.
 * @author lauro
 *
 */
public class MadocLibraryWrapper {

	private final MadocLibraryType madocLibrary;
	
	public MadocLibraryWrapper(MadocLibraryType libraryType){
		this.madocLibrary = libraryType;
	}
	
	public MadocLibraryWrapper(
			CatalogService catalogService,
			String id, 
			String version) throws CatalogException{
	
		this.madocLibrary = catalogService.getMadocLibraryModel(id, version).getMadocLibrary();
	}
	
	public MadocLibraryWrapper(
			CatalogService catalogService,
			String id) throws CatalogException{
		this(catalogService, id, null);
	}
	
	/**
	 * Return MadocLibraryType loaded via catalogService.
	 * @return
	 */
	public MadocLibraryType getMadocLibrary() {
		return madocLibrary;
	}
	
	/**
	 * Return a DataSet wrapper queriable by JXPath of all datasets in MadocLibraryType  
	 * @return
	 */
	public DataSets getDataSetsWrapper(){
		return DataSets.fromDataSets(madocLibrary.getDataSets());
	}
	
}
