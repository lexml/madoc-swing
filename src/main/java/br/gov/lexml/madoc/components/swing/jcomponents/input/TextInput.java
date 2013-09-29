package br.gov.lexml.madoc.components.swing.jcomponents.input;

import javax.swing.JTextField;

import br.gov.lexml.madoc.components.swing.listeners.SwingListener;

class TextInput implements MadocSwingInput<JTextField> {

	private final JTextField textField;
	
	{
		textField = new JTextField();
	}

	@Override
	public String getStringValue() {
		return textField.getText();
	}

	@Override
	public JTextField getComponent() {
		return textField;
	}

	@Override
	public void setStringValue(String value) {
		textField.setText(value);
	}

	@Override
	public void addListener(SwingListener el) {
		textField.addActionListener(el);
	}

    @Override
    public void setEnabled(boolean enabled) {
        textField.setEnabled(enabled);
    }

}
