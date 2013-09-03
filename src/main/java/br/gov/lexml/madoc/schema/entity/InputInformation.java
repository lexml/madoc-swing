package br.gov.lexml.madoc.schema.entity;

/**
 * Entity class used by JToggleInput while creating accessory input objects 
 * @author lauro
 *
 */
public class InputInformation {

	private boolean isInput = false;
	private InputType inputType;
	private String inputDefaultValue;
	
	public boolean isInput() {
		return isInput;
	}
	public InputInformation setInput(boolean isInput) {
		this.isInput = isInput;
		return this;
	}
	public InputType getInputType() {
		return inputType;
	}
	public InputInformation setInputType(InputType inputType) {
		this.inputType = inputType;
		return this;
	}
	public String getInputDefaultValue() {
		return inputDefaultValue;
	}
	public InputInformation setInputDefaultValue(String inputDefaultValue) {
		this.inputDefaultValue = inputDefaultValue;
		return this;
	} 
	
	public boolean isSetInputDefaultValue(){
		return this.inputDefaultValue!= null && !this.inputDefaultValue.equals("");
	}
	
}
