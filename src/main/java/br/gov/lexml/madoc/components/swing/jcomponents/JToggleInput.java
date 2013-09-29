package br.gov.lexml.madoc.components.swing.jcomponents;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JToggleButton;

import br.gov.lexml.madoc.components.swing.jcomponents.input.MadocSwingInput;
import br.gov.lexml.madoc.components.swing.jcomponents.input.MadocSwingInputFactory;
import br.gov.lexml.madoc.components.swing.listeners.SwingListener;
import br.gov.lexml.madoc.schema.entity.InputInformation;

/**
 * A generic component with a JToggleButton (JCheckBox, JRadioButton) and an
 * optional JTextField.
 * 
 * @author lauro
 * 
 */
public class JToggleInput<T extends JToggleButton> extends JPanel {

	private static final long serialVersionUID = -2165598185795830739L;

	private final T toggleButton;
	private final boolean isInput;
	private final MadocSwingInput<?> inputComponent;
	
	public JToggleInput(T toggleButton, InputInformation inputInformation) {

		this.toggleButton = toggleButton;
		
		setLayout(new BorderLayout(10, 0));

		toggleButton.setHorizontalAlignment(T.LEFT);

		add(toggleButton, BorderLayout.WEST);

		this.isInput = inputInformation.isInput();
		
		if (isInput){
			inputComponent = MadocSwingInputFactory.createComponentByInputInformation(inputInformation);
			
			add(inputComponent.getComponent(), BorderLayout.CENTER);
		} else {
			inputComponent = null;
		}
	}

	public T getToggleButton() {
		return toggleButton;
	}
	
	public void setSelected(boolean selected){
		toggleButton.setSelected(selected);
	}
	
	public void setCaption(String caption){
		toggleButton.setText(caption);
	}

	public boolean isInput() {
		return isInput;
	}
	
	public boolean isSelected(){
		return toggleButton.isSelected();
	}
	
	public String getText(){
		if (isInput){
			return inputComponent.getStringValue();
		}
		return "";
	}
	
	public void setText(String text){
		if (isInput){
			inputComponent.setStringValue(text);
		}
	}
	
	public void addListener(SwingListener l){
		if (l!= null){
			if (isInput){
				inputComponent.addListener(l);
			}
			//toggleButton.addChangeListener(l);
			toggleButton.addActionListener(l);
		}	
	}
	
	@Override
	public void setEnabled(boolean b){
		super.setEnabled(b);
		toggleButton.setEnabled(b);
        if (isInput) {
            inputComponent.setEnabled(b);
        }
	}

}
