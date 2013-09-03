package br.gov.lexml.madoc.catalog;

import br.gov.lexml.madoc.schema.entity.MadocLibraryType;
import br.gov.lexml.madoc.schema.entity.MetadataType;

public class MadocLibraryModelData extends ModelInfo {

	private final MadocLibraryType docBase;
	
	public MadocLibraryModelData(String docUri, String modelId, String version,
			MetadataType metadata, MadocLibraryType docBase) {
		super(docUri, modelId, version, metadata);
		this.docBase = docBase;
	}
	
	public MadocLibraryType getMadocLibrary(){
		return docBase;
	}

	
}
