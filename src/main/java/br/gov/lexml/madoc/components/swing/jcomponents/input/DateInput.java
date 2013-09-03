package br.gov.lexml.madoc.components.swing.jcomponents.input;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.gov.lexml.madoc.components.swing.listeners.SwingListener;
import br.gov.lexml.madoc.schema.Constants;

import com.toedter.calendar.JDateChooser;

class DateInput implements MadocSwingInput<JDateChooser> {

	private final JDateChooser jDateChooser;
	
	private static final Logger log = LoggerFactory.getLogger(DateInput.class);
	
	{
		jDateChooser = new JDateChooser();
		jDateChooser.setDate(new Date());
	}
	
	@Override
	public String getStringValue() {
		Date date = jDateChooser.getDate();

		if (date == null) {
			return "";
		}

		return new SimpleDateFormat(Constants.DATE_FORMAT).format(date);
	}

	@Override
	public JDateChooser getComponent() {
		return jDateChooser;
	}

	@Override
	public void setStringValue(String value) {
		try {
			jDateChooser.setDate(new SimpleDateFormat(Constants.DATE_FORMAT).parse(value));
		} catch (ParseException e) {
			log.warn("Trying convert an invalid date on DateInput. Value given: "+value);
		}
	}

	@Override
	public void addListener(SwingListener el) {
		//there is no listener to add
	}

}
