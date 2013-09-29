package br.gov.lexml.madoc.components.swing.jcomponents.input;

import javax.swing.JComponent;

import br.gov.lexml.madoc.components.swing.listeners.SwingListener;

public interface MadocSwingInput<C extends JComponent>{

	public String getStringValue();
	
	public void setStringValue(String value);
	
	public C getComponent();
	
	public void addListener(SwingListener el);

    public void setEnabled(boolean enabled);
}
