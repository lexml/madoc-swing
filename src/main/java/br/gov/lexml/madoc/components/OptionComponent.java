package br.gov.lexml.madoc.components;

import br.gov.lexml.madoc.schema.entity.BaseOptionType;

public interface OptionComponent
	<Q extends QuestionComponent<?, ?>, 
	 O extends BaseOptionType, 
	 C> 
	extends BaseWizardComponent<O, C>{

	Q getQuestionComponent();
	
	String getValue();
	
}
