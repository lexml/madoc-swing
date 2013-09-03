package br.gov.lexml.madoc.rendition;

import java.util.HashMap;
import java.util.Map;

import br.gov.lexml.madoc.data.CollectionValue;
import br.gov.lexml.madoc.data.DataSetUtil;
import br.gov.lexml.madoc.data.DataSets;
import br.gov.lexml.madoc.schema.entity.BaseOptionType;
import br.gov.lexml.madoc.schema.entity.BaseWizardRestrictType;
import br.gov.lexml.madoc.schema.entity.ChoiceListOptionType;
import br.gov.lexml.madoc.schema.entity.MadocAnswerType;
import br.gov.lexml.madoc.schema.entity.MadocDocumentType;
import br.gov.lexml.madoc.schema.entity.PageType;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerOptionType;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerType;
import br.gov.lexml.madoc.schema.entity.QuestionType;
import br.gov.lexml.madoc.schema.entity.SectionType;
import br.gov.lexml.madoc.schema.entity.SelectOptionType;
import br.gov.lexml.madoc.schema.entity.VariableAnswerType;
import br.gov.lexml.madoc.schema.entity.visitor.BaseVisitor;
import br.gov.lexml.madoc.schema.entity.visitor.VisitorAction;

/**
 * A collection objects used on Velocity Context  
 * @author lauro
 *
 */
class ContextCollection {

	private final MadocAnswerType madocAnswer;
	private final MadocDocumentType madocDocument;
	
	private Map<String, QuestionAnswerType> answers;
	private Map<String, QuestionAnswerOptionType> answersOptions;
	private Map<String, QuestionType> questions;
	private Map<String, BaseOptionType> questionsOptions;
	private Map<String, String> variables;
	private DataSets dataSets;
	private CollectionValue collectionValue;
	
	ContextCollection(MadocAnswerType madocAnswer, MadocDocumentType madocDocument){
		this.madocAnswer = madocAnswer;
		this.madocDocument = madocDocument;
	}
	
	/**
	 * Returns a structure of DataSets
	 * @return
	 */
	DataSets getDataSets(){
		if (this.dataSets== null){
			this.dataSets = madocDocument.getDataSets() == null ? DataSets.EMPTY : DataSets.fromDataSets(madocDocument.getDataSets());
		}
		return this.dataSets; 
	}
	
	/**
	 * Returns a CollectionValue structure queryable as a DataSet
	 * @return
	 */
	CollectionValue getMetadataCollectionVale(){
		
		if (this.collectionValue == null){
			this.collectionValue = DataSetUtil.valueFromDataSetValue(madocDocument.getMetadata());
		}
		return this.collectionValue;
	}
	
	/**
	 * Returns a map of id and QuestionAnswerType 
	 * @return
	 */
	Map<String, QuestionAnswerType> getAnswersMap(){
		if (answers== null){
			Map<String, QuestionAnswerType> answers = new HashMap<String, QuestionAnswerType>();
			for (QuestionAnswerType question : madocAnswer.getQuestionsAnswers().getQuestionAnswer()) {
				answers.put(question.getId(), question);
			}
			this.answers = answers;
		}
		return this.answers;
	}
	
	/**
	 * Returns a map of id and QuestionAnswerOptionType
	 * @return
	 */
	Map<String, QuestionAnswerOptionType> getAnswersOptionsMap(){
		if (answersOptions== null){
			
			final Map<String, QuestionAnswerOptionType> options = new HashMap<String, QuestionAnswerOptionType>();
			
			madocAnswer.getQuestionsAnswers().accept(new BaseVisitor(){
				@Override
				public VisitorAction enter(QuestionAnswerOptionType aBean) {
					options.put(aBean.getId(), aBean);
					
					return VisitorAction.CONTINUE;
				}
				
			});
			
			this.answersOptions = options;
		}
		return this.answersOptions;
	}
	
	/**
	 * Returns a map of id and QuestionType
	 * @return
	 */
	Map<String, QuestionType> getQuestionsMap(){
		if (this.questions== null){
			
			final Map<String, QuestionType> q = new HashMap<String, QuestionType>();
			
			for (PageType p : madocDocument.getWizard().getPages().getPage()){
				for (BaseWizardRestrictType qsc : p.getQuestionOrSectionOrCommand() ){
					if (qsc instanceof QuestionType){
						q.put(qsc.getId(), (QuestionType) qsc);
					} else if (qsc instanceof SectionType){
						for (BaseWizardRestrictType qc : ((SectionType) qsc).getQuestionOrCommandOrHtmlContent() ){
							if (qc instanceof QuestionType){
								q.put(qc.getId(), (QuestionType)qc);
							}
						}
					}
				}
			}
			
			madocDocument.getWizard().getPages().accept(new BaseVisitor(){
				@Override
				public VisitorAction enter(QuestionType aBean) {
					q.put(aBean.getId(), aBean);
					return VisitorAction.CONTINUE;
				}
			});
			
			this.questions = q;
		} 
		return this.questions;
	}
	
	/**
	 * Returns a map of id and BaseOptionType
	 * @return
	 */
	Map<String, BaseOptionType> getQuestionsOptionsMap(){
		if (questionsOptions== null){
			
			final Map<String, BaseOptionType> qOptions = new HashMap<String, BaseOptionType>();
			
			madocDocument.getWizard().getPages().accept(new BaseVisitor(){
				
				void add(BaseOptionType aBean){
					qOptions.put(aBean.getId(), aBean);
				}
				
				@Override
				public VisitorAction enter(SelectOptionType aBean) {
					add(aBean);
					return VisitorAction.CONTINUE;
				}
				
				@Override
				public VisitorAction enter(ChoiceListOptionType aBean) {
					add(aBean);
					return VisitorAction.CONTINUE;
				}
				
			});
			
			questionsOptions = qOptions;
			
		}
		return this.questionsOptions;
	}
	
	/**
	 * Returns a map of variable name and variable value
	 * @return
	 */
	Map<String, String> getVariablesMap(){
		if (variables== null){
			Map<String, String> vars = new HashMap<String, String>();
			for (VariableAnswerType va : madocAnswer.getVariables().getVariableAnswer()){
				vars.put(va.getVariableName(), va.getValue());
			}
			this.variables = vars;
		}
		return this.variables;
	}
}


