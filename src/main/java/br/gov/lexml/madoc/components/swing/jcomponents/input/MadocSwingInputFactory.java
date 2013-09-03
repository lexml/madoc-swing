package br.gov.lexml.madoc.components.swing.jcomponents.input;

import br.gov.lexml.madoc.schema.entity.InputInformation;
import br.gov.lexml.madoc.schema.entity.InputType;

public class MadocSwingInputFactory {

	public static MadocSwingInput<?> createComponentByInputInformation(InputInformation ii){
		
		if (!ii.isInput()){
			return null;
		}
		
		MadocSwingInput<?> input = MadocSwingInputFactory.createComponentByType(ii.getInputType());
		
		if (ii.isSetInputDefaultValue()){
			input.setStringValue(ii.getInputDefaultValue());
		}
		
		return input;
	}
	
	private static MadocSwingInput<?> createComponentByType(InputType inputType){
		
		switch (inputType){
			case DATE:
				return new DateInput();
				
			case DECIMAL:
				return new DecimalInput(); 
	
			case INTEGER:
				return new IntegerInput();
				
			case TEXT:
				return new TextInput();
		}
		
		return null;
	}
	
}
