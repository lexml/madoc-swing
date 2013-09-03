package br.gov.lexml.madoc.components.rules;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.schema.entity.BaseEntity;
import br.gov.lexml.madoc.schema.entity.ConsumeRestServiceActionType;
import br.gov.lexml.madoc.schema.entity.ConsumeRestServiceActionType.Transformations.ExternalXSL;
import br.gov.lexml.madoc.schema.entity.ConsumeRestServiceActionType.Transformations.InlineXSL;
import br.gov.lexml.madoc.util.QuestionReplacerUtil;
import br.gov.lexml.madoc.util.XMLUtil;

class ConsumeRestServiceAction {

	private static final Logger log = LoggerFactory.getLogger(ConsumeRestServiceAction.class);
	
	private final ConsumeRestServiceActionType aBean;
	private final ComponentController componentController;
	
	ConsumeRestServiceAction(ConsumeRestServiceActionType aBean, ComponentController componentController) {
		this.aBean = aBean;
		this.componentController = componentController;
	}
	
	void process(){
		
		//get resolver
		URIResolver uriResolver = componentController.getCatalogService().getURIResolver();
		
		if (uriResolver== null){
			return;
		}
		
		String uriSource = ""; 
		Source xmlSource = null; 
		
		try {
			uriSource= QuestionReplacerUtil.replaceQuestionsIdByQuestionsValues(aBean.getUri(), componentController);
			
			log.debug("uriSource="+uriSource);
			
			// getting XML base document
			xmlSource = uriResolver.resolve(uriSource, null);

		} catch (TransformerException e) {
			
			RuntimeException ef = new RuntimeException("Error while resolving "+aBean.getUri()+" as "+uriSource, e);
			log.error(ef.getMessage(), ef);
			
			throw ef;
		}
		
		try {
			// getting transformer pipeline and transforming
			Document finalDocument = transformSource(xmlSource, uriResolver);
			
			// process actions
			ConsumeActionVisitor cav = new ConsumeActionVisitor(finalDocument, componentController);
			aBean.getActions().accept(cav);
			
		} catch (TransformerException e) {
			
			RuntimeException ef = new RuntimeException("Error while transforming "+aBean.getUri()+" as "+uriSource, e);
			log.error(ef.getMessage(), ef);
			
			throw ef;
		}
	}
	
	/**
	 * Transform source document based transformation pipeline from aBean
	 * @param xmlSource
	 * @param uriResolver
	 * @return
	 * @throws TransformerException 
	 */
	private Document transformSource(final Source xmlSource, URIResolver uriResolver) throws TransformerException{
		
		// getting transformer pipeline
		List<Transformer> transforms = createTransformPipeline(uriResolver);
					
		// transforming
		Document documentResult = null;
		Source source = xmlSource;
		for (Transformer transformer : transforms){
			DOMResult domResult = new DOMResult();
			transformer.transform(source, domResult);
			documentResult = (Document) domResult.getNode();
			
			if (log.isDebugEnabled()){
				log.debug("Document after transformation: "+XMLUtil.xmlToString(documentResult));
			}
			
			source = new DOMSource(documentResult);
		}
		
		return documentResult;
	}
	
	
	/**
	 * Create a transformer pipeline based on aBean
	 * @param uriResolver
	 * @return
	 * @throws TransformerException 
	 */
	private List<Transformer> createTransformPipeline(URIResolver uriResolver) throws TransformerException{
		
		//TransformerFactory tFactory = TransformerFactory.newInstance();
		TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();
		
		if (aBean.getTransformations()== null || aBean.getTransformations().getExternalXSLAndInlineXSL() == null){
			//adding an identity transformer
			List<Transformer> transforms = new ArrayList<Transformer>(1);
			transforms.add(tFactory.newTransformer());
			
			return transforms;
		}
		
		List<BaseEntity> transfListBean = aBean.getTransformations().getExternalXSLAndInlineXSL();
		List<Transformer> transforms = new ArrayList<Transformer>(transfListBean.size());
		for (BaseEntity xslBase : aBean.getTransformations().getExternalXSLAndInlineXSL()){
						
			Source sourceXSL = null;
					
			if (xslBase instanceof ExternalXSL){
							
				ExternalXSL xslExternal = (ExternalXSL) xslBase;
				sourceXSL = uriResolver.resolve(QuestionReplacerUtil.replaceQuestionsIdByQuestionsValues(xslExternal.getHref(), componentController), null);
					
			} else if (xslBase instanceof InlineXSL){
					
				InlineXSL xslInternal = (InlineXSL) xslBase;
				//sourceXSL = new DOMSource(xslInternal.getAny());
				
				sourceXSL = new StreamSource(new StringReader(XMLUtil.xmlToString(xslInternal.getAny())));
			} 
			
			if (sourceXSL!= null){
				transforms.add(tFactory.newTransformer(sourceXSL));
			}
		}

		//adding an identity transformer
		if (transforms.isEmpty()){
			transforms.add(tFactory.newTransformer());
		}
		
		return transforms;
	}
	
}
