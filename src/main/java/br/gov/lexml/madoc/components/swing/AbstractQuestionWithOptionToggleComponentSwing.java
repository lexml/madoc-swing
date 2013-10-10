package br.gov.lexml.madoc.components.swing;

import java.util.Collection;
import java.util.LinkedHashMap;

import javax.swing.JToggleButton;

import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.schema.Constants;
import br.gov.lexml.madoc.schema.entity.ObjectFactory;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerOptionType;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerOptionsType;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerType;
import br.gov.lexml.madoc.schema.entity.QuestionType;
import br.gov.lexml.madoc.schema.entity.SelectOptionType;
import br.gov.lexml.madoc.schema.entity.SelectOptionsType;
import br.gov.lexml.madoc.schema.entity.SelectQuestionType;

abstract class AbstractQuestionWithOptionToggleComponentSwing
	<Q extends SelectQuestionType,
	 C extends AbstractOptionToggleItemComponentSwing
	 		<? extends AbstractQuestionWithOptionToggleComponentSwing<? extends QuestionType,?>, ? extends JToggleButton>
	> 
	extends AbstractQuestionWithOptionComponentSwing<Q, C>{

	public AbstractQuestionWithOptionToggleComponentSwing(Q question,
			ComponentController componentController) {
		super(question, componentController);
	}
	
	protected abstract C createOptionToggleItemComponent(SelectOptionType selectOption);
	
	@Override
	protected LinkedHashMap<String, C> createOptionsComponents() {
		LinkedHashMap<String, C> map = new LinkedHashMap<String, C>();
		
		SelectOptionsType options = getWizardElement().getOptions();
		if (options!= null){
			for (SelectOptionType selectOption : options.getOption()){
				C ocbic = createOptionToggleItemComponent(selectOption);
				map.put(selectOption.getId(), ocbic);
			}
		}
		
		return map;
	}

	@Override
	protected QuestionAnswerType createAnswer(QuestionAnswerType qat) {
		qat.setHasList(false);
		qat.setHasOptions(true);
		
		MultiLineValueBuilderHelper valueBuilder = new MultiLineValueBuilderHelper();
		
		QuestionAnswerOptionsType qaOptions = new ObjectFactory().createQuestionAnswerOptionsType();
		
		for (C option : getOptionsMap().values()){
			QuestionAnswerOptionType qaot = option.getAnswerOptionType();
			
			qaOptions.getOption().add(qaot);
			
			if (qaot.isSelected()){
				valueBuilder.add(qaot.getValue());
			}
		}
		
		qat.setOptions(qaOptions);
		qat.setValue(valueBuilder.getResult());
		
		return qat;
	}

	@Override
	protected void answerUpdated(QuestionAnswerType qat) {
		
		if (qat.isHasOptions()){
			
			for (QuestionAnswerOptionType qaot : qat.getOptions().getOption()){
				C optionComponent = getOptionsMap().get(qaot.getId());
				
				if (optionComponent!= null){
					optionComponent.setSelected(qaot.isSelected());
					optionComponent.setInputTextValue(qaot.getValue());
				}	
			}
		}
		
	}
	
	@Override
	public void setValue(String value) {
		setValue(value, true);
	}
	
	@Override
	public void addValue(String value) {
		setValue(value, false);	
	}
	
	@Override
	public void selectOption(String optionId) {
		for (C o : getOptionsMap().values()){
			o.setSelected(o.getId().equals(optionId));
		}
	}
	
	@Override
	public String getSelectedOptionId() {
		for (C o : getOptionsMap().values()){
			if(o.isSelected()) {
				return o.getId();
			}
		}
		return null;
	}
	
	private void setValue(String value, boolean clear){
		Collection<C> optionsList = getOptionsMap().values();
		
		//clear all QuestionOptions
		if (clear){
			for (C o : optionsList){
				o.setSelected(false);
				o.setInputTextValue(o.getWizardElement().getInputDefaultValue());
			}
		}
		
		//for each value, set Optioncomponent
		for (String v : MultiLineValueBuilderHelper.explode(value)){
			for (C o : optionsList){
				boolean sel = v.equals(Constants.TRUE_STRING) || v.equals(o.getWizardElement().getValue());
				o.setSelected(sel);
				if (o.isSelected()){
					o.setInputTextValue(v);
				}
			}
		}	

	}
			
}
