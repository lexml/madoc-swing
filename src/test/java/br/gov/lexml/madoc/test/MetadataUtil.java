package br.gov.lexml.madoc.test;

import br.gov.lexml.madoc.data.CollectionValue;
import br.gov.lexml.madoc.data.DataSetUtil;
import br.gov.lexml.madoc.schema.entity.MetadataType;

public class MetadataUtil {

	public static String getTitulo(MetadataType metadata) {
		CollectionValue collectionValue = DataSetUtil.valueFromDataSetValue(metadata);
		
		return collectionValue.queryString("/Titulo");
	}

}
