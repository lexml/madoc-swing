package br.gov.lexml.madoc.components.rules;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.schema.entity.ConsumeAddQuestionValueActionType;
import br.gov.lexml.madoc.schema.entity.ConsumeSetQuestionValueActionType;
import br.gov.lexml.madoc.schema.entity.ConsumeSetVariableValueActionType;
import br.gov.lexml.madoc.schema.entity.visitor.BaseVisitor;
import br.gov.lexml.madoc.schema.entity.visitor.VisitorAction;

class ConsumeActionVisitor extends BaseVisitor {

	private static final Logger log = LoggerFactory.getLogger(ConsumeActionVisitor.class);

	private final Document document;
	private final ComponentController componentController;
	
	private final XPathFactory xPathFactory = XPathFactory.newInstance();
	
	/**
	 * Visitor for ConsumeActionType objects
	 * @param xmlSource xml document from ConsumeRestServiceActionType with all transformations  
	 * @param componentController
	 */
	ConsumeActionVisitor(Document document, ComponentController componentController){
		this.document = document;
		this.componentController = componentController;
	}
	
	@Override
	public VisitorAction enter(ConsumeSetQuestionValueActionType aBean){
		log.debug("ConsumeSetQuestionValueActionType - questionId="+aBean.getQuestionId()+" xPath="+aBean.getXpath());
		
		try {
			componentController.setQuestionValue(aBean.getQuestionId(), processXPath(aBean.getXpath()));
		} catch (XPathExpressionException e) {
			log.error("Error on processing XPath \""+aBean.getXpath()+"\" on setting questionId="+aBean.getQuestionId());
			e.printStackTrace();
		}
		
		return VisitorAction.CONTINUE;
	}	
	
	@Override
	public VisitorAction enter(ConsumeAddQuestionValueActionType aBean) {
		log.debug("ConsumeAddQuestionValueActionType - questionId="+aBean.getQuestionId()+" xPath="+aBean.getXpath());

		try {
			componentController.addQuestionValue(aBean.getQuestionId(), processXPath(aBean.getXpath()));
		} catch (XPathExpressionException e) {
			log.error("Error on processing XPath \""+aBean.getXpath()+"\" on setting questionId="+aBean.getQuestionId());
			e.printStackTrace();
		}

		return VisitorAction.CONTINUE;
	}
	
	@Override
	public VisitorAction enter(ConsumeSetVariableValueActionType aBean) {
		log.debug("ConsumeSetVariableValueActionType - questionId="+aBean.getVariableName()+" xPath="+aBean.getXpath());
		
		try {
			componentController.setVariable(aBean.getVariableName(), processXPath(aBean.getXpath()));
		} catch (XPathExpressionException e) {
			log.error("Error on processing XPath \""+aBean.getXpath()+"\" on setting variableName="+aBean.getVariableName()); 
			e.printStackTrace();
		}
		
		return VisitorAction.CONTINUE;
	}
	
	/**
	 * Returns the result String when apllying the xPathExpression on xmlSource
	 * @param xPathExpression
	 * @return
	 * @throws XPathExpressionException
	 */
	private String processXPath(String xPathExpression) throws XPathExpressionException{
		
		if (xPathExpression== null || xPathExpression.equals("")){
			return "";
		}
		
		XPathExpression expr = xPathFactory.newXPath().compile(xPathExpression);
		return expr.evaluate(document);
	}
	
}
