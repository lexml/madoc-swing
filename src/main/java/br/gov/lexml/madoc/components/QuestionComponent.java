package br.gov.lexml.madoc.components;

import java.util.List;

import br.gov.lexml.madoc.schema.entity.QuestionAnswerType;
import br.gov.lexml.madoc.schema.entity.QuestionType;

public interface QuestionComponent<Q extends QuestionType, C> extends BaseWizardComponent<Q, C> {
	
	List<String> getIds();

	void setVisible(String id, boolean state);
	
	void setEnabled(String id, boolean state);

	void setRequired(boolean state);

	boolean isRequiredValueSet();
	
	void setHighlight(boolean state);

	void setHint(String hint);

	void setValue(String value);
	
	void addValue(String value);
	
	QuestionAnswerType getAnswer();
	
	void setAnswer(QuestionAnswerType answer);

	/**
	 * Syntactic sugar for getAnswer().getValue()
	 * @return
	 */
	String getValue();

}
