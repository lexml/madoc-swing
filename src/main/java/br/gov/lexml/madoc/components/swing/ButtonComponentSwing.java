package br.gov.lexml.madoc.components.swing;

import javax.swing.JButton;
import javax.swing.JPanel;

import br.gov.lexml.madoc.components.AbstractBaseWizardComponent;
import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.components.swing.listeners.DefaultButtonListener;
import br.gov.lexml.madoc.components.swing.listeners.SwingListener;
import br.gov.lexml.madoc.schema.entity.ButtonType;

public class ButtonComponentSwing
	extends AbstractBaseWizardComponent<ButtonType, JPanel>
	implements CommandComponentSwing<ButtonType, JPanel> {

	private JPanel buttonPanel;
	private JButton button;
	
	protected ButtonComponentSwing(ButtonType wizardElement,
			ComponentController componentController) {
		super(wizardElement, componentController);
	}

	@Override
	public void setDisplay(String caption) {
		if (button!= null) {
			button.setText(caption);
		}
	}
	
	@Override
	public void setVisible(boolean state) {
		buttonPanel.setVisible(state);
		wizardElement.setVisible(state);
	}

	@Override
	public void setEnabled(boolean state) {
		buttonPanel.setEnabled(state);
		button.setEnabled(state);
		wizardElement.setEnabled(state);
	}

	@Override
	protected JPanel createComponent() {
		buttonPanel = new JPanel();
		button = new JButton();
		
		button.setText(wizardElement.getDisplay());
		
		SwingListener listener = DefaultButtonListener.createDefaultButtonListener(this);
		if (listener!= null){
			button.addActionListener(listener);
		}
		
		button.setEnabled(wizardElement.isEnabled());
		buttonPanel.setVisible(wizardElement.isVisible());
		
		buttonPanel.add(button);
		
		return buttonPanel;
	}

}
