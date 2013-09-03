package br.gov.lexml.madoc.components.swing;

import javax.swing.ButtonGroup;

import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.components.swing.jcomponents.QuestionPanel;
import br.gov.lexml.madoc.schema.entity.RadioBoxGroupQuestionType;
import br.gov.lexml.madoc.schema.entity.SelectOptionType;

class RadioBoxGroupQuestionComponentSwing
	extends AbstractQuestionWithOptionToggleComponentSwing<RadioBoxGroupQuestionType, OptionRadioBoxItemComponent>{

	private ButtonGroup cbg;
	
	public RadioBoxGroupQuestionComponentSwing(RadioBoxGroupQuestionType question,
			ComponentController controller) {
		super(question, controller);
	}

	@Override
	protected QuestionPanel createComponentWithOptions() {
		QuestionPanel panel = AbstractQuestionComponentSwing.createDefaultQuestionPanel(wizardElement, componentController.getCatalogService());
		
		cbg = new ButtonGroup();
		
		for (OptionRadioBoxItemComponent option : getOptionsMap().values()){
			cbg.add(option.getComponent().getToggleButton());
			panel.add(option.getComponent());
		}
		
		
		return panel;
	}
	
	@Override
	protected OptionRadioBoxItemComponent createOptionToggleItemComponent(SelectOptionType selectOption) {
		return new OptionRadioBoxItemComponent(this, selectOption);
	}


}
