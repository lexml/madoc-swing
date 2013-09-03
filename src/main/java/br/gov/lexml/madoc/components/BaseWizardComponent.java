package br.gov.lexml.madoc.components;

import br.gov.lexml.madoc.schema.entity.BaseWizardType;

public interface BaseWizardComponent<B extends BaseWizardType, C>{

	String getId();
	
	void setVisible(boolean state);
	
	void setEnabled(boolean state);
	
	void setDisplay(String caption);

	C getComponent();
	
	B getWizardElement();

	ComponentController getComponentController();
	
}
