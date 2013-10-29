package br.gov.lexml.madoc.components.swing;

import java.awt.event.ActionListener;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.components.swing.jcomponents.QuestionPanel;
import br.gov.lexml.madoc.schema.entity.InputTextQuestionType;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerType;
import br.gov.lexml.swing.util.PatternDocumentFilter;

class InputTextQuestionComponentSwing extends AbstractQuestionComponentSwing<InputTextQuestionType> {
	
	private static final Log log = LogFactory.getLog(InputTextQuestionComponentSwing.class);

	private JTextField textField;
	
	public InputTextQuestionComponentSwing(InputTextQuestionType question, ComponentController controller) {
		super(question, controller);
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
		textField.setText(hostEditorReplacer.replaceString(value));
	}
	
	@Override
	public void addValue(String value) {
		setValue(getValue()+hostEditorReplacer.replaceString(value));
	} 

	@Override
	protected QuestionPanel createComponent() {
		
		QuestionPanel panel = createDefaultQuestionPanel(wizardElement, componentController.getCatalogService());

		textField = new JTextField();

		if(wizardElement.isSetRegex()) {
			try {
				Pattern pattern = Pattern.compile(wizardElement.getRegex());
				DocumentFilter docFilter = new PatternDocumentFilter(pattern);
				((AbstractDocument)textField.getDocument()).setDocumentFilter(docFilter);
			}
			catch(PatternSyntaxException e) {
				log.error("Pattern '" + wizardElement.getRegex() + "' inválida.");
			}
		}

		if (wizardElement.isSetDefaultValue()) {
			// setar valor default depois do addListener
			textField.setText(hostEditorReplacer.replaceString(wizardElement.getDefaultValue()));
		}
		
		//add listener
		ActionListener listener = createDefaultListener();
		if (listener!= null){
			textField.addActionListener(listener);
		}
		
		panel.add(textField);
		
		return panel;
	}
	
}
