package br.gov.lexml.madoc.components.swing.listeners;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.event.ChangeEvent;

import br.gov.lexml.madoc.components.QuestionComponent;
import br.gov.lexml.madoc.components.rules.RulesVisitor;
import br.gov.lexml.madoc.schema.entity.RulesType;

/**
 * Default listener for page JComponents from PageConstructVisitor
 * 
 * @author lauro
 */
public class DefaultQuestionListener implements SwingListener {

	// list of rules applied to the question
	private final RulesType localRules;

	private final QuestionComponent<?,?> questionComponent;
	
	/**
	 * Create a listener only if necessary
	 * @param baseWizardType
	 * @return
	 */
	public static SwingListener createDefaultListener(
			QuestionComponent<?,?> questionComponent) {
		
		RulesType localRules = null;
		if (questionComponent!= null 
				&& questionComponent.getWizardElement()!= null
				&& questionComponent.getWizardElement().getOnChangeRules()!= null){
			localRules = questionComponent.getWizardElement().getOnChangeRules();
		}
		
		if (localRules== null || localRules.getRule()== null || localRules.getRule().isEmpty()) {
			return null;
		} else {
			return new DefaultQuestionListener(questionComponent, localRules);
		}
	}
	
	private DefaultQuestionListener(QuestionComponent<?,?> questionComponent, RulesType localRules){
		this.questionComponent = questionComponent;
		this.localRules = localRules;
	}
	
	private void processRules() {
		RulesVisitor rulesVisitor = new RulesVisitor(questionComponent, questionComponent.getComponentController());
		
		localRules.accept(rulesVisitor);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		processRules();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		processRules();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		processRules();		
	}

}