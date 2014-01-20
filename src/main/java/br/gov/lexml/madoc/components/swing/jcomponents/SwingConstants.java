package br.gov.lexml.madoc.components.swing.jcomponents;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.UIManager;

public final class SwingConstants {
	
	public static final int PAGE_MAXIMUM_WIDTH = 700;

	public static final Color LABEL_COLOR = new JLabel().getBackground();
	public static final Font BOLD_FONT = UIManager.getFont("TextField.font").deriveFont(Font.BOLD);
	public static final Font TITLE_FONT = BOLD_FONT.deriveFont(14f);
	public static final Font TOOLTIP_FONT = UIManager.getFont("TextField.font").deriveFont(11f);
	
	public static final Color REQUIRED_PANEL_COLOR = Color.RED;
	public static final int REQUIRED_PANEL_THICKNESS = 2; 
	
	public static final String CLIENT_PROPERTY_INPUT_TEXT = "inputText";
	public static final String CLIENT_PROPERTY_VALUE = "value";
	public static final String CLIENT_PROPERTY_BASEWIZARDTYPE = "baseWizardType";
	public static final String CLIENT_PROPERTY_PAGETYPE = "pageType";
	
	public static final String BUTTON_CAPTION_PRIOR_PAGE = "Anterior";
	public static final String BUTTON_CAPTION_NEXT_PAGE = "Próxima";
	public static final String BUTTON_CAPTION_FIRST_PAGE = "Primeira";
	public static final String BUTTON_CAPTION_LAST_PAGE = "Última";
	public static final String LABEL_CAPTION_PAGE = "Página  ";
	public static final String LABEL_CAPTION_PAGE_FROM = "  de  ";

	/**
	 * No construction
	 */
	private SwingConstants(){
	}

}
