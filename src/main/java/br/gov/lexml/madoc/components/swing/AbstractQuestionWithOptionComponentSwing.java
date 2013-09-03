package br.gov.lexml.madoc.components.swing;

import br.gov.lexml.madoc.components.AbstractQuestionWithOptionComponent;
import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.components.swing.jcomponents.QuestionPanel;
import br.gov.lexml.madoc.schema.entity.BaseOptionType;
import br.gov.lexml.madoc.schema.entity.QuestionType;

abstract class AbstractQuestionWithOptionComponentSwing
	<Q extends QuestionType, 
	OptionCompSw extends OptionComponentSwing
 						<? extends QuestionComponentSwing<? extends QuestionType, ? extends QuestionPanel>,
 						 ? extends BaseOptionType, 
 						 ?>>
	extends AbstractQuestionWithOptionComponent<Q, QuestionPanel, OptionCompSw> 
	implements QuestionComponentSwing<Q, QuestionPanel> {
	
	public AbstractQuestionWithOptionComponentSwing(Q question,
			ComponentController componentController) {
		super(question, componentController);
	}

	@Override
	public void setDisplay(String caption) {
		getComponent().setCaption(caption);
	}
	@Override
	public void setRequired(boolean state) {
		getWizardElement().setRequired(state);
		getComponent().setRequired(state);
	}
	
	@Override
	public void setHighlight(boolean state) {
		getComponent().setHighlight(state);
	}
	
	@Override
	public void setVisible(boolean state) {
		getWizardElement().setVisible(state);
		getComponent().setVisible(state);
	}
	
	@Override
	public void setEnabled(boolean state) {
		getWizardElement().setEnabled(state);
		getComponent().setEnabled(state);
	}
	
	@Override
	public void setHint(String hint) {
		getComponent().setHint(hint);
	}

}
