package br.gov.lexml.madoc.components;

import br.gov.lexml.madoc.schema.entity.BaseWizardType;

/**
 * Components that can contain others, like Page and Sections
 * @author lauro
 *
 * @param <B>
 * @param <C>
 */
public interface ParentComponent<B extends BaseWizardType, C> extends BaseWizardComponent<B, C>{

	/**
	 * Return true if QuestionComponent is here
	 * @param questionComponent
	 * @return
	 */
	boolean containsThisQuestionComponent(QuestionComponent<?,?> questionComponent);

	
}
