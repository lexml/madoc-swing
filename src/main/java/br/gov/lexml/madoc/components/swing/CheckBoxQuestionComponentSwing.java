package br.gov.lexml.madoc.components.swing;

import javax.swing.JCheckBox;

import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.components.swing.jcomponents.JToggleInput;
import br.gov.lexml.madoc.components.swing.jcomponents.QuestionPanel;
import br.gov.lexml.madoc.schema.Constants;
import br.gov.lexml.madoc.schema.entity.CheckBoxQuestionType;
import br.gov.lexml.madoc.schema.entity.InputInformation;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerType;

class CheckBoxQuestionComponentSwing extends AbstractQuestionComponentSwing<CheckBoxQuestionType> {

	private JToggleInput<JCheckBox> toggleInput;
	
	public CheckBoxQuestionComponentSwing(CheckBoxQuestionType question,
			ComponentController componentController) {
		super(question, componentController);
	}

	@Override
	protected QuestionPanel createComponent() {
		QuestionPanel panel = createDefaultQuestionPanel(wizardElement, componentController.getCatalogService());

		InputInformation ii = new InputInformation();
		ii.setInput(wizardElement.isInput()).setInputType(wizardElement.getInputType()).setInputDefaultValue(wizardElement.getInputDefaultValue());
		
		toggleInput = new JToggleInput<JCheckBox>(new JCheckBox(), ii);
		toggleInput.setCaption(hostEditorReplacer.replaceString(wizardElement.getDisplay()));
		
		if (wizardElement.isSetChecked()){
			toggleInput.setSelected(wizardElement.isChecked());
		}
		
		//add listener
		toggleInput.addListener(createDefaultListener());

		panel.add(toggleInput);

		return panel;

	}

	@Override
	protected QuestionAnswerType createAnswer(QuestionAnswerType qat) {
		
		String value;
		
		if (!toggleInput.isSelected()) {
			value = Constants.FALSE_STRING;
		} else {
			if (wizardElement.isInput() && !toggleInput.getText().equals("")){
				value = toggleInput.getText();
			} else {
				if (wizardElement.isSetValue()){
					value = wizardElement.getValue();
				} else {
					value = Constants.TRUE_STRING;
				}
			}
		}
		
		qat.setValue(value);
		
		return qat;
	}

	@Override
	protected void answerUpdated(QuestionAnswerType qat) {
		setValue(qat.getValue());
	}
	
	@Override
	public void setDisplay(String caption) {
		super.setDisplay(caption);
		toggleInput.setCaption(caption);
	}
	
	@Override
	public void setValue(String value) {
		toggleInput.setSelected(value!= null && !value.equals(Constants.FALSE_STRING));
		toggleInput.setText(value);
	}
	
	@Override
	public void addValue(String value) {
		setValue(value);
	}

}
