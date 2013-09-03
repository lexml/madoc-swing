package br.gov.lexml.madoc.components.swing;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.JPanel;

import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.schema.entity.ButtonType;
import br.gov.lexml.madoc.schema.entity.CheckBoxGroupQuestionType;
import br.gov.lexml.madoc.schema.entity.CheckBoxQuestionType;
import br.gov.lexml.madoc.schema.entity.ChoiceListQuestionType;
import br.gov.lexml.madoc.schema.entity.ComboQuestionType;
import br.gov.lexml.madoc.schema.entity.DateQuestionType;
import br.gov.lexml.madoc.schema.entity.DecimalQuestionType;
import br.gov.lexml.madoc.schema.entity.HtmlContentType;
import br.gov.lexml.madoc.schema.entity.InputTextQuestionType;
import br.gov.lexml.madoc.schema.entity.IntegerQuestionType;
import br.gov.lexml.madoc.schema.entity.MemoTextQuestionType;
import br.gov.lexml.madoc.schema.entity.RadioBoxGroupQuestionType;
import br.gov.lexml.madoc.schema.entity.RichTextQuestionType;
import br.gov.lexml.madoc.schema.entity.SectionType;
import br.gov.lexml.madoc.schema.entity.TextListQuestionType;
import br.gov.lexml.madoc.schema.entity.visitor.BaseVisitor;
import br.gov.lexml.madoc.schema.entity.visitor.VisitorAction;

class PagesComponentSwingVisitor extends BaseVisitor {
	
	private final ComponentController controller;

	private SectionComponentSwing currentSection;
	private Set<SectionComponentSwing> listSection = new LinkedHashSet<SectionComponentSwing>();
	private JPanel cardLayoutMaximizePanel;

	public PagesComponentSwingVisitor(ComponentController controller, JPanel cardLayoutMaximizePanel){
		this.controller = controller;
		this.currentSection = new SectionComponentSwing(controller);
		this.cardLayoutMaximizePanel = cardLayoutMaximizePanel;
	}
	
	public Set<SectionComponentSwing> getSetSections(){
		return listSection;
	}
	
	private VisitorAction returnContinue(QuestionComponentSwing<?,?> question){
		currentSection.add(question);
		
		listSection.add(this.currentSection);
		
		return VisitorAction.CONTINUE;
	}
	
	private VisitorAction returnContinue(CommandComponentSwing<?,?> command){
		currentSection.add(command);
		
		listSection.add(this.currentSection);
		
		return VisitorAction.CONTINUE;
	}
	
	private VisitorAction returnContinue(HtmlContentComponentSwing htmlContent){
		currentSection.add(htmlContent);
		
		listSection.add(this.currentSection);
		
		return VisitorAction.CONTINUE;
	}
	
	@Override
	public VisitorAction enter(SectionType aBean) {
		
		//creating a "real section"
		currentSection = new SectionComponentSwing(aBean, controller);
		
		return VisitorAction.CONTINUE;
	}

	@Override
	public VisitorAction leave(SectionType aBean) {

		//creating a "phantom section"
		currentSection = new SectionComponentSwing(controller);

		return VisitorAction.CONTINUE;
	}


	/* QuestionType */

	@Override
	public VisitorAction enter(InputTextQuestionType aBean) {

		return returnContinue(new InputTextQuestionComponentSwing(aBean, controller));
	}

	@Override
	public VisitorAction enter(MemoTextQuestionType aBean) {

		return returnContinue(new MemoTextQuestionComponentSwing(aBean, controller));
	}

	@Override
	public VisitorAction enter(RichTextQuestionType aBean) {

		return returnContinue(new RichTextQuestionComponentSwing(aBean, controller, cardLayoutMaximizePanel));
	}

	@Override
	public VisitorAction enter(IntegerQuestionType aBean) {

		return returnContinue(new IntegerQuestionComponentSwing(aBean, controller));
	}

	@Override
	public VisitorAction enter(DecimalQuestionType aBean) {

		return returnContinue(new DecimalQuestionComponentSwing(aBean, controller));
	}

	@Override
	public VisitorAction enter(DateQuestionType aBean) {
		return returnContinue(new DateQuestionComponentSwing(aBean, controller));
	}

	@Override
	public VisitorAction enter(CheckBoxQuestionType aBean) {

		return returnContinue(new CheckBoxQuestionComponentSwing(aBean, controller));
	}

	@Override
	public VisitorAction enter(ComboQuestionType aBean) {

		return returnContinue(new ComboQuestionComponentSwing(aBean, controller));
	}

	@Override
	public VisitorAction enter(TextListQuestionType aBean) {

		return returnContinue(new TextListQuestionComponentSwing(aBean, controller));
	}

	@Override
	public VisitorAction enter(ChoiceListQuestionType aBean) {
		return returnContinue(new ChoiceListQuestionComponentSwing(aBean, controller));
	}

	@Override
	public VisitorAction enter(CheckBoxGroupQuestionType aBean) {
		return returnContinue(new CheckBoxGroupQuestionComponentSwing(aBean, controller));
	}

	@Override
	public VisitorAction enter(RadioBoxGroupQuestionType aBean) {
		return returnContinue(new RadioBoxGroupQuestionComponentSwing(aBean, controller));
	}
	
	@Override
	public VisitorAction enter(ButtonType aBean) {
		return returnContinue(new ButtonComponentSwing(aBean, controller));
	}
	
	public VisitorAction enter(HtmlContentType aBean) {
		return returnContinue(new HtmlContentComponentSwing(aBean, controller));
	}

}
