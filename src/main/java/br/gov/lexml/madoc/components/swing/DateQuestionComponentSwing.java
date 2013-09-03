package br.gov.lexml.madoc.components.swing;

import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.components.swing.jcomponents.QuestionPanel;
import br.gov.lexml.madoc.schema.Constants;
import br.gov.lexml.madoc.schema.entity.DateQuestionType;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerType;

import com.toedter.calendar.JDateChooser;

class DateQuestionComponentSwing extends AbstractQuestionComponentSwing<DateQuestionType> {

	private static final Logger log = LoggerFactory.getLogger(DateQuestionComponentSwing.class);

	
	private JDateChooser calendar;
	
	public DateQuestionComponentSwing(DateQuestionType question, ComponentController controller) {
		super(question, controller);
	}

	@Override
	protected QuestionPanel createComponent() {
		QuestionPanel panel = createDefaultQuestionPanel(wizardElement, componentController.getCatalogService());

		calendar = new JDateChooser();
		
		if (wizardElement.isSetToday()){
			calendar.setDate(new Date());
		} else if (wizardElement.isSetDefaultValue()){
			calendar.setDate(wizardElement.getDefaultValue().getTime());
		}

		//add listener
		PropertyChangeListener listener = createDefaultListener();
		if (listener!= null){
			calendar.addPropertyChangeListener(listener);
		}

		panel.add(calendar);

		return panel;
	}

	@Override
	protected QuestionAnswerType createAnswer(QuestionAnswerType qat) {
		Date date = calendar.getDate();

		if (date == null) {
			return null;
		}

		qat.setValue(new SimpleDateFormat(Constants.DATE_FORMAT).format(date));
		return qat;
	}

	@Override
	protected void answerUpdated(QuestionAnswerType qat) {
		setValue(qat.getValue());
	}
	
	@Override
	public void setValue(String value) {
		try {
			calendar.setDate(new SimpleDateFormat(
					Constants.DATE_FORMAT).parse(value));
		} catch (ParseException e) {
			log.warn("Could set value "+value+" in JDateChooser", e);

			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Add number of days to current date 
	 */
	@Override
	public void addValue(String value) {
		try {
			
			Calendar c = Calendar.getInstance();
			c.setTime(calendar.getDate());
			c.add(Calendar.DATE, Integer.valueOf(value));
			
			calendar.setDate(c.getTime());
			
		} catch (Exception e) {
			log.warn("Could add value "+value+" in JDateChooser", e);

			throw new RuntimeException(e);
		}
	}

}
