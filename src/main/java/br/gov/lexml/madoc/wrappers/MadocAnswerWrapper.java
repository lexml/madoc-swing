package br.gov.lexml.madoc.wrappers;

import br.gov.lexml.madoc.schema.entity.MadocAnswerType;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerType;

/**
 * Helper class with useful methods on MadocAnswerType.
 * 
 * @author lauro
 *
 */
public class MadocAnswerWrapper {

	private final MadocAnswerType answer;
	
	public MadocAnswerWrapper(MadocAnswerType answer) {
		this.answer = answer;
	}
	
	public QuestionAnswerType getQuestionAnswerById(String id) {
		for (QuestionAnswerType qa : answer.getQuestionsAnswers().getQuestionAnswer()) {
			if (qa.getId().equals(id)) {
				return qa;
			}
		}
		return null;
	}
	
	public String getValueById(String id) {
		QuestionAnswerType qa = getQuestionAnswerById(id);
		return qa == null? null: qa.getValue();
	}
	
}
