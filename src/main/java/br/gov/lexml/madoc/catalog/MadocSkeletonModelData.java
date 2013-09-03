package br.gov.lexml.madoc.catalog;

import br.gov.lexml.madoc.schema.entity.MadocSkeletonType;
import br.gov.lexml.madoc.schema.entity.MetadataType;

public class MadocSkeletonModelData extends ModelInfo {

	private final MadocSkeletonType docBase;
	
	public MadocSkeletonModelData(String docUri, String modelId, String version,
			MetadataType metadata, MadocSkeletonType docBase) {
		super(docUri, modelId,version,metadata);
		this.docBase = docBase;
	}
	
	public MadocSkeletonType getMadocSkeleton(){
		return docBase;
	}
	
}
