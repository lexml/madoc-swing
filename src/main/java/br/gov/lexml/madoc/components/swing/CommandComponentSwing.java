package br.gov.lexml.madoc.components.swing;

import javax.swing.JComponent;

import br.gov.lexml.madoc.components.CommandComponent;
import br.gov.lexml.madoc.schema.entity.BaseCommandType;

public interface CommandComponentSwing
	<B extends BaseCommandType, C extends JComponent> 
	extends 
		CommandComponent<B, C>, 
		BaseWizardComponentSwing<B, C>{

}
