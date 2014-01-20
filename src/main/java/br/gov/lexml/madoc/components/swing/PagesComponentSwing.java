package br.gov.lexml.madoc.components.swing;

import br.gov.lexml.madoc.components.AbstractPagesComponent;
import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.components.QuestionComponent;
import br.gov.lexml.madoc.components.swing.jcomponents.PagesControl;
import br.gov.lexml.madoc.schema.entity.PageType;
import br.gov.lexml.madoc.schema.entity.PagesType;

public class PagesComponentSwing 
	extends AbstractPagesComponent<PageComponentSwing, PagesControl> {


	public PagesComponentSwing(PagesType wizardElement,
			ComponentController controller) {
		super(wizardElement, controller);
	}

	@Override
	public void showPageOfThisQuestionComponent(QuestionComponent<?,?> question){
		for (PageComponentSwing page : listPages) {
			if (page.containsThisQuestionComponent(question)){
				component.showThisPagePanel(page.getComponent());
			}
		}
	}
	
	@Override
	public void scrollToQuestion(QuestionComponent<?, ?> question) {
		for (PageComponentSwing page : listPages) {
			if (page.containsThisQuestionComponent(question)){
				component.showThisPagePanel(page.getComponent());
				component.scrollToQuestion(question);
			}
		}
	}
	
	/**
	 * Creates main PagesControl
	 * @return
	 */
	@Override
	protected PagesControl createComponent() {

		//creating PagesControl
		PagesControl pages = new PagesControl();
		
		//creating component factory
		//creating sections for each page
		for (PageType pageType : wizardElement.getPage()){
			PagesComponentSwingVisitor pcv = new PagesComponentSwingVisitor(controller, pages);
			pageType.accept(pcv);
			
			PageComponentSwing pageComponent = new PageComponentSwing(pageType, controller);
			pageComponent.addSections(pcv.getSetSections());
			
			listPages.add(pageComponent);
		}

		for (PageComponentSwing pageComponent : listPages) {
			pages.addPage(pageComponent.getComponent());
		}
		
		return pages;
	}

}
