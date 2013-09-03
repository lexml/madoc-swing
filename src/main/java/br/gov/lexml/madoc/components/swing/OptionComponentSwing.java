package br.gov.lexml.madoc.components.swing;

import javax.swing.JComponent;

import br.gov.lexml.madoc.components.OptionComponent;
import br.gov.lexml.madoc.components.QuestionComponent;
import br.gov.lexml.madoc.components.swing.jcomponents.QuestionPanel;
import br.gov.lexml.madoc.schema.entity.BaseOptionType;
import br.gov.lexml.madoc.schema.entity.QuestionType;


interface OptionComponentSwing
	<Q extends QuestionComponent<? extends QuestionType, ? extends QuestionPanel>, 
	 O extends BaseOptionType, 
	 C extends JComponent> 
	extends OptionComponent<Q, O, C> {

	public final static class ComponentOptionItem extends JComponent {
		
		private static final long serialVersionUID = 170236749188759476L;
		
		private String value;
		
		public JComponent setValue(String value){
			this.value = value;
			return this;
		}
		
		@Override
		public String toString() {
			return value;
		}
	}

}
