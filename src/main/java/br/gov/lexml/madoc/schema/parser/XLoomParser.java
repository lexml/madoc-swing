package br.gov.lexml.madoc.schema.parser;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import br.gov.lexml.madoc.schema.Constants;
import br.gov.lexml.xloom.processor.DefaultURIResolver;
import br.gov.lexml.xloom2.XLoomProcessor;

class XLoomParser {

	private static final Logger log = LoggerFactory.getLogger(XLoomParser.class);
	
	/**
	 * Process XLoom directives, if necessary
	 * @param source
	 * @return
	 */
	static Source processXLoom(Source source, URIResolver resolver) throws ParseException{			
		/*
		StringWriter w = new StringWriter();
		StreamResult sr = new StreamResult(w);
		*/
		if(resolver == null) {
			resolver = new DefaultURIResolver();
		}
		/*
		Transformer tf;
		try {			
			tf = TransformerFactory.newInstance().newTransformer();			
			tf.setURIResolver(resolver);
			tf.transform(source, sr);
		} catch (TransformerConfigurationException e1) {
			throw new ParseException("Error reading source: " + source.getSystemId(),e1);
		} catch (TransformerFactoryConfigurationError e1) {
			throw new ParseException("Error reading source: " + source.getSystemId(),e1);
		} catch (TransformerException e1) {
			throw new ParseException("Error reading source: " + source.getSystemId(),e1);
		}		
		
		IOUtils.closeQuietly(w);
		
		String doc = w.toString();
		
		if (log.isDebugEnabled()){
			log.debug("doc=" + doc);
		}
		
		Source sourceProcessed = new StreamSource(new ByteArrayInputStream(doc.getBytes()), source.getSystemId());
		*/
		Source sourceProcessed = XLoomParser.loadXLoom(source,resolver);
		
		/*
		 if(doc.indexOf(Constants.XLOOM_NAMESPACE) >= 0) {			
		 	sourceProcessed = XLoomParser.loadXLoom(sourceProcessed, resolver);
		}
		*/ 
		
		return sourceProcessed;
	}
	
	private static Source loadXLoom(Source source, URIResolver resolver){			
		
		return new XLoomProcessor(resolver).process(source).asSafeSource();
	} 

}
