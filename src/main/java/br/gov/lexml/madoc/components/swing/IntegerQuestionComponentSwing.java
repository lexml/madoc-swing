package br.gov.lexml.madoc.components.swing;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.components.swing.jcomponents.QuestionPanel;
import br.gov.lexml.madoc.schema.entity.IntegerQuestionType;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerType;

class IntegerQuestionComponentSwing extends AbstractQuestionComponentSwing<IntegerQuestionType> {

	private static final Logger log = LoggerFactory.getLogger(IntegerQuestionComponentSwing.class);
	
	private SpinnerNumberModel spinnerModel;
	private JSpinner spinner;
	
	public IntegerQuestionComponentSwing(IntegerQuestionType question,
			ComponentController controller) {
		super(question, controller);
	}

	@Override
	protected QuestionPanel createComponent() {
		QuestionPanel panel = createDefaultQuestionPanel(wizardElement, componentController.getCatalogService());

		Integer def = new Integer(0);
		Integer min = null; 
		Integer max = null;
		
		if (wizardElement.isSetMinValue()){
			min = wizardElement.getMinValue();
		}
		
		if (wizardElement.isSetMaxValue()){
			max = wizardElement.getMaxValue();
		}
		
		if (wizardElement.isSetDefaultValue()){
			def = wizardElement.getDefaultValue();
		}
		
		if (min!= null && def!= null && def< min){
			def = min;
		}

		spinnerModel = new SpinnerNumberModel(def, min, max, new Integer(1));

		spinner = new JSpinner(spinnerModel);
		
		spinner.setEditor(new JSpinner.NumberEditor(spinner, "#"));

		//add listener
		ChangeListener listener = createDefaultListener();
		if (listener!= null){
			spinnerModel.addChangeListener(listener);
		}
		
		panel.add(spinner);
		
		return panel;
	}

	@Override
	protected QuestionAnswerType createAnswer(QuestionAnswerType qat) {
		qat.setValue(spinnerModel.getValue().toString());
		return qat;
	}

	@Override
	protected void answerUpdated(QuestionAnswerType qat) {
		setValue(qat.getValue());
	}
	
	@Override
	public void setValue(String value) {
		spinnerModel.setValue(Integer.parseInt(value));
	}
	
	@Override
	public void addValue(String value) {
		String currentValue = getValue();
		try {
			int o1 = Integer.parseInt(currentValue);
			int o2 = Integer.parseInt(value);
			int o3 = o1+o2;
			setValue(String.valueOf(o3));
		} catch (Exception e){
			log.warn("Could not addValue "+value+" to "+currentValue);
		}
	} 

	
	@Override
	public boolean isRequiredValueReached() {
		
		if (spinnerModel.getValue() instanceof Number){
			Number n = (Number)spinnerModel.getValue();
			
			// testing minimum
			if (spinnerModel.getMinimum()!= null && 
					(spinnerModel.getMinimum() instanceof Number)) {
				Number min = (Number) spinnerModel.getMinimum();
				if (n.longValue() < min.longValue()){
					return false;
				}
			}
			
			// testing maximum
			if (spinnerModel.getMaximum()!= null && 
					(spinnerModel.getMaximum() instanceof Number)) {
				Number max = (Number) spinnerModel.getMaximum();
				if (n.longValue() > max.longValue()){
					return false;
				}
			}
		}
		
		return true;
	}

}
