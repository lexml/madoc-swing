package br.gov.lexml.madoc.components.swing.jcomponents;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.gov.lexml.madoc.components.QuestionComponent;
import br.gov.lexml.swing.editorhtml.handlers.CardLayoutMaximizeHandler;


/**
 * Control pages navigation.
 * @author lauro
 *
 */
public class PagesControl extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(PagesControl.class);
	
	private int currentPageNumber = 0;
	private final JPanel buttonsPanel;
	private final JPanel contentPanel;
	private JComponent currentPanel;
	private final List<JComponent> pagesList;
	private JScrollPane scrollPane;
	
	private final JButton prior = new JButton(SwingConstants.BUTTON_CAPTION_PRIOR_PAGE);
	private final JButton next = new JButton(SwingConstants.BUTTON_CAPTION_NEXT_PAGE);
	private final JButton first = new JButton(SwingConstants.BUTTON_CAPTION_FIRST_PAGE);
	private final JButton last = new JButton(SwingConstants.BUTTON_CAPTION_LAST_PAGE);
	private final JLabel totalPageNumLabel= new JLabel();
	
	private SpinnerNumberModel goToInputFieldModel = new SpinnerNumberModel(new Integer(1),new Integer(1),new Integer(1),new Integer(1));
	
	private final JSpinner goToInputField = new JSpinner(goToInputFieldModel);
	
	
	{
		setLayout(new CardLayout());

		JPanel pMain = new JPanel(new BorderLayout());

		//initialing variables
		pagesList = new ArrayList<JComponent>();
		contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		buttonsPanel = createButtonsPanel();
		
		//BEGIN: creating scrollPane that contains pagesContentPanel
		JPanel pNorth = new JPanel(new BorderLayout());
		pNorth.add(contentPanel, BorderLayout.WEST);
		
		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		p.add(pNorth, BorderLayout.NORTH);
		
		scrollPane = new JScrollPane(p);
		scrollPane.setBorder(null);
		scrollPane.setViewportBorder(null);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getVerticalScrollBar().setUnitIncrement(26);
		
		pMain.add(scrollPane, BorderLayout.CENTER);
		//END: creating scrollPane that contains pagesContentPanel
		
		//setting buttons control
		pMain.add(buttonsPanel, BorderLayout.SOUTH);

		add(pMain, CardLayoutMaximizeHandler.DEFAULT_MINIMIZED_CARD_KEY);
		
		goToInputField.setPreferredSize(new Dimension(60, (int)goToInputField.getPreferredSize().getHeight()));
	}
	
	public PagesControl(){
		
	}
	
	public PagesControl(List<JComponent> panelPages){
		for (JComponent page : panelPages){
			addPage(page);
		}
	}
	
	public void setButtonsPanelVisible(boolean aFlag){
		buttonsPanel.setVisible(aFlag);
	}
	
	public void addPage(JComponent page){
		pagesList.add(page);
		buttonsPanel.setVisible(!pagesList.isEmpty());
		
		int pagesSize = pagesList.size();
		goToInputFieldModel.setMaximum(pagesSize <= 0 ? 1 : pagesSize);
		
		totalPageNumLabel.setText(""+pagesSize);
	}
	
	public boolean hasNext() {
		int i = 0;
		while (currentPageNumber+1< pagesList.size()){
			if (pagesList.get(currentPageNumber+ ++i).isVisible()){
				return true;
			}
		}
		return false;
	}

	public boolean hasPrior() {
		int i = 0;
		while (currentPageNumber> 0){
			if (pagesList.get(currentPageNumber- ++i).isVisible()){
				return true;
			}
		}
		return false;
	}

	public void showNextPagePanel() {
		while (currentPageNumber< pagesList.size()){
			if (pagesList.get(++currentPageNumber).isVisible()){
				showCurrentPanel();
				break;
			}
		}
	}
	
	public void showPriorPagePanel() {
		while (currentPageNumber>= 0){
			if (pagesList.get(--currentPageNumber).isVisible()){
				showCurrentPanel();
				break;
			}
		}
	}
	
	public void showFirstPagePanel() {
		currentPageNumber = -1;
		showNextPagePanel();
	}
	
	public void showLastPagePanel() {
		currentPageNumber = pagesList.size();
		showPriorPagePanel();
	}
	
	/**
	 * Show a page panel based on a page number starting on 0.
	 * @param pageNum
	 */
	public void showThisPagePanel(int pageNum){
		currentPageNumber = pageNum;
		showCurrentPanel();
	}
	
	public void showThisPagePanel(JComponent component){
		//looking for page number
		currentPageNumber = pagesList.indexOf(component);
		
		showCurrentPanel();
	}
	
	public void scrollToQuestion(QuestionComponent<?, ?> question) {
		if(currentPanel != null) {
			QuestionPanel panel = (QuestionPanel)question.getComponent(); 
			scrollPane.getViewport().setViewPosition(
					SwingUtilities.convertPoint(panel, new Point(0, 0), currentPanel));
		}
	}
	
	private void showCurrentPanel() {
		if (currentPanel!= null){
			contentPanel.remove(currentPanel);
		}
		goToInputField.setValue(currentPageNumber+1);
		currentPanel = pagesList.get(currentPageNumber);
		contentPanel.add(currentPanel);
		contentPanel.updateUI();
		
		processControlButtonsVisibility();
	}
	
	public boolean isEmpty() {
		return pagesList == null || pagesList.isEmpty();
	}
	
	public int getPagesCount(){
		return pagesList.size();
	}
	
	
	/**
	 * Create a panel with buttons prior, next, first and last
	 * @return
	 */
	private JPanel createButtonsPanel(){

		// next button configuration
		next.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (hasNext()) {
					showNextPagePanel();
				}
			}
		});

		// Prior button configuration
		prior.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (hasPrior()) {
					showPriorPagePanel();
				}
			}
		});

		// first button configuration
		first.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				showFirstPagePanel();
			}
		});

		// last button configuration
		last.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				showLastPagePanel();
			}
		});
		
		// goToInputField configuration
		((JSpinner.DefaultEditor)goToInputField.getEditor()).getTextField().addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER ){
					try {
						showThisPagePanel(Integer.parseInt(goToInputField.getValue().toString())-1);
					} catch (Exception ex){
						log.debug("goToButton.actionPerformed exception: "+ex.getMessage(), ex);
					}
				}
			}
		});


		// adding to control Panel
		JPanel internalControlPanel = new JPanel();
		internalControlPanel.add(first);
		internalControlPanel.add(prior);
		
		internalControlPanel.add(Box.createHorizontalStrut(20));
		
		internalControlPanel.add(new JLabel(SwingConstants.LABEL_CAPTION_PAGE));
		internalControlPanel.add(goToInputField);
		internalControlPanel.add(new JLabel(SwingConstants.LABEL_CAPTION_PAGE_FROM));
		internalControlPanel.add(totalPageNumLabel);
		
		internalControlPanel.add(Box.createHorizontalStrut(20));
		
		internalControlPanel.add(next);
		internalControlPanel.add(last);
		
		processControlButtonsVisibility();

		JPanel controlPanel = new JPanel(new BorderLayout());
		controlPanel.add(internalControlPanel, BorderLayout.WEST);
		controlPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
		return controlPanel;
	}
	
	/**
	 * Set the buttons visibility
	 * 
	 * @param first
	 * @param prior
	 * @param next
	 * @param last
	 */
	private void processControlButtonsVisibility() {
		next.setEnabled(hasNext());
		prior.setEnabled(hasPrior());
		first.setEnabled(hasPrior());
		last.setEnabled(hasNext());
	}

}