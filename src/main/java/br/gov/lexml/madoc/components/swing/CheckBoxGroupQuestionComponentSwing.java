package br.gov.lexml.madoc.components.swing;

import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.components.swing.jcomponents.QuestionPanel;
import br.gov.lexml.madoc.schema.entity.CheckBoxGroupQuestionType;
import br.gov.lexml.madoc.schema.entity.SelectOptionType;

class CheckBoxGroupQuestionComponentSwing
	extends AbstractQuestionWithOptionToggleComponentSwing<CheckBoxGroupQuestionType, OptionCheckBoxItemComponent>{

	public CheckBoxGroupQuestionComponentSwing(CheckBoxGroupQuestionType question,
			ComponentController componentController) {
		super(question, componentController);
	}

	@Override
	protected QuestionPanel createComponentWithOptions() {
		QuestionPanel panel = AbstractQuestionComponentSwing.createDefaultQuestionPanel(wizardElement, componentController.getCatalogService());
		
		for (OptionCheckBoxItemComponent option : getOptionsMap().values()){
			panel.add(option.getComponent());
		}
		
		return panel;
	}

	@Override
	protected OptionCheckBoxItemComponent createOptionToggleItemComponent(SelectOptionType selectOption) {
		return new OptionCheckBoxItemComponent(this, selectOption);
	}

}
