package br.gov.lexml.madoc.components.rules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.gov.lexml.madoc.components.BaseWizardComponent;
import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.components.QuestionComponent;
import br.gov.lexml.madoc.components.QuestionWithOptionComponent;
import br.gov.lexml.madoc.schema.entity.AddQuestionValueActionType;
import br.gov.lexml.madoc.schema.entity.ChangeCaptionActionType;
import br.gov.lexml.madoc.schema.entity.ChangeEnableActionType;
import br.gov.lexml.madoc.schema.entity.ChangeHintActionType;
import br.gov.lexml.madoc.schema.entity.ChangeRequiredActionType;
import br.gov.lexml.madoc.schema.entity.ChangeVisibilityActionType;
import br.gov.lexml.madoc.schema.entity.ConsumeRestServiceActionType;
import br.gov.lexml.madoc.schema.entity.SelectOptionActionType;
import br.gov.lexml.madoc.schema.entity.SetQuestionValueActionType;
import br.gov.lexml.madoc.schema.entity.SetVariableValueActionType;
import br.gov.lexml.madoc.schema.entity.visitor.BaseVisitor;
import br.gov.lexml.madoc.schema.entity.visitor.VisitorAction;
import br.gov.lexml.madoc.util.QuestionReplacerUtil;


/**
 * Implement each type of ActionType (Visitor pattern)
 * 
 * @author lauro
 * 
 */
class ActionVisitor extends BaseVisitor {

	private final ComponentController componentController;
	
	private static final Logger log = LoggerFactory.getLogger(ActionVisitor.class);
	
	public ActionVisitor(ComponentController componentController){
		this.componentController = componentController;
	}
	
	/**
	 * Expands HostEditor and QuestionsIdByQuestionsValue from value
	 * @param value
	 * @return
	 */
	private String expandValue(String value){
		return componentController.getHostEditorReplacer().replaceString(
				QuestionReplacerUtil.replaceQuestionsIdByQuestionsValues(value, componentController));
	}
	
	/*
	 * Change Strings
	 */
	
	@Override
	public VisitorAction enter(ChangeHintActionType aBean) {

		QuestionComponent<?, ?> q = componentController.getQuestionComponentById(aBean.getTargetId());

		if (q!= null){
			q.setHint(expandValue(aBean.getChangeTo()));
		}
		
		log.debug("ChangeHintActionType; targetId: "+aBean.getTargetId()+"; getChangeTo: "+aBean.getChangeTo()+(q== null ? "; question not found" : "; question found"));
		
		return VisitorAction.CONTINUE;
	}
	
	@Override
	public VisitorAction enter(SetQuestionValueActionType aBean) {
		
		log.debug("SetQuestionValueActionType; questionId: "+aBean.getQuestionId());

		componentController.setQuestionValue(aBean.getQuestionId(), expandValue(aBean.getValue()));
		
		return VisitorAction.CONTINUE;
	}
	
	@Override
	public VisitorAction enter(AddQuestionValueActionType aBean) {

		log.debug("AddQuestionValueActionType; questionId: "+aBean.getQuestionId());

		componentController.addQuestionValue(aBean.getQuestionId(), expandValue(aBean.getValue()));
	
		return VisitorAction.CONTINUE;
	}

	@Override
	public VisitorAction enter(SetVariableValueActionType aBean) {

		componentController.setVariable(aBean.getVariableName(), expandValue(aBean.getValue()));
		
		if (log.isDebugEnabled()){
			log.debug("SetVariableValueActionType"
						+"; variableName: "+aBean.getVariableName()
						+"; value: "+aBean.getValue() 
						+ "; valueReplaced: "+componentController.getHostEditorReplacer().replaceString(aBean.getValue()));
		}
		
		return VisitorAction.CONTINUE;
	}
	

	
	@Override
	public VisitorAction enter(ChangeCaptionActionType aBean) {
		
		BaseWizardComponent<?, ?> b = componentController.getBaseWizardComponentById(aBean.getTargetId());
			
		if (b!= null){
			b.setDisplay(expandValue(aBean.getChangeTo()));
		}
		
		log.debug("ChangeCaptionActionType; targetId: "+aBean.getTargetId()+"; changeTo: "+aBean.getChangeTo()+(b== null ? "; BaseWizard not found" : "; BaseWizard found"));

		return VisitorAction.CONTINUE;

	}
	
	/*
	 * Consume Rest Service
	 */
	
	@Override
	public VisitorAction enter(ConsumeRestServiceActionType aBean) {
		
		log.debug("ConsumeRestServiceActionType start");
		
		ConsumeRestServiceAction crsa = new ConsumeRestServiceAction(aBean, componentController);
		crsa.process();

		log.debug("ConsumeRestServiceActionType end");
		
		return VisitorAction.CONTINUE;
	}

	/*
	 * ChangeBooleanActionType
	 * @see br.gov.lexml.madoc.schema.entity.ChangeBooleanActionType
	 */
	
	@Override
	public VisitorAction enter(ChangeEnableActionType aBean) {

		QuestionComponent<?, ?> q = componentController.getQuestionComponentById(aBean.getTargetId());
		
		if (q!= null){
			q.setEnabled(aBean.getTargetId(), aBean.isChangeTo());
		}
		
		log.debug("ChangeEnableActionType; targetId: "+aBean.getTargetId()+"; isChangeTo: "+aBean.isChangeTo()+(q== null ? "; question not found" : "; question found"));
		
		return VisitorAction.CONTINUE;
	}
	
	@Override
	public VisitorAction enter(ChangeVisibilityActionType aBean) {
		
		String type;
		
		QuestionComponent<?, ?> q = componentController.getQuestionComponentById(aBean.getTargetId());
		
		if (q != null){
			//if it is a question
			type = "question";
			
			q.setVisible(aBean.getTargetId(), aBean.isChangeTo());
		} else {
			//if it is a BaseWizardType
			type = "baseWizard";
			
			BaseWizardComponent<?, ?> b = componentController.getBaseWizardComponentById(aBean.getTargetId());
			
			if (b!= null){
				b.setVisible(aBean.isChangeTo());
			}
		}
		
		log.debug("ChangeVisibilityActionType; targetId: "+aBean.getTargetId()+"; isChangeTo: "+aBean.isChangeTo()+(q== null ? "; "+type+" not found" : "; "+type+" found"));

		return VisitorAction.CONTINUE;
	}
	
	@Override
	public VisitorAction enter(ChangeRequiredActionType aBean) {
		QuestionComponent<?, ?> q = componentController.getQuestionComponentById(aBean.getTargetId());

		if (q!= null){
			q.setRequired(aBean.isChangeTo());
		}
		
		log.debug("ChangeRequiredActionType; targetId: "+aBean.getTargetId()+"; isChangeTo: "+aBean.isChangeTo()+(q== null ? "; question not found" : "; question found"));
		
		return VisitorAction.CONTINUE;
	}
	
	
	@Override
	public VisitorAction enter(SelectOptionActionType aBean) {
		
		log.debug("SelectOptionActionType; questionId: " + aBean.getOptionId());

		componentController.selectOption(aBean.getOptionId());
	
		return VisitorAction.CONTINUE;
	}
	
}