package br.gov.lexml.madoc.components.swing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import br.gov.lexml.madoc.components.AbstractBaseWizardComponent;
import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.components.QuestionComponent;
import br.gov.lexml.madoc.components.SectionComponent;
import br.gov.lexml.madoc.components.swing.jcomponents.SwingConstants;
import br.gov.lexml.madoc.schema.entity.SectionType;

class SectionComponentSwing extends AbstractBaseWizardComponent<SectionType, JPanel> implements SectionComponent<JPanel>{

	private final List<QuestionComponentSwing<?,?>> questionsComponents = new ArrayList<QuestionComponentSwing<?,?>>();
	private final List<CommandComponentSwing<?,?>> buttonsComponents = new ArrayList<CommandComponentSwing<?,?>>();
	private final List<HtmlContentComponentSwing> htmlContentComponents = new ArrayList<HtmlContentComponentSwing>();
	
	private TitledBorder titledBorder;
	
	/**
	 * Constructor to a "Phantom Section". In this case, the SectionComponent has no WizardElement. 
	 * It is useful to add questions without a "real Section".
	 * @param hostEditorReplacer
	 */
	public SectionComponentSwing(ComponentController controller) {
		super(null, controller);
	}
	
	public SectionComponentSwing(SectionType wizardElement, ComponentController controller) {
		super(wizardElement, controller);
	}
	
	/**
	 * Return true if QuestionComponent is here
	 * @param firstQuestion
	 */
	@Override
	public boolean containsThisQuestionComponent(QuestionComponent<?,?> firstQuestion){
		return questionsComponents.contains(firstQuestion);
	}

	/**
	 * Add a QuestionComponent to the list
	 * @param questionComponent
	 */
	public void add(QuestionComponentSwing<?,?> questionComponent){
		questionsComponents.add(questionComponent);
		
		//Add each QuestionPanel from QuestionsComponents in the resultPanel
		if (getComponent() != null){
			getComponent().add(questionComponent.getComponent());
		}
	}
	
	/**
	 * Add a CommandComponent to the list
	 * @param buttonComponent
	 */
	public void add(CommandComponentSwing<?, ?> buttonComponent){
		buttonsComponents.add(buttonComponent);
		
		//Add each QuestionPanel from QuestionsComponents in the resultPanel
		if (getComponent() != null){
			getComponent().add(buttonComponent.getComponent());
		}
	}
	
	/**
	 * Add a HtmlContentComponenet to the list
	 * @param htmlContentComponent
	 */
	public void add(HtmlContentComponentSwing htmlContentComponent){
		htmlContentComponents.add(htmlContentComponent);
		
		//Add each QuestionPanel from QuestionsComponents in the resultPanel
		if (getComponent() != null){
			getComponent().add(htmlContentComponent.getComponent());
		}
	}
	
	@Override
	protected JPanel createComponent() {
		// create new panel
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		// if this is not an empty Section 
		if (wizardElement!= null){
			
			titledBorder = new TitledBorder(wizardElement.getDisplay());
			titledBorder.setTitleFont(SwingConstants.BOLD_FONT);
			
			panel.setBorder(
					BorderFactory.createCompoundBorder(
							titledBorder,
							BorderFactory.createEmptyBorder(0, 10, 0, 10)));
	
			
			
			if (wizardElement.isSetEnabled()){
				panel.setEnabled(wizardElement.isEnabled());
			}
			if (wizardElement.isSetVisible()){
				panel.setVisible(wizardElement.isVisible());
			}
			if (wizardElement.isSetHint()){
				
				//JPanel topLabel = new JPanel();
				//topLabel.setLayout(new BoxLayout(topLabel, BoxLayout.Y_AXIS));

				JLabel lToolTip = new JLabel(wizardElement.getHint());
				lToolTip.setFont(SwingConstants.TOOLTIP_FONT);
				lToolTip.setBorder(BorderFactory.createEmptyBorder(2, 0, 5, 0));
				panel.add(painelOnLeft(lToolTip));
				
			}
		}
		
		return panel;
	}
	
	private JPanel painelOnLeft(JComponent... components) {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		for(JComponent c: components) {
			p.add(c);
		}
		p.add(Box.createHorizontalGlue());
		return p;
	}


	@Override
	public void setDisplay(String caption) {
		if (titledBorder!= null){
			titledBorder.setTitle(caption);
		}
	}
	
	@Override
	public void setVisible(boolean state) {
		getComponent().setVisible(state);
		wizardElement.setVisible(state);
	}

	@Override
	public void setEnabled(boolean state) {
		getComponent().setEnabled(state);
		wizardElement.setEnabled(state);
	}

}
