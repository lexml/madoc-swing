package br.gov.lexml.madoc.execution.swing;

import javax.swing.JPanel;

import br.gov.lexml.madoc.MadocException;
import br.gov.lexml.madoc.catalog.CatalogService;
import br.gov.lexml.madoc.components.swing.PagesComponentSwing;
import br.gov.lexml.madoc.execution.AbstractWizardExecution;
import br.gov.lexml.madoc.schema.entity.MadocAnswerType;
import br.gov.lexml.madoc.schema.entity.MadocReferencesAnswersType;

/**
 * WizardExecutionSwing is a user swing interface.
 * 
 * @author lauro
 * 
 */
public class WizardExecutionSwing extends
		AbstractWizardExecution<PagesComponentSwing, JPanel> {

	public WizardExecutionSwing(
			CatalogService catalogService,
			String madocId, 
			String documentVersion)
			throws MadocException {
		super(catalogService, madocId, documentVersion);
	}

	public WizardExecutionSwing(
			CatalogService catalogService,
			String madocId) throws MadocException {
		super(catalogService, madocId);
	}

	public WizardExecutionSwing(
			CatalogService catalogService,
			MadocAnswerType originalAnswer) throws MadocException {
		super(catalogService, originalAnswer);
	}
	
	public WizardExecutionSwing(
			CatalogService catalogService,
			MadocReferencesAnswersType madocReferences) throws MadocException {
		super(catalogService, madocReferences);
	}
	
	@Override
	protected PagesComponentSwing createComponent() throws MadocException {

		final PagesComponentSwing pages= 
				new PagesComponentSwing(getCurrentMadocDocument().getWizard().getPages(), controller);
		
		pages.getComponent().setButtonsPanelVisible(pages.pagesSize() > 1);
		pages.getComponent().showFirstPagePanel();

		return pages;

	}

}
