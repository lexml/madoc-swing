package br.gov.lexml.madoc.components.swing;

import javax.swing.JCheckBox;

import br.gov.lexml.madoc.schema.entity.SelectOptionType;

class OptionCheckBoxItemComponent 
	extends AbstractOptionToggleItemComponentSwing<CheckBoxGroupQuestionComponentSwing, JCheckBox> {

	public OptionCheckBoxItemComponent(
			CheckBoxGroupQuestionComponentSwing questionComponent,
			SelectOptionType optionType) {
		super(questionComponent, optionType);
	}

	@Override
	protected JCheckBox createToggleButton() {
		return new JCheckBox();
	}
	
	
}
