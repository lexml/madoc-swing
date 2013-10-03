package br.gov.lexml.madoc.components;

import br.gov.lexml.madoc.schema.entity.BaseOptionInterface;
import br.gov.lexml.madoc.schema.entity.BaseOptionType;
import br.gov.lexml.madoc.schema.entity.OptionableQuestionInterface;
import br.gov.lexml.madoc.schema.entity.QuestionType;


public interface QuestionWithOptionComponent
	<Q extends QuestionType & OptionableQuestionInterface<? extends BaseOptionInterface<? extends BaseOptionType>>, C> 
	extends QuestionComponent<Q, C> {

	/**
	 * Select an option as current value
	 * @param optionId
	 */
	void selectOption(String optionId);
	
}
