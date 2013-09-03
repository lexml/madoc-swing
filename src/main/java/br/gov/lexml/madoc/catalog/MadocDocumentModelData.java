package br.gov.lexml.madoc.catalog;

import br.gov.lexml.madoc.data.DataSets;
import br.gov.lexml.madoc.schema.entity.MadocDocumentType;
import br.gov.lexml.madoc.schema.entity.MetadataType;

public class MadocDocumentModelData extends ModelInfo {

	private final MadocDocumentType docBase;
	
	private final DataSets dataSets;
	
	public MadocDocumentModelData(String docUri, String modelId, String version,
			MetadataType metadata, MadocDocumentType docBase) {
		super(docUri, modelId, version, metadata);
		this.docBase = docBase;
		this.dataSets = docBase.getDataSets() == null ? DataSets.EMPTY : 
				DataSets.fromDataSets(docBase.getDataSets());
	}
	
	public MadocDocumentType getMadocDocument(){
		return docBase;
	}

	public DataSets getDataSets() {
		return dataSets;
	}
	
}
