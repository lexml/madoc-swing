package br.gov.lexml.madoc.components;

import java.util.List;

import br.gov.lexml.madoc.schema.entity.MadocAnswerType;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerType;
import br.gov.lexml.madoc.schema.entity.VariablesAnswersType;

public interface PagesComponent<P extends PageComponent<?,?>, C> {

	C getComponent();
	
	List<QuestionAnswerType> getQuestionsAnswers();
	
	VariablesAnswersType getVariablesAnswers();
	
	/**
	 * Process all components with MadocAnswerType
	 * @param madocAnswer
	 */
	void informMadocAnswer(MadocAnswerType madocAnswer);
	
	/**
	 * Process onLoadRules for all questions  
	 */
	void processOnLoadRulesFromQuestions();
	
	/**
	 * Process onChangeRules for all questions  
	 */
	void processOnChangeRulesFromQuestions();
	
	/**
	 * Change the active page of PagesControl based on QuestionComponent
	 * @param firstQuestion
	 */
	void showPageOfThisQuestionComponent(QuestionComponent<?,?> question);
	
	void scrollToQuestion(QuestionComponent<?,?> question);
	
	/**
	 * Return the list of QuestionComponent inside this PagesComponent 
	 * @return
	 */
	List<QuestionComponent<?, ?>> getQuestionComponents();
	
	/**
	 * Return the number of pages
	 * @return
	 */
	int pagesSize();
	
}
