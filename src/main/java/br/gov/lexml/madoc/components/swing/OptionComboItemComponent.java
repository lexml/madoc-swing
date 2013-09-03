package br.gov.lexml.madoc.components.swing;

import javax.swing.JComponent;

import br.gov.lexml.madoc.components.AbstractOptionComponent;
import br.gov.lexml.madoc.schema.entity.ObjectFactory;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerOptionType;
import br.gov.lexml.madoc.schema.entity.SelectOptionType;

class OptionComboItemComponent 
	extends AbstractOptionComponent
		<ComboQuestionComponentSwing, 
		 SelectOptionType, 
		 JComponent>
	implements OptionComponentSwing<
		ComboQuestionComponentSwing, 
		SelectOptionType, 
		JComponent>{

	private ComponentOptionItem componentOptionItem;
	
	public OptionComboItemComponent(ComboQuestionComponentSwing questionComponent, SelectOptionType optionType) {
		super(questionComponent, optionType);
	}

	/**
	 * Create a QuestionAnswerOptionType
	 * @return QuestionAnswerOptionType
	 */
	public QuestionAnswerOptionType getAnswerOptionType(){
		QuestionAnswerOptionType qaot = new ObjectFactory().createQuestionAnswerOptionType();
		qaot.setId(getId());
		qaot.setValue(getValue());
		qaot.setEnabled(getWizardElement().isEnabled());
		qaot.setVisible(getWizardElement().isVisible());
		
		qaot.setSelected(getQuestionComponent().getValue().equals(getValue()));
		
		return qaot;
	}
	
	@Override
	protected JComponent createComponent() {

		// verificando se é o valor selecionado
		if (wizardElement.isSelected()){
			questionComponent.setSelected(this);
		}
		
		componentOptionItem =  new ComponentOptionItem();
		
		return componentOptionItem.setValue(getValue());
	}
	
	@Override
	public void setDisplay(String caption) {
		if (componentOptionItem!= null){
			componentOptionItem.setValue(caption);
		}
	}

	@Override
	public void setVisible(boolean state) {
		// there is way to set this	
	}

	@Override
	public void setEnabled(boolean state) {
		// there is way to set this	
	}
	
}
