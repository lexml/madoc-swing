package br.gov.lexml.madoc.components;

import br.gov.lexml.madoc.schema.entity.BaseOptionType;
import br.gov.lexml.madoc.schema.entity.QuestionType;

public abstract class AbstractOptionComponent 
	<Q extends QuestionComponent<? extends QuestionType, ?>,
 	 O extends BaseOptionType, 
	 C>
	extends AbstractBaseWizardComponent<O, C>
	implements OptionComponent<Q, O, C>{

	protected final Q questionComponent;
	
	public AbstractOptionComponent(Q questionComponent, O optionType){
		super(optionType, questionComponent.getComponentController());
		this.questionComponent = questionComponent;
	}
	
	@Override
	public Q getQuestionComponent() {
		return questionComponent;
	}
	
	@Override
	public String toString() {
		if (wizardElement.isSetDisplay()){
			return wizardElement.getDisplay();
		} else {
			return wizardElement.getValue();
		}
	}
	
	@Override
	public String getValue() {
		return wizardElement.getValue();
	}
}
