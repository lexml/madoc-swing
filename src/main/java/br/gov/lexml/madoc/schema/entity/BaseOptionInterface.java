package br.gov.lexml.madoc.schema.entity;

import java.util.List;

public interface BaseOptionInterface <O extends BaseOptionType> {

	String getDataSetBind();
	
	boolean isSetDataSetBind();
	
	boolean isSetOption();
	
	List<O> getOption();
	
	boolean isSetSorted();
	
	boolean isSorted();
	
}
