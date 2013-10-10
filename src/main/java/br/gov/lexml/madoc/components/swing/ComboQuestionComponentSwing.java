package br.gov.lexml.madoc.components.swing;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.components.swing.jcomponents.QuestionPanel;
import br.gov.lexml.madoc.schema.entity.ComboQuestionType;
import br.gov.lexml.madoc.schema.entity.ObjectFactory;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerOptionType;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerOptionsType;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerType;
import br.gov.lexml.madoc.schema.entity.SelectOptionType;
import br.gov.lexml.madoc.schema.entity.SelectOptionsType;

class ComboQuestionComponentSwing 
	extends AbstractQuestionWithOptionComponentSwing<ComboQuestionType, OptionComboItemComponent>{

	private JComboBox combo;
	private DefaultComboBoxModel comboBoxModel;
	
	private OptionComboItemComponent selectedComboItemComponent;

	public ComboQuestionComponentSwing(ComboQuestionType question, 
			ComponentController componentController) {
		super(question, componentController);
	}

	public void setSelected(OptionComboItemComponent item){
		selectedComboItemComponent = item;
		
		if (combo!= null){
			combo.setSelectedItem(selectedComboItemComponent);
		}
	}
	
	@Override
	protected LinkedHashMap<String, OptionComboItemComponent> createOptionsComponents() {

		LinkedHashMap<String, OptionComboItemComponent> optionComponentsMap = new LinkedHashMap<String, OptionComboItemComponent>();
		List<OptionComboItemComponent> optionComponentsList = new ArrayList<OptionComboItemComponent>();

		SelectOptionsType options = wizardElement.getOptions();
		if (options!= null){
			for (SelectOptionType option : options.getOption()) {
				OptionComboItemComponent comboItemComponent = new OptionComboItemComponent(this, option);

				if (option.getId()!= null && !option.getId().equals("")){
					optionComponentsMap.put(option.getId(), comboItemComponent);
				}
				optionComponentsList.add(comboItemComponent);
			}
		}

		return optionComponentsMap;
	}
	
	@Override
	protected QuestionPanel createComponentWithOptions() {
		QuestionPanel panel = AbstractQuestionComponentSwing.createDefaultQuestionPanel(wizardElement, componentController.getCatalogService());

		comboBoxModel = new DefaultComboBoxModel(getOptionsMap().values().toArray());
		combo = new JComboBox(comboBoxModel);
		
		combo.setAlignmentX(JComboBox.LEFT_ALIGNMENT);

		
		//add listener
		ActionListener listener = AbstractQuestionComponentSwing.createDefaultListener(this);
		if (listener!= null){
			combo.addActionListener(listener);
		}

		// Importante estar após o addListener
		if (selectedComboItemComponent!= null){
			combo.setSelectedItem(selectedComboItemComponent);
		}

		panel.add(combo);

		return panel;
	}

	@Override
	protected QuestionAnswerType createAnswer(QuestionAnswerType qat) {
		
		QuestionAnswerOptionsType qaOptions = new ObjectFactory().createQuestionAnswerOptionsType();
		
		for (OptionComboItemComponent option : getOptionsMap().values()){
			QuestionAnswerOptionType qaot = option.getAnswerOptionType();
			qaOptions.getOption().add(qaot);
		}
		
		qat.setOptions(qaOptions);
		qat.setHasList(false);
		qat.setHasOptions(true);
		
		qat.setValue(getValue());
		
		return qat;
	}
	
	@Override
	public String getValue() {
		OptionComboItemComponent selItem = (OptionComboItemComponent) combo.getModel().getSelectedItem();
		if (selItem!= null && (selItem instanceof OptionComboItemComponent)){
			return selItem.getValue();
		}
		return "";
	}

	@Override
	protected void answerUpdated(QuestionAnswerType qat) {
		setValue(qat.getValue());
	}
	
	@Override
	public void setValue(String value) {
		for (OptionComboItemComponent ocic : getOptionsMap().values()){
			if (ocic.getWizardElement().getValue().equals(value)){
				comboBoxModel.setSelectedItem(ocic);
				break;
			}
		}
	}
	
	@Override
	public void addValue(String value) {
		setValue(value);
	}

	@Override
	public void selectOption(String optionId) {
		for (OptionComboItemComponent ocic : getOptionsMap().values()){
			if (ocic.getWizardElement().getId().equals(optionId)){
				comboBoxModel.setSelectedItem(ocic);
				break;
			}
		}
	}
	
	@Override
	public String getSelectedOptionId() {
		Object selectedItem = comboBoxModel.getSelectedItem();
		if(selectedItem != null) {
			return ((OptionComboItemComponent)selectedItem).getId();
		}
		return null;
	}

}
