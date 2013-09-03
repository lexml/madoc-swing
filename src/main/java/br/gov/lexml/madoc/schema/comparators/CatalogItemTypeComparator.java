package br.gov.lexml.madoc.schema.comparators;

import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;

import br.gov.lexml.madoc.schema.entity.CatalogItemType;

public class CatalogItemTypeComparator implements Comparator<CatalogItemType>{

	@Override
	public int compare(CatalogItemType o1, CatalogItemType o2) {
		return new CompareToBuilder()
			.append(o1.getMetadata().getId(), o2.getMetadata().getId())
			.append(o1.getVersion(), o2.getVersion())
			.append(o1.getResourceName(), o2.getResourceName())
			.build();
	}

}
