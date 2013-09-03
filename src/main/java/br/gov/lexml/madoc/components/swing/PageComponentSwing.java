package br.gov.lexml.madoc.components.swing;

import java.awt.BorderLayout;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import br.gov.lexml.madoc.components.AbstractBaseWizardComponent;
import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.components.PageComponent;
import br.gov.lexml.madoc.components.QuestionComponent;
import br.gov.lexml.madoc.components.swing.jcomponents.SwingConstants;
import br.gov.lexml.madoc.schema.entity.PageType;

class PageComponentSwing 
	extends AbstractBaseWizardComponent<PageType, JPanel> 
	implements PageComponent<SectionComponentSwing, JPanel>{

	private final Set<SectionComponentSwing> listSection = new HashSet<SectionComponentSwing>();
	
	private JLabel pageTitle;
	
	public PageComponentSwing(PageType wizardElement, ComponentController controller) {
		super(wizardElement, controller);
	}

	/**
	 * Create an empty component 
	 */
	@Override
	protected JPanel createComponent() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		if (wizardElement.isSetDisplay()) {
			pageTitle = new JLabel(wizardElement.getDisplay());
			pageTitle.setFont(SwingConstants.TITLE_FONT);
			JPanel pTitle = new JPanel(new BorderLayout());
			pTitle.add(pageTitle, BorderLayout.WEST);
			panel.add(pTitle);
			panel.add(Box.createVerticalStrut(10));
		}
		if (wizardElement.isSetVisible()){
			panel.setVisible(wizardElement.isVisible());
		}
		if (wizardElement.isSetEnabled()){
			panel.setEnabled(wizardElement.isEnabled());
		}
		
		return panel;
	}
	
	@Override
	public void setDisplay(String caption) {
		if (pageTitle!= null){
			pageTitle.setText(caption);
		}
	}
	
	@Override
	public void addSections(Set<SectionComponentSwing> sections){
		
		for (SectionComponentSwing section : sections){
			//adding section to the set, if it isn't there 
			if (listSection.add(section)){
				//adding section components

				getComponent().add(section.getComponent());
			}
		}
	}
	
	@Override
	public boolean containsThisQuestionComponent(QuestionComponent<?,?> questionComponent){
		for (SectionComponentSwing sectionComponentSwing : listSection){
			if (sectionComponentSwing.containsThisQuestionComponent(questionComponent)){
				return true;
			}
		}
		return false;
	}

	@Override
	public void setVisible(boolean state) {
		getComponent().setVisible(state);
		wizardElement.setVisible(state);
	}

	@Override
	public void setEnabled(boolean state) {
		setEnabled(state);
		wizardElement.setEnabled(state);
	}
}
