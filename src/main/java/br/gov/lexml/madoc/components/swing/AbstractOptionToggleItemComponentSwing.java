package br.gov.lexml.madoc.components.swing;

import javax.swing.JToggleButton;

import br.gov.lexml.madoc.components.AbstractOptionComponent;
import br.gov.lexml.madoc.components.swing.jcomponents.JToggleInput;
import br.gov.lexml.madoc.schema.Constants;
import br.gov.lexml.madoc.schema.entity.InputInformation;
import br.gov.lexml.madoc.schema.entity.ObjectFactory;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerOptionType;
import br.gov.lexml.madoc.schema.entity.QuestionType;
import br.gov.lexml.madoc.schema.entity.SelectOptionType;

abstract class AbstractOptionToggleItemComponentSwing
	<Q extends AbstractQuestionWithOptionToggleComponentSwing<? extends QuestionType,?>, T extends JToggleButton>
	extends AbstractOptionComponent
		<Q, 
		SelectOptionType, 
		JToggleInput<T>>
	implements OptionComponentSwing<Q, SelectOptionType, JToggleInput<T>>{

	public AbstractOptionToggleItemComponentSwing(
			Q questionComponent,
			SelectOptionType optionType) {
		super(questionComponent, optionType);
	}

	protected abstract T createToggleButton();
	
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
		qaot.setSelected(isSelected());
		return qaot;
	}
	
	public void setSelected(boolean state){
		wizardElement.setSelected(state);
		getComponent().setSelected(state);
	}
	
	public void setInputTextValue(String text){
		if (wizardElement.isInput()){
			getComponent().setText(text);
		}
	}
	
	public boolean isSelected(){
		return getComponent().isSelected();
	}
	
	@Override
	public void setDisplay(String caption) {
		getComponent().setText(caption);
	}
	
	@Override
	public String getValue() {
		if (!isSelected()){
			return Constants.FALSE_STRING;
		}
		
		if (getComponent().isInput()){
			return getComponent().getText();
		}
		
		return wizardElement.getValue();
	}
	
	@Override
	protected JToggleInput<T> createComponent() {
		
		InputInformation ii = new InputInformation();
		ii.setInput(wizardElement.isInput()).setInputType(wizardElement.getInputType()).setInputDefaultValue(wizardElement.getInputDefaultValue());
		
		JToggleInput<T> toggleInput = new JToggleInput<T>(createToggleButton(), ii);
		
		if (wizardElement.isSetDisplay()){
			toggleInput.setCaption(hostEditorReplacer.replaceString(wizardElement.getDisplay()));
		} else {
			toggleInput.setCaption(hostEditorReplacer.replaceString(wizardElement.getValue()));
		}
		
		if (wizardElement.isSetSelected()){
			toggleInput.setSelected(wizardElement.isSelected());
		}
		
		if (wizardElement.isSetHint()){
			toggleInput.setToolTipText(wizardElement.getHint());
		}
		
		if (wizardElement.isSetVisible()){
			toggleInput.setVisible(wizardElement.isVisible());
		}
		
		if (wizardElement.isSetEnabled()){
			toggleInput.setEnabled(wizardElement.isEnabled());
		}
		
		if (wizardElement.isSetValue()){
			toggleInput.setText(hostEditorReplacer.replaceString(wizardElement.getValue()));
		}
		
		//add listener
		toggleInput.addListener(AbstractQuestionComponentSwing.createDefaultListener(questionComponent));

		return toggleInput;
	}
	
	@Override
	public void setVisible(boolean state) {
		wizardElement.setVisible(state);
		getComponent().setVisible(state);
	}

	@Override
	public void setEnabled(boolean state) {
		getComponent().setEnabled(state);
		wizardElement.setEnabled(state);
	}

}
