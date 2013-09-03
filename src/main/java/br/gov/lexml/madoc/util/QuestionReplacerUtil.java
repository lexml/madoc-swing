package br.gov.lexml.madoc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.components.QuestionComponent;

public final class QuestionReplacerUtil {
	
	private static final Pattern regex = Pattern.compile("(\\{[\\w-.#$[0-9]]*\\})");

	private QuestionReplacerUtil(){};
	
	/**
	 * Replaces questions-id variables by questions values in an a String
	 * Example: http://legis.senado.gov.br/dadosabertos/materia/{q-proposicao-tipo}/{q-proposicao-numero}/{q-proposicao-ano}
	 * @param s
	 * @param controller
	 * @return
	 */
	public static final String replaceQuestionsIdByQuestionsValues(String s, ComponentController controller){
	
		// getting list of variables
		Matcher m = regex.matcher(s);
		List<String> variables = new ArrayList<String>();
	    while (m.find()){
	        variables.add(m.group(1));
	    }
	    
	    // replacement of variables by question values
	    String finalUri = s;
	    for (String v : variables){
	    	String questionId = v.replaceAll("[\\{\\}]", "");
	    	
	    	QuestionComponent<?,?> q = controller.getQuestionComponentById(questionId);
	    	if (q!= null){
	    		
	    		
	    		finalUri = finalUri.replaceAll(Pattern.quote(v), q.getAnswer().getValue());
	    	}
	    }
		
		return finalUri;
	}
}
