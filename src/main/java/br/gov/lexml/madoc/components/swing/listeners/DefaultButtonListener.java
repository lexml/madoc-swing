package br.gov.lexml.madoc.components.swing.listeners;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.event.ChangeEvent;

import br.gov.lexml.madoc.components.CommandComponent;
import br.gov.lexml.madoc.components.rules.RulesVisitor;
import br.gov.lexml.madoc.schema.entity.RulesType;

public class DefaultButtonListener implements SwingListener {

	private final RulesType localRules;
	private final CommandComponent<?, ?> commandComponent;

	/**
	 * Create a listener only if necessary
	 * 
	 * @param baseWizardType
	 * @return
	 */
	public static SwingListener createDefaultButtonListener(
			CommandComponent<?, ?> commandComponent) {
		return new DefaultButtonListener(commandComponent, commandComponent.getWizardElement().getOnClickRules());
	}
	
	private DefaultButtonListener(CommandComponent<?, ?> commandComponent, RulesType localRules){
		this.commandComponent = commandComponent;
		this.localRules = localRules;
	}
	
	private void processRules() {
		RulesVisitor rulesVisitor = new RulesVisitor(commandComponent, commandComponent.getComponentController());
		
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
