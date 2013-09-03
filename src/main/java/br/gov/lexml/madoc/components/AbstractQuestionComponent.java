package br.gov.lexml.madoc.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.gov.lexml.madoc.schema.entity.BaseWizardType;
import br.gov.lexml.madoc.schema.entity.ObjectFactory;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerType;
import br.gov.lexml.madoc.schema.entity.QuestionType;

public abstract class AbstractQuestionComponent 
	<Q extends QuestionType,
	 C> 
	extends AbstractBaseWizardComponent<Q, C> 
	implements QuestionComponent<Q, C> {

	private static final Logger log = LoggerFactory.getLogger(AbstractQuestionComponent.class);
	
	protected QuestionAnswerType questionAnswer;

	public AbstractQuestionComponent(Q question, ComponentController componentController) {
		super(question, componentController);
	}
	
	protected abstract QuestionAnswerType createAnswer(QuestionAnswerType qat);
	
	protected abstract void answerUpdated(QuestionAnswerType qat);
	
	@Override
	public String getValue() {
		QuestionAnswerType a = getAnswer();
		if (a!= null){
			return a.getValue(); 
		}
		return null;
	}
	
	@Override
	public List<String> getIds() {
		List<String> list = new ArrayList<String>();
		list.add(getId());
		
		return list;
	}
	
	@Override
	public void setVisible(String id, boolean state) {
		if (id.equals(getId())){
			setVisible(state);
		}
	}

	@Override
	public void setEnabled(String id, boolean state) {
		if (id.equals(getId())){
			setEnabled(state);
		}
	}
	
	@Override
	public QuestionAnswerType getAnswer() {
		QuestionAnswerType qat = new ObjectFactory().createQuestionAnswerType();
		qat.setId(wizardElement.getId());
		qat.setEnabled(wizardElement.isEnabled());
		qat.setVisible(wizardElement.isVisible());

		return createAnswer(qat);
	}
	
	@Override
	public void setAnswer(QuestionAnswerType answer) {
		this.questionAnswer = answer;
		
		setVisible(answer.isVisible());
		setEnabled(answer.isEnabled());
		
		answerUpdated(answer);
	}
	
	@Override
	public final boolean isRequiredValueSet() {

		boolean resultBase = isRequiredValueReachedBaseVerification();
		
		if (resultBase){
			
			if (log.isDebugEnabled()){
				log.debug("isRequiredValueReached for "+wizardElement.getId()+": base verification=true");
			}

			return true;
		}
		
		boolean resultAdditional = isRequiredValueReached();

		if (log.isDebugEnabled()){
			log.debug("isRequiredValueReached for "+wizardElement.getId()+": base verification= "+(resultBase ? "true" : "false")+"; additional= "+(resultAdditional ? "true" : "false"));
		}
		
		return resultAdditional;
	}

	/**
	 * Tests additional rules for required value
	 * @return
	 */
	protected boolean isRequiredValueReached() {
		
		if (StringUtils.isEmpty(getValue().trim())){
			return false;
		}

		return true;
	}
	
	/**
	 * Tests if required value for this question is already reached based on common verifications.
	 * @return
	 */
	private boolean isRequiredValueReachedBaseVerification(){
		boolean result = false;
		
		// if one of the parents is not visible or enabled, the return is true
		List<BaseWizardComponent<?, ?>> parents = componentController.getParentsOf(this);
		if (parents!= null){
			for (BaseWizardComponent<?,?> bwc : parents){
				BaseWizardType bwe = bwc.getWizardElement();
				if (!bwe.isVisible() || !bwe.isEnabled()){
					result = true;
				}
			}
		}
		
		// if this question is not required, visible or enabled, the return is true 
		if (!result && (!wizardElement.isRequired() || !wizardElement.isVisible() || !wizardElement.isEnabled())){
			result = true;
		}
		
		return result;
	}
	
}
