package br.gov.lexml.madoc.components.swing.jcomponents;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.PopupMenu;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import br.gov.lexml.madoc.catalog.CatalogService;
import br.gov.lexml.madoc.schema.Constants;
import br.gov.lexml.madoc.schema.entity.QuestionType;

public class QuestionPanel extends JPanel {
	
	private static final long serialVersionUID = -5772729020895573316L;
	
	private final JPanel pInput;
	private final JLabel lToolTip;
	private final JLabel requiredLabel = new JLabel();
	private final JLabel display;
	private final JTextPane textPane;
	
	private Border originalBorder;
	private boolean highlight; 
	
	{
		requiredLabel.setForeground(Color.red);
		requiredLabel.setFont(SwingConstants.BOLD_FONT);
	}
	
	public QuestionPanel(QuestionType question, CatalogService catalogService) {
		
		//this panel
		
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		
		//label panel
		
		JPanel topLabel = new JPanel();
		topLabel.setLayout(new BoxLayout(topLabel, BoxLayout.Y_AXIS));
		
		display = new JLabel(question.getDisplay());
		display.setHorizontalAlignment(JLabel.LEFT);
		display.setFont(SwingConstants.BOLD_FONT);
		topLabel.add(painelOnLeft(display, requiredLabel));

		lToolTip = new JLabel();
		lToolTip.setFont(SwingConstants.TOOLTIP_FONT);
		lToolTip.setBorder(BorderFactory.createEmptyBorder(2, 0, 5, 0));
		topLabel.add(painelOnLeft(lToolTip));
		
		super.add(topLabel, BorderLayout.NORTH);

		//input panel 
		
		pInput = new JPanel();
		pInput.setLayout(new BoxLayout(pInput, BoxLayout.Y_AXIS));
		pInput.setBorder(BorderFactory.createEmptyBorder(8, 5, 0, 0));
		super.add(pInput, BorderLayout.CENTER);
		
		// hint label
		
		if(question.isSetHint()) {
			setHint(question.getHint());
		}
		
		// other
		
		if (question.isSetEnabled()) {
			setEnabled(question.isEnabled());
		}
		if (question.isSetVisible()){
			setVisible(question.isVisible());
		}
		
		setRequired(question.isRequired());
		
		// htmlText in a textPane
		if (!question.isSetHtmlContent()){
			textPane = null;
		} else {
			textPane = new HtmlContentPane(question.getHtmlContent(), catalogService);
			if (textPane!= null){
				topLabel.add(painelOnLeft(textPane));
			}
		} 
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
	
	/**
	 * Change the question caption
	 * @param caption
	 */
	public void setCaption(String caption){
		if (display!= null){
			display.setText(caption);
		}
	}
	
	/**
	 * Change a hint tool tip
	 * @param hint
	 */
	public void setHint(String hint){
		lToolTip.setText(hint);
	}
	
	/**
	 * Set or not the question as required.
	 * @param requered
	 */
	public void setRequired(boolean isRequired){
		if (isRequired){
			requiredLabel.setText(Constants.REQUIRED_FIELD_TEXT);
		} else {
			requiredLabel.setText("");
		}
	}
	
	/**
	 * Highlights or not this question panel.
	 * @param highlight
	 */
	public void setHighlight(boolean highlight) {
		
		if(!this.highlight && originalBorder == null) {
			originalBorder = getBorder();
		}
		
		this.highlight = highlight;
		
		if (highlight) {
			Border highlightBorder = BorderFactory.createCompoundBorder(
					new LineBorder(SwingConstants.REQUIRED_PANEL_COLOR, SwingConstants.REQUIRED_PANEL_THICKNESS),
					BorderFactory.createEmptyBorder(2, 2, 2, 2));
			if(originalBorder == null) {
				setBorder(highlightBorder);
			}
			else {
				setBorder(BorderFactory.createCompoundBorder(highlightBorder, originalBorder));
			}
		} 
		else {
			setBorder(originalBorder);
		}
		
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		loopEnabled(pInput, enabled);
	}
	
	private void loopEnabled(Container p, boolean enabled){
		p.setEnabled(enabled);
		for (Component c : p.getComponents()){
			if (c instanceof Container){
				loopEnabled((Container)c, enabled);
			}
			
			c.setEnabled(enabled);
		}
	}
	
	
	@Override
	public Component add(Component comp) {
		comp.setEnabled(isEnabled());
		return pInput.add(comp);
	}

	@Override
	public Component add(Component comp, int index) {
		comp.setEnabled(isEnabled());
		return pInput.add(comp, index);
	}
	
	@Override
	public void add(Component comp, Object constraints) {
		comp.setEnabled(isEnabled());
		pInput.add(comp, constraints);
	}
	
	@Override
	public void add(Component comp, Object constraints, int index) {
		comp.setEnabled(isEnabled());
		pInput.add(comp, constraints, index);
	}
	
	@Override
	public synchronized void add(PopupMenu popup) {
		popup.setEnabled(isEnabled());
		pInput.add(popup);
	}
	
	@Override
	public Component add(String name, Component comp) {
		comp.setEnabled(isEnabled());
		return pInput.add(name, comp);
	}
	
}