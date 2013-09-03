package br.gov.lexml.madoc.components.swing;

import javax.swing.JRadioButton;

import br.gov.lexml.madoc.schema.entity.SelectOptionType;

class OptionRadioBoxItemComponent
	extends AbstractOptionToggleItemComponentSwing<RadioBoxGroupQuestionComponentSwing, JRadioButton> {

	public OptionRadioBoxItemComponent(
			RadioBoxGroupQuestionComponentSwing questionComponent,
			SelectOptionType optionType) {
		super(questionComponent, optionType);
	}
	
	@Override
	protected JRadioButton createToggleButton() {
		JRadioButton jrb = new JRadioButton();
		return jrb;
	}

}
