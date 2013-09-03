package br.gov.lexml.madoc.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import br.gov.lexml.madoc.components.rules.RulesVisitor;
import br.gov.lexml.madoc.schema.entity.MadocAnswerType;
import br.gov.lexml.madoc.schema.entity.ObjectFactory;
import br.gov.lexml.madoc.schema.entity.PagesType;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerType;
import br.gov.lexml.madoc.schema.entity.RulesType;
import br.gov.lexml.madoc.schema.entity.VariableAnswerType;
import br.gov.lexml.madoc.schema.entity.VariablesAnswersType;

public abstract class AbstractPagesComponent<P extends PageComponent<?,?>, C> implements PagesComponent<P, C> {
	
	protected final PagesType wizardElement;
	protected final ComponentController controller;
	
	protected final C component;
	
	protected final List<P> listPages = new ArrayList<P>(); 
	
	public AbstractPagesComponent(PagesType wizardElement, ComponentController controller){
		this.wizardElement = wizardElement;
		this.controller = controller;
		
		this.component = createComponent();
	}
	
	protected abstract C createComponent();
	
	@Override
	public void informMadocAnswer(MadocAnswerType madocAnswer){
		controller.informMadocAnswers(madocAnswer);
	}
	
	@Override
	public void processOnLoadRulesFromQuestions() {
		//processing QuestionRules type
		for (QuestionComponent<?,?> questionComponent : controller.getQuestionComponents()){
			
			RulesVisitor rulesVisitor = new RulesVisitor(questionComponent, controller);
			
			//processing onLoadRules
			RulesType rulesOnLoad = questionComponent.getWizardElement().getOnLoadRules();
			if (rulesOnLoad!= null){
				rulesOnLoad.accept(rulesVisitor);
			}
		}
	}
	
	@Override
	public void processOnChangeRulesFromQuestions() {
		for (QuestionComponent<?,?> questionComponent : controller.getQuestionComponents()){
			
			RulesVisitor rulesVisitor = new RulesVisitor(questionComponent, controller);
			
			//processing onChangeRules
			RulesType rulesOnChange = questionComponent.getWizardElement().getOnChangeRules();
			if (rulesOnChange!= null){
				rulesOnChange.accept(rulesVisitor);
			}
		}
	}

	
	@Override
	public C getComponent() {
		return component;
	}
	
	@Override
	public List<QuestionAnswerType> getQuestionsAnswers() {
		
		List<QuestionAnswerType> listQuestionsAnswers = new ArrayList<QuestionAnswerType>();

		for (QuestionComponent<?, ?> question : controller.getQuestionComponents()){
			listQuestionsAnswers.add(question.getAnswer());
		}
		
		return listQuestionsAnswers;
	}
	
	@Override
	public VariablesAnswersType getVariablesAnswers(){
		VariablesAnswersType variablesAnswers = new ObjectFactory().createVariablesAnswersType();
		for (Entry<String, String> entry : controller.getVariables().entrySet()){
			VariableAnswerType variableAnswer = new ObjectFactory().createVariableAnswerType();
			variableAnswer.setVariableName(entry.getKey());
			variableAnswer.setValue(entry.getValue());

			variablesAnswers.getVariableAnswer().add(variableAnswer);
		}
		return variablesAnswers;
	}
	
	@Override
	public List<QuestionComponent<?, ?>> getQuestionComponents(){
		return controller.getQuestionComponents();
	}
	
	@Override
	public int pagesSize() {
		return listPages.size();
	}

}
