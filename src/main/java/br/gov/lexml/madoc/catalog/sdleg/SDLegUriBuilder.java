package br.gov.lexml.madoc.catalog.sdleg;

import br.gov.lexml.madoc.catalog.UriBuilder;

public class SDLegUriBuilder implements UriBuilder {

	@Override
	public String buildUri(String modelId, String baseId, String modelVersion) {
		if(baseId == null) {
			return null;
		} else if(modelVersion == null) {
			return "urn:sf:sistema;sdleg:id;" + baseId;
		} else {
			return "urn:sf:sistema;sdleg:id;" + baseId + ":versao;" + modelVersion;
		}
	}

}
