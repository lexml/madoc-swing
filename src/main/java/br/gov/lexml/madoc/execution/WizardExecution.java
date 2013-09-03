package br.gov.lexml.madoc.execution;

import br.gov.lexml.madoc.MadocException;
import br.gov.lexml.madoc.components.BaseWizardComponent;
import br.gov.lexml.madoc.components.PagesComponent;
import br.gov.lexml.madoc.execution.hosteditor.HostEditor;
import br.gov.lexml.madoc.schema.entity.DataSetMapType;
import br.gov.lexml.madoc.schema.entity.MadocAnswerType;
import br.gov.lexml.madoc.schema.entity.MadocDocumentType;

/**
 * Interface of wizard' execution. A wizard execution is a user interface. 
 * @author lauro
 *
 */
public interface WizardExecution<P extends PagesComponent<?, ? extends C>, C> {

	/**
	 * Returns MadocDocument used by components. This MadocDocument has already been expanded.
	 * @return
	 */
	MadocDocumentType getCurrentMadocDocument();
	
	/**
	 * Returns the original MadocDocument as read from XML
	 * @return
	 */
	MadocDocumentType getOriginalMadocDocument();

	/**
	 * Create a DocumentAnswerType representing user's interview answers.
	 */
	MadocAnswerType createMadocAnswer();
	
	/**
	 * Set a metadata to be used by getMadocAnswer() 
	 * @param metadata
	 */
	void setAnswerMedatada(DataSetMapType metadata);
	
	/**
	 * Returns the answer's metadata loaded on constructor that will be used by createMadocAnswer()
	 * @return
	 */
	DataSetMapType getAnswerMetadata();
	
	/**
	 * Returns true if the document has been changed
	 * @return
	 */
	boolean isChanged();
	
	/**
	 * Returns true if all required fields are filled. It highlights required fields. 
	 * It's a shortcut to isRequiredQuestionsAnswered(true).
	 * @return
	 */
	boolean isRequiredQuestionsAnswered();
	
	/**
	 * Returns true if all required fields are filled. It highlights required fields and shows the 
	 * first panel with empty field when highlight parameter is set to true.  
	 * @return
	 */
	boolean isRequiredQuestionsAnswered(boolean highlight);
	
	/**
	 * Returns main component that might be shown to the user 
	 * @return
	 */
	C getComponent() throws MadocException;
	
	/**
	 * Set a MadocAnswerType represents data saved. 
	 * @param madocAnswer
	 */
	void informMadocAnswer(MadocAnswerType madocAnswer);
	
	/**
	 * Set HostEditor
	 * @param hostEditor
	 */
	void setHostEditor(HostEditor hostEditor);
	
	/**
	 * @return HostEditor
	 */
	HostEditor getHostEditor();
	
	/**
	 * Set question value
	 * @param questionId
	 * @param value
	 */
	void setQuestionValue(String questionId, String value);
	
	/**
	 * Returns a BaseWizardComponent given its id.
	 * @param id
	 * @return
	 */
	BaseWizardComponent<?, ?> getBaseWizardComponentById(String id);	
	
	/**
	 * Add question value
	 * @param questionId
	 * @param value
	 */
	void addQuestionValue(String questionId, String value);
	
}
