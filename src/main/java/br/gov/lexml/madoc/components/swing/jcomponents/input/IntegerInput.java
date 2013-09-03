package br.gov.lexml.madoc.components.swing.jcomponents.input;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.gov.lexml.madoc.components.swing.listeners.SwingListener;

class IntegerInput implements MadocSwingInput<JSpinner> {

	private final SpinnerNumberModel spinnerModel;
	private final JSpinner spinner;
	
	private static final Logger log = LoggerFactory.getLogger(IntegerInput.class);

	
	{
		spinnerModel = new SpinnerNumberModel(new Integer(0),null,null,new Integer(1));
		
		spinner = new JSpinner(spinnerModel);
		spinner.setEditor(new JSpinner.NumberEditor(spinner, "#"));
	}

	@Override
	public String getStringValue() {
		return spinner.getValue().toString();
	}

	@Override
	public JSpinner getComponent() {
		return spinner;
	}

	@Override
	public void setStringValue(String value) {
		try{
			spinnerModel.setValue(Integer.valueOf(value));
		} catch (Exception e){
			log.warn("Trying convert an invalid integer on IntegerInput. Value given: "+value);
		}
	}

	@Override
	public void addListener(SwingListener el) {
		spinner.addChangeListener(el);
	}


}
