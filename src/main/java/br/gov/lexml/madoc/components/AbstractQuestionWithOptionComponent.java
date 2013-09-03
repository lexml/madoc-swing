package br.gov.lexml.madoc.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.gov.lexml.madoc.schema.Constants;
import br.gov.lexml.madoc.schema.entity.BaseOptionType;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerOptionType;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerType;
import br.gov.lexml.madoc.schema.entity.QuestionType;

public abstract class AbstractQuestionWithOptionComponent 
	<Q extends QuestionType, 
	 C,
	 OptionCompSw extends OptionComponent
	 						<? extends QuestionComponent<? extends QuestionType, ?>,
	 						 ? extends BaseOptionType, 
	 						 ?>>
	extends AbstractQuestionComponent<Q, C>{
	
	private LinkedHashMap<String, OptionCompSw> optionsMap;
	
	public AbstractQuestionWithOptionComponent(
			Q question,
			ComponentController componentController) {
		super(question, componentController);
	}

	/**
	 * Helper class for multilined values.
	 * @author lauro
	 *
	 */
	public final static class MultiLineValueBuilderHelper {
		private StringBuilder sb = new StringBuilder();
		
		public void add(String s){
			if (s!= null && !s.equals("")){
				if (sb.length()> 0){
					sb.append(Constants.SPLIT_TOKEN_VALUES);
				}
				sb.append(s);
			}
		}
		public String getResult(){
			return sb.toString();
		}
		@Override
		public String toString() {
			return getResult();
		}
		
		public static List<String> explode(String value){
			return Arrays.asList(value.split(Constants.SPLIT_TOKEN_VALUES));
		}
	}
	
	protected abstract C createComponentWithOptions();
	
	protected abstract LinkedHashMap<String, OptionCompSw> createOptionsComponents();

	protected final Map<String, OptionCompSw> getOptionsMap(){
		return this.optionsMap;
	}

	@Override
	protected final C createComponent() {
		this.optionsMap = createOptionsComponents();
		
		return createComponentWithOptions();
	}

	@Override
	public void setAnswer(QuestionAnswerType answer) {
		super.setAnswer(answer);
		
		if (answer.isHasOptions()){
			for (QuestionAnswerOptionType option : answer.getOptions().getOption()){
				setVisible(option.getId(), option.isVisible());
				setEnabled(option.getId(), option.isEnabled());
			}
		}
	}

	@Override
	public List<String> getIds() {
		List<String> list = new ArrayList<String>();
		
		//adding itself
		list.add(getId());
		
		//adding children
		for (OptionCompSw option : optionsMap.values()){
			if ((option.getId()!= null) && (!option.getId().equals(""))){
				list.add(option.getId());
			}
			
		}
		
		return list;
	}
	
	@Override
	public void setVisible(String id, boolean state) {
		OptionCompSw qoc = optionsMap.get(id);
		if (qoc== null){
			super.setVisible(id, state);
		} else {
			qoc.setVisible(state);
		}
	}

	@Override
	public void setEnabled(String id, boolean state) {
		OptionCompSw qoc = optionsMap.get(id);
		if (qoc== null){
			super.setEnabled(id, state);
		} else {
			qoc.setEnabled(state);
		}	
	}
	
	@Override
	protected boolean isRequiredValueReached() {
		
		
		if(!super.isRequiredValueReached()) {
			return false;
		}
		
		String value = getValue();
		for(OptionCompSw qoc: optionsMap.values()) {
			if(qoc.getValue().equals(value)) {
				return qoc.getWizardElement().isEnabled() &&
						qoc.getWizardElement().isVisible();
			}
		}
		
		return true;
		
	}
}
