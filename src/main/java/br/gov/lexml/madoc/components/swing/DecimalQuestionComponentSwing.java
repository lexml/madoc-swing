package br.gov.lexml.madoc.components.swing;

import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Pattern;

import javax.swing.JFormattedTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.components.swing.jcomponents.QuestionPanel;
import br.gov.lexml.madoc.schema.Constants;
import br.gov.lexml.madoc.schema.entity.DecimalQuestionType;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerType;
import br.gov.lexml.swing.util.PatternDocumentFilter;

class DecimalQuestionComponentSwing extends AbstractQuestionComponentSwing<DecimalQuestionType> {

	private static final Logger log = LoggerFactory.getLogger(DecimalQuestionComponentSwing.class);

	private JFormattedTextField textField;
	
	public DecimalQuestionComponentSwing(DecimalQuestionType question,
			ComponentController controller) {
		super(question, controller);
	}

	@Override
	protected QuestionPanel createComponent() {
		QuestionPanel panel = createDefaultQuestionPanel(wizardElement, componentController.getCatalogService());
		
		textField = new JFormattedTextField(
				NumberFormat.getInstance());
		
		if (wizardElement.isSetDefaultValue()) {
			textField.setValue(wizardElement.getDefaultValue());
		}
		
		String formato;
		if (wizardElement.isSetMask()){
			formato = wizardElement.getMask();
		} else {
			formato = "###,###,##0.00";
		}
		DefaultFormatter fmt = new NumberFormatter(new DecimalFormat(formato));
		DefaultFormatterFactory fmtFactory = new DefaultFormatterFactory(fmt, fmt, fmt);
		textField.setFormatterFactory(fmtFactory);
		
		((AbstractDocument) textField.getDocument()).setDocumentFilter(new PatternDocumentFilter(Pattern.compile("\\d+(?:,\\d{0,2})?")));

		//add listener
		ActionListener listener = createDefaultListener();
		if (listener!= null){
			textField.addActionListener(listener);
		}

		panel.add(textField);
		
		return panel;
	}

	@Override
	protected QuestionAnswerType createAnswer(QuestionAnswerType qat) {
		qat.setValue(textField.getText());
		return qat;
	}

	@Override
	protected void answerUpdated(QuestionAnswerType qat) {
		setValue(qat.getValue());
	}
	
	@Override
	public void setValue(String value) {
		textField.setText(value);
	}
	
	@Override
	public void addValue(String value) {
		String currentValue = getValue();
		
		NumberFormat nf = NumberFormat.getInstance(Constants.DEFAULT_LOCALE);
		
		try {
			Number o1 = nf.parse(value);
			Number o2 = nf.parse(value);
			float o3 = o1.floatValue() + o2.floatValue();
			setValue(String.valueOf(o3));
		} catch (Exception e){
			log.warn("Could not addValue "+value+" to "+currentValue);
		}

	}

}
