package br.gov.lexml.madoc.components.swing;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.components.swing.jcomponents.QuestionPanel;
import br.gov.lexml.madoc.schema.entity.ChoiceListOptionType;
import br.gov.lexml.madoc.schema.entity.ChoiceListQuestionType;
import br.gov.lexml.madoc.schema.entity.ObjectFactory;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerOptionType;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerOptionsType;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerType;
import br.gov.lexml.swing.componentes.ActionToGenListModelListener;
import br.gov.lexml.swing.componentes.DefaultListChoiceActionApprover;
import br.gov.lexml.swing.componentes.JListChoice2;

class ChoiceListQuestionComponentSwing extends 
	AbstractQuestionWithOptionComponentSwing<ChoiceListQuestionType, OptionChoiceListItemComponent>{	

	private JListChoice2<OptionChoiceListItemComponent> choice;

	private Set<OptionChoiceListItemComponent> movable; 
	
	public ChoiceListQuestionComponentSwing(ChoiceListQuestionType question,
			ComponentController componentController) {
		super(question, componentController);
	}

	@Override
	protected LinkedHashMap<String, OptionChoiceListItemComponent> createOptionsComponents() {
		
		LinkedHashMap<String, OptionChoiceListItemComponent> map = 
			new LinkedHashMap<String, OptionChoiceListItemComponent>();
		
		for (ChoiceListOptionType clot : wizardElement.getOptions().getOption()){
			OptionChoiceListItemComponent cloic = new OptionChoiceListItemComponent(this, clot); 
			
			map.put(clot.getId(), cloic);
		}
		
		return map;
	}
	
	@Override
	protected QuestionPanel createComponentWithOptions() {
		QuestionPanel panel = AbstractQuestionComponentSwing.createDefaultQuestionPanel(wizardElement, componentController.getCatalogService());
		
		if (movable == null){
			movable = new HashSet<OptionChoiceListItemComponent>();
		}
		
		choice = JListChoice2.apply();

		populateChoiceModels();
		
		choice.setApprover(new DefaultListChoiceActionApprover<OptionChoiceListItemComponent>() {
			@Override
			public boolean additionApproved(final OptionChoiceListItemComponent e) {
				return movable.contains(e);
			}
			@Override
			public boolean removalApproved(final OptionChoiceListItemComponent e, final int p) {
				return movable.contains(e);
			}
		});
		
		//add listener
		ActionListener listener = AbstractQuestionComponentSwing.createDefaultListener(this);
		if (listener!= null){
			
			ActionToGenListModelListener<OptionChoiceListItemComponent> listenerChoice = 
					new ActionToGenListModelListener<OptionChoiceListItemComponent>(listener);
			
			choice.getDestModel().addListener(listenerChoice);
			choice.getDomainModel().addListener(listenerChoice);
		}
		
		panel.add(choice);

		return panel;
	}

	
	private void populateChoiceModels() {

		final Collection<OptionChoiceListItemComponent> options = getOptionsMap().values();
		/*
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			*/
				choice.getDomainModel().replaceWith(options);

				choice.getDestModel().clear();
				
				movable.clear();
				
				List<OptionChoiceListItemComponent> l = new ArrayList<OptionChoiceListItemComponent>();
				for (OptionChoiceListItemComponent qocs : options) {
					if (qocs.getWizardElement().isSelected()) {
						l.add(qocs);
					}
					
					if (qocs.getWizardElement().isEnabled()) {						
						movable.add(qocs);
					}
				}		
				choice.getDestModel().addAll(l);
			/*}
		});
		*/
		
	}	
	
	private QuestionAnswerOptionType createQuestionAnswerOptionType(ChoiceListOptionType clot, boolean selected){
		QuestionAnswerOptionType qaot = new ObjectFactory().createQuestionAnswerOptionType();
		qaot.setId(clot.getId());
		qaot.setEnabled(clot.isEnabled());
		qaot.setVisible(clot.isVisible());
		qaot.setValue(clot.getValue());
		qaot.setSelected(selected);
		return qaot;
	}
	
	private ChoiceListOptionType createChoiceListOptionType(QuestionAnswerOptionType qaot){
		
		//getting display from MadocDocument
		String display = "";
		for (ChoiceListOptionType clotOriginal : wizardElement.getOptions().getOption()){
			if (clotOriginal.getId().equals(qaot.getId())){
				display = clotOriginal.getDisplay();
			}
		}

		//creating object
		ChoiceListOptionType clot = new ObjectFactory().createChoiceListOptionType();
		clot.setId(qaot.getId());
		clot.setEnabled(qaot.isEnabled());
		clot.setVisible(qaot.isVisible());
		clot.setValue(qaot.getValue());
		clot.setSelected(qaot.isSelected());
		clot.setDisplay(display);
		
		return clot;
	}
	
	@Override
	protected QuestionAnswerType createAnswer(QuestionAnswerType qat) {
		
		MultiLineValueBuilderHelper valueBuilder = new MultiLineValueBuilderHelper();

		QuestionAnswerOptionsType qaoty = new ObjectFactory().createQuestionAnswerOptionsType();

		//selected
		for (OptionChoiceListItemComponent clot : choice.getDestModel().elements()){

			QuestionAnswerOptionType qaot = createQuestionAnswerOptionType(clot.getWizardElement(), true);
			
			qaoty.getOption().add(qaot);
			
			if (qaot.isSelected()){
				valueBuilder.add(qaot.getValue());
			}
		}
		
		//unselected
		for (OptionChoiceListItemComponent clot : choice.getSourceModel().elements()){

			QuestionAnswerOptionType qaot = createQuestionAnswerOptionType(clot.getWizardElement(), false);
			
			qaoty.getOption().add(qaot);
			
			if (qaot.isSelected()){
				valueBuilder.add(qaot.getValue());
			}
		}

		qat.setOptions(qaoty);
		qat.setValue(valueBuilder.getResult());
		qat.setHasOptions(true);
		
		return qat;
	}

	@Override
	protected void answerUpdated(QuestionAnswerType qat) {

		//recreate values
		createOptionsComponents();
		populateChoiceModels();
		
		for (QuestionAnswerOptionType qaot : qat.getOptions().getOption()){
			if (qaot.isSelected()){
				choice.getDestModel().add(new OptionChoiceListItemComponent(this, createChoiceListOptionType(qaot)));
			}
		}
	}
	
	@Override
	public void setValue(String value) {
		
		//recreate values 
//		createOptionsComponents();
//		populateChoiceModels();
		
		choice.getDestModel().clear();
		
		//we can only set an existing value  
		for (ChoiceListOptionType clot : wizardElement.getOptions().getOption()){
			if (clot.getValue().equals(value)){
				choice.getDestModel().add(new OptionChoiceListItemComponent(this, clot));
				break;
			}
		}
		
	}
	
	@Override
	public void selectOption(String optionId) {
		choice.getDestModel().clear();
		
		//we can only set an existing value  
		for (ChoiceListOptionType clot : wizardElement.getOptions().getOption()){
			if (clot.getId().equals(optionId)){
				choice.getDestModel().add(new OptionChoiceListItemComponent(this, clot));
				break;
			}
		}
		
	}
	
	@Override
	public final boolean isRequiredValueReached() {
		if (!super.isRequiredValueReached()){
			return false;
		}
		if (wizardElement.isSetMinSize()){
			return choice.getDestModel().elements().size() >= wizardElement.getMinSize().intValue();
		}
		return true;
	}
	
	@Override
	public void addValue(String value) {
		
		//we can only set an existing value  
		for (ChoiceListOptionType clot : wizardElement.getOptions().getOption()){
			
			if (clot.getValue().equals(value)){
			
				boolean exists = false;
				for (int i = 0; i< choice.getDestModel().size(); i++){
					if (choice.getDestModel().get(i).getWizardElement().getId().equals(clot.getId())){
						exists = true;
						break;
					}
				}

				if (!exists){
					OptionChoiceListItemComponent oclic = new OptionChoiceListItemComponent(this, clot);
					if (oclic.getWizardElement().isEnabled()){
						movable.add(oclic);
					}
					choice.getDestModel().add(oclic);
					break;
				}
			}
		}
	}


}
