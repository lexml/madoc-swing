package br.gov.lexml.madoc.components.swing;

import br.gov.lexml.madoc.catalog.CatalogService;
import br.gov.lexml.madoc.components.AbstractQuestionComponent;
import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.components.QuestionComponent;
import br.gov.lexml.madoc.components.swing.jcomponents.QuestionPanel;
import br.gov.lexml.madoc.components.swing.listeners.DefaultQuestionListener;
import br.gov.lexml.madoc.components.swing.listeners.SwingListener;
import br.gov.lexml.madoc.schema.entity.QuestionType;

abstract class AbstractQuestionComponentSwing
	<Q extends QuestionType> 
	extends AbstractQuestionComponent<Q, QuestionPanel> 
	implements QuestionComponentSwing<Q, QuestionPanel> {

	protected AbstractQuestionComponentSwing(Q wizardElement,
			ComponentController componentController) {
		super(wizardElement, componentController);
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


	/*
	 * HELPERS
	 */
	
	/**
	 * Create a default QuestionPanel
	 * @param question
	 * @return
	 */
	protected static QuestionPanel createDefaultQuestionPanel(QuestionType question, CatalogService catalogService) {
		return new QuestionPanel(question, catalogService);
	}

	/**
	 * Create a listener only if necessary
	 * @return
	 */
	protected SwingListener createDefaultListener(){
		return DefaultQuestionListener.createDefaultListener(this);
	}	
	
	protected static SwingListener createDefaultListener(QuestionComponent<?,?> questionComponent){
		return DefaultQuestionListener.createDefaultListener(questionComponent);
	}	
	
}
