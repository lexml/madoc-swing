package br.gov.lexml.madoc.rendition;

import br.gov.lexml.madoc.schema.Constants;
import br.gov.lexml.madoc.schema.entity.BaseOptionType;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerOptionType;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerType;
import br.gov.lexml.madoc.schema.entity.QuestionType;

final class VelocityExtensionQuestionsUtils {

	private VelocityExtensionQuestionsUtils(){}
	
	/**
	 * Returns the value from an id
	 * @param contextCollection
	 * @param id
	 * @return
	 */
	static String getValueOfId(ContextCollection contextCollection, String id){

		// 1 - getting from questions
		QuestionAnswerType qat = contextCollection.getAnswersMap().get(id);
		if(qat != null) {
			return qat.getValue();
		} 

		// 2 - getting from variables
		String val = contextCollection.getVariablesMap().get(id);
		if(val != null) {
			return val.toString();
		} 
		
		// 3 - getting from options
		QuestionAnswerOptionType option = contextCollection.getAnswersOptionsMap().get(id);
		if (option!= null){
			return option.getValue();
		}
		
		return null;
	}
	
	/**
	 * Returns the display from an id
	 * @param id
	 * @return
	 */
	static String getDisplayFromId(ContextCollection contextCollection, String id){
		
		// 1 - getting from questions
		QuestionType qt = contextCollection.getQuestionsMap().get(id);
		if(qt != null) {
			return qt.getDisplay();
		} 
		
		// 2 - getting from options
		BaseOptionType bot = contextCollection.getQuestionsOptionsMap().get(id);
		if (bot!= null){
			return bot.getDisplay();
		}
		
		// 3 - getting from variables
		String val = contextCollection.getVariablesMap().get(id);
		if(val != null) {
			return val.toString();
		}
		
		return null;
	}
	
	/**
	 * Returns true if the id is visible
	 * @param id
	 * @return
	 */
	static boolean isVisibleFromId(ContextCollection contextCollection, String id){
		
		// 1 - getting from questions
		QuestionAnswerType qt = contextCollection.getAnswersMap().get(id);
		if(qt != null) {
			return qt.isVisible();
		} 
		
		// 2 - getting from options
		QuestionAnswerOptionType bot = contextCollection.getAnswersOptionsMap().get(id);
		if (bot!= null){
			return bot.isVisible();
		}
		
		return true;
	}
	
	/**
	 * Returns the id that owns the value
	 * @param value
	 * @return
	 */
	static String getOptionsDisplayFromQuestionId(ContextCollection contextCollection, Object questionId){

		QuestionAnswerType qAnswer = contextCollection.getAnswersMap().get(questionId);
		if (qAnswer== null){
			return null;
		}
		
		StringBuilder r = new StringBuilder();
		boolean first = true;
		for (QuestionAnswerOptionType o : qAnswer.getOptions().getOption()){
			if (o.isSelected()){
				BaseOptionType bot = contextCollection.getQuestionsOptionsMap().get(o.getId());
				if (bot != null){
					if (!first){
						r.append(Constants.SPLIT_TOKEN_VALUES);
					}
					r.append(bot.getDisplay());
					first = false;
				}
			}
		}
		
		return r.toString();
	}
	
}
