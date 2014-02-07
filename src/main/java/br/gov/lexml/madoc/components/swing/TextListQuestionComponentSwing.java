package br.gov.lexml.madoc.components.swing;

import java.awt.event.ActionListener;

import br.gov.lexml.madoc.components.AbstractQuestionWithOptionComponent.MultiLineValueBuilderHelper;
import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.components.swing.jcomponents.QuestionPanel;
import br.gov.lexml.madoc.schema.entity.ObjectFactory;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerListType;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerType;
import br.gov.lexml.madoc.schema.entity.TextListQuestionType;
import br.gov.lexml.swing.componentes.ActionToGenListModelListener;
import br.gov.lexml.swing.componentes.JOpenList;

class TextListQuestionComponentSwing extends AbstractQuestionComponentSwing<TextListQuestionType> {

	private JOpenList<String> openList;
	
	private String defaultValue = "";
	
	public TextListQuestionComponentSwing(TextListQuestionType question,
			ComponentController controller) {
		super(question, controller);
	}

	@Override
	protected QuestionPanel createComponent() {
		QuestionPanel panel = createDefaultQuestionPanel(wizardElement, componentController.getCatalogService());
		
		openList = JOpenList.createStringOpenList();
		
		//add listener
		ActionListener listener = createDefaultListener();
		if (listener!= null){
			
			openList.getModel().addListener(new ActionToGenListModelListener<String>(listener));
		}

		panel.add(openList);
		
		return panel;
	}

	@Override
	protected QuestionAnswerType createAnswer(QuestionAnswerType qat) {
		qat.setHasList(true);
		qat.setHasOptions(false);
		
		MultiLineValueBuilderHelper valueBuilder = new MultiLineValueBuilderHelper();
		
		QuestionAnswerListType qalt = new ObjectFactory().createQuestionAnswerListType();
		for (String s : openList.getModel().elements()){
			qalt.getValue().add(s);
			valueBuilder.add(s);
		}
		
		qat.setList(qalt);
		qat.setValue(valueBuilder.getResult());
		
		return qat;
	}

	@Override
	protected void answerUpdated(QuestionAnswerType qat) {
		openList.getModel().clear();
		openList.getModel().addAll(qat.getList().getValue());
	}
	
	@Override
	public void setValue(String value) {
		openList.getModel().clear();
		openList.getModel().add(value);
	}
	
	@Override
	public void setDefaultValue(String value) {
		boolean isEmpty = openList.getModel().size() == 0;
		if(isEmpty || (openList.getModel().size() == 1 && openList.getModel().get(0).equals(defaultValue))) {
			setValue(value);
		}
		defaultValue = value;
	}
	
	@Override
	public void addValue(String value) {
		openList.getModel().add(value);
	}

}
