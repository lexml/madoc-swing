package br.gov.lexml.madoc.components.swing;

import br.gov.lexml.madoc.components.AbstractBaseWizardComponent;
import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.components.HtmlContentComponent;
import br.gov.lexml.madoc.components.swing.jcomponents.HtmlContentPane;
import br.gov.lexml.madoc.schema.entity.HtmlContentType;

public class HtmlContentComponentSwing
	extends AbstractBaseWizardComponent<HtmlContentType, HtmlContentPane> 
	implements HtmlContentComponent<HtmlContentPane>{

	protected HtmlContentComponentSwing(HtmlContentType wizardElement,
			ComponentController componentController) {
		super(wizardElement, componentController);
	}

	private HtmlContentPane component;
	
	@Override
	public void setVisible(boolean state) {
		component.setVisible(state);
	}	

	@Override
	public void setEnabled(boolean state) {
		component.setEnabled(state);
	}

	@Override
	public void setDisplay(String caption) {
		component.setText(caption);
	}

	@Override
	protected HtmlContentPane createComponent() {
		
		String content = "";
		if (wizardElement.isSetDisplay()){
			content = wizardElement.getDisplay();
		}
		if (wizardElement.isSetContent()){
			content = wizardElement.getContent();
		}
		
		component = new HtmlContentPane(content, componentController.getCatalogService());
		
		if (wizardElement.isSetVisible()){
			component.setVisible(wizardElement.isVisible());
		}
		
		if (wizardElement.isSetEnabled()){
			component.setVisible(wizardElement.isEnabled());
		}
		
		return component;
	}

}
