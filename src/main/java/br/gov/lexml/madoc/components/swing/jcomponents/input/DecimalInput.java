package br.gov.lexml.madoc.components.swing.jcomponents.input;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Pattern;

import javax.swing.JFormattedTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import br.gov.lexml.madoc.components.swing.listeners.SwingListener;
import br.gov.lexml.swing.util.PatternDocumentFilter;

class DecimalInput implements MadocSwingInput<JFormattedTextField> {

	private final JFormattedTextField textField;
	
	{
		textField = new JFormattedTextField(NumberFormat.getInstance());
		
		DefaultFormatter fmt = new NumberFormatter(new DecimalFormat("###,###,##0.00"));
		DefaultFormatterFactory fmtFactory = new DefaultFormatterFactory(fmt, fmt, fmt);
		textField.setFormatterFactory(fmtFactory);
		
		((AbstractDocument) textField.getDocument()).setDocumentFilter(new PatternDocumentFilter(Pattern.compile("\\d+(?:,\\d{0,2})?")));
	}
	
	@Override
	public String getStringValue() {
		return textField.getText();
	}

	@Override
	public JFormattedTextField getComponent() {
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
