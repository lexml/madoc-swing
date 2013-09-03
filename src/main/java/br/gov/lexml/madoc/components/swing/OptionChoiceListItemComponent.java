package br.gov.lexml.madoc.components.swing;

import javax.swing.JComponent;

import br.gov.lexml.madoc.components.AbstractOptionComponent;
import br.gov.lexml.madoc.schema.entity.ChoiceListOptionType;

class OptionChoiceListItemComponent 
	extends AbstractOptionComponent<ChoiceListQuestionComponentSwing, ChoiceListOptionType, JComponent>
	implements 
		OptionComponentSwing<
			ChoiceListQuestionComponentSwing, 
			ChoiceListOptionType, 
			JComponent>{

	private ComponentOptionItem componentOptionItem;
	
	public OptionChoiceListItemComponent(ChoiceListQuestionComponentSwing questionComponent, ChoiceListOptionType optionType) {
		super(questionComponent, optionType);
	}
	
	@Override
	protected JComponent createComponent() {
		componentOptionItem = new ComponentOptionItem();
		return componentOptionItem.setValue(getValue());
	}
	
	@Override
	public void setDisplay(String caption) {
		if (componentOptionItem!= null){
			componentOptionItem.setValue(caption);
		}
	}

	@Override
	public void setVisible(boolean state) {
		// there is not way to set this	
	}

	@Override
	public void setEnabled(boolean state) {
		// there is not way to set this	
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		
		if (super.equals(obj)){
			return true;
		}
		
		if (obj instanceof OptionChoiceListItemComponent){
			OptionChoiceListItemComponent bwc = ((OptionChoiceListItemComponent)obj);
			if (bwc.getId()!= null && !bwc.getId().equals("") && getId()!=null && !getId().equals("")){
				return ((OptionChoiceListItemComponent)obj).getId().equals(getId());
			}
		}
		
		return false;
	}
	
	
}
