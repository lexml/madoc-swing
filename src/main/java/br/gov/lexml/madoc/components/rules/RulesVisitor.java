package br.gov.lexml.madoc.components.rules;

import br.gov.lexml.madoc.components.CommandComponent;
import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.components.QuestionComponent;
import br.gov.lexml.madoc.schema.Constants;
import br.gov.lexml.madoc.schema.entity.ActionListType;
import br.gov.lexml.madoc.schema.entity.ActionType;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerOptionType;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerOptionsType;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerType;
import br.gov.lexml.madoc.schema.entity.SimpleRuleType;
import br.gov.lexml.madoc.schema.entity.SwitchCaseRuleType;
import br.gov.lexml.madoc.schema.entity.SwitchRuleType;
import br.gov.lexml.madoc.schema.entity.visitor.BaseVisitor;
import br.gov.lexml.madoc.schema.entity.visitor.VisitorAction;

/**
 * Implement each type of RuleType (Visitor pattern)
 *
 * @author lauro
 *
 */
public class RulesVisitor extends BaseVisitor {

	private final QuestionComponent<?,?> questionComponent;
	private final ComponentController componentController;

	/**
	 * RulesVisitor for a Wizard Rules
	 * @param componentController
	 * @param when
	 */
	public RulesVisitor(ComponentController componentController){
		this.questionComponent = null;
		this.componentController = componentController;
	}

	public RulesVisitor(QuestionComponent<?,?> questionComponent, ComponentController componentController){
		this.questionComponent = questionComponent;
		this.componentController = componentController;
	}

	/**
	 * RulesVisitor for a CommandComponent. Executes only BaseRuleWhenType.ON_CHANGE rules.
	 * @param commandComponent
	 * @param componentController
	 */
	public RulesVisitor(CommandComponent<?,?> commandComponent, ComponentController componentController){
		this.questionComponent = null;
		this.componentController = componentController;
	}

	/**
	 * SimpleRuleType has the following semantic:
	 *  - it always executes
	 */
	public VisitorAction enter(SimpleRuleType aBean) {

		ActionVisitor actionVisitor = new ActionVisitor(componentController);

		for (ActionType action : aBean.getAction()){
			action.accept(actionVisitor);
		}

		return VisitorAction.CONTINUE;
	}

	/**
	 * SwitchRuleType has the following semantic:
	 *  - the questionValue might have multiple values, separated by commas (,), like in questionValue="x,y,z"
	 * 	- each CASE (of the type ActionListType) is executed when all of its questionValue="x,y,z" are true
	 *  - the OTHERWISE (of the type ActionListType) is executed when no CASE are true
	 * When SwitchRuleType is set on a CommandType, SwitchRuleType is executed only when questionId is set
	 *
	 */
	@Override
	public VisitorAction enter(SwitchRuleType aBean) {

			// getting Question

			final QuestionComponent<?,?> wizardElement;
			if (!aBean.isSetQuestionId()){
				wizardElement = this.questionComponent;
			} else {
				if (componentController== null){
					wizardElement = null;
				} else {
					wizardElement = componentController.getQuestionComponentById(aBean.getQuestionId());
				}
			}

			if (wizardElement== null){
				return VisitorAction.CONTINUE;
			}

			// getting component answer

			QuestionAnswerType answer = wizardElement.getAnswer();

			if (answer== null){
				return VisitorAction.CONTINUE;
			}

			String componentValue = answer.getValue();

			// if valueFound = true, there's no otherwise
			boolean valueFound = false;

			// for each case
			for (ActionListType action : aBean.getCase()) {

				// getting question value
				String questionValue = ((SwitchCaseRuleType) action).getQuestionValue();
				String attributeToTest = ((SwitchCaseRuleType) action).getAttributeToTest();

				boolean allSelectedExternal = true;
				if(attributeToTest.equals("optionId")) {

					String selectedOptionId = "";
					QuestionAnswerOptionsType options = answer.getOptions();
					for(QuestionAnswerOptionType o: options.getOption()) {
						if(o.isSelected()) {
							selectedOptionId = o.getId();
							break;
						}
					}

					allSelectedExternal = questionValue.trim().equals(selectedOptionId.trim());

				}
				else {
					for (String questionOneValue : questionValue.split(Constants.SPLIT_TOKEN_VALUES)) {
						boolean internalFound = false;
						for (String componentValueSelected : componentValue.split(Constants.SPLIT_TOKEN_VALUES)) {

							if (questionOneValue.trim().equals(componentValueSelected.trim())) {
								internalFound = true;

								break;
							}
						}
						if (!internalFound) {
							allSelectedExternal = false;
							break;
						}
					}
				}
				if (allSelectedExternal) {
					ActionVisitor actionVisitor = new ActionVisitor(wizardElement.getComponentController());
					action.accept(actionVisitor);
					valueFound = true;
				}
			}

			// treating otherwise
			if (aBean.isSetOtherwise() && !valueFound) {
				ActionVisitor actionVisitor = new ActionVisitor(wizardElement.getComponentController());
				aBean.getOtherwise().accept(actionVisitor);
			}

		return VisitorAction.CONTINUE;
	}
}