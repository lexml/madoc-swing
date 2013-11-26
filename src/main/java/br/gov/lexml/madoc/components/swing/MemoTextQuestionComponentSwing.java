package br.gov.lexml.madoc.components.swing;

import java.awt.Component;
import java.awt.Container;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;

import org.apache.commons.lang3.StringUtils;

import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.components.swing.jcomponents.QuestionPanel;
import br.gov.lexml.madoc.schema.entity.MemoTextQuestionType;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerType;

class MemoTextQuestionComponentSwing extends AbstractQuestionComponentSwing<MemoTextQuestionType> {

	private JTextArea textArea;
	
	private final class MyJScrollPane extends JScrollPane{
		
		private static final long serialVersionUID = -4608379519816324891L;

		MyJScrollPane(Component view, int vsbPolicy, int hsbPolicy){
			super(view, vsbPolicy, hsbPolicy);
		}
		
		@Override
		public void setEnabled(boolean enabled) {
			super.setEnabled(enabled);
			loopEnabled(this, enabled);
		}
		
		private void loopEnabled(Container p, boolean enabled){
			for (Component c : p.getComponents()){
				if (c instanceof Container){
					loopEnabled((Container)c, enabled);
				}
				
				c.setEnabled(enabled);
			}
		}
	}
	
	public MemoTextQuestionComponentSwing(MemoTextQuestionType question,
			ComponentController controller) {
		super(question, controller);
	}

	@Override
	protected QuestionPanel createComponent() {
		QuestionPanel panel = createDefaultQuestionPanel(wizardElement, componentController.getCatalogService());

		textArea = new JTextArea(wizardElement.getLines().intValue(), 50);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		
		textArea.setBorder(null);

		// setar valor default depois do addListener
		textArea.setText(wizardElement.getDefaultValue());
		
		JScrollPane sp = new MyJScrollPane(textArea, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		sp.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		panel.add(sp);

		return panel;

	}

	@Override
	protected QuestionAnswerType createAnswer(QuestionAnswerType qat) {
		qat.setValue(textArea.getText());
		return qat;
	}

	@Override
	protected void answerUpdated(QuestionAnswerType qat) {
		setValue(qat.getValue());
	}
	
	@Override
	public void setValue(String value) {
		textArea.setText(hostEditorReplacer.replaceString(value));		
	}
	
	@Override
	public void setDefaultValue(String value) {
		boolean isEmpty = StringUtils.isEmpty(textArea.getText());
		String defaultValue = hostEditorReplacer.replaceString(
				hostEditorReplacer.replaceString(wizardElement.getDefaultValue()));
		if(isEmpty || textArea.getText().equals(defaultValue)) {
			setValue(value);
			wizardElement.setDefaultValue(textArea.getText());
		}
	}
	
	@Override
	public void addValue(String value) {
		setValue(getValue()+hostEditorReplacer.replaceString(value));
	} 

}
