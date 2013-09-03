package br.gov.lexml.madoc.catalog;

import br.gov.lexml.madoc.schema.entity.MetadataType;
import br.gov.lexml.madoc.schema.entity.ResourceEntity;

public class ResourceEntityModelData extends ModelInfo{

	private final ResourceEntity resource;

	public ResourceEntityModelData(String docUri, String modelId, String version, MetadataType metadata, ResourceEntity resource) {
		super(docUri, modelId, version, metadata);
		this.resource = resource;
	}
	
	public ResourceEntity getResourceEntity(){
		return resource;
	}

}
