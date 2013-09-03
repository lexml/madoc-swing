package br.gov.lexml.madoc.schema.entity;

public interface OptionableQuestionInterface <O extends BaseOptionInterface<? extends BaseOptionType>> {

	O getOptions();
	
	boolean isSetOptions();
	
	
}
