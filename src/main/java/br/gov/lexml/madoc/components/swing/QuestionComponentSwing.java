package br.gov.lexml.madoc.components.swing;

import br.gov.lexml.madoc.components.QuestionComponent;
import br.gov.lexml.madoc.components.swing.jcomponents.QuestionPanel;
import br.gov.lexml.madoc.schema.entity.QuestionType;

interface QuestionComponentSwing
	<Q extends QuestionType, 
	 C extends QuestionPanel> 
	extends QuestionComponent<Q, C> {

}
