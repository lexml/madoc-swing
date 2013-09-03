package br.gov.lexml.madoc.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import br.gov.lexml.madoc.schema.Constants;
import br.gov.lexml.madoc.schema.parser.ParseException;

public class XMLUtil {

	private static final Logger log = LoggerFactory.getLogger(XMLUtil.class);
	
	public static String removeEntitiesAndNormalize(String content){
		return StringEscapeUtils.unescapeHtml(XMLUtil.removeEntities(content)).replaceAll("\\n", "").replaceAll(" +", "").trim();
	}
	
	public static String removeEntities(String content){
		if (StringUtils.isEmpty(content)){
			return "";
		}
		return content.replaceAll("<.+?>", "");
	}
	
	/**
	 * Convert and Node XML to String
	 * @param node
	 * @return
	 */
	public static String xmlToString(Node node) {
		/*
		DOMImplementationLS domLS = ((DOMImplementationLS) node.getOwnerDocument().getImplementation().getFeature("LS", "3.0"));  
		
		return domLS.createLSSerializer().writeToString(node);
		*/
          
        try {
            Source source = new DOMSource(node);
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            
            transformer.transform(source, result);
            
            return stringWriter.getBuffer().toString();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
     
    }

	public static Document convertXMLStringToDocument(String xmlString){
        
		if (log.isDebugEnabled()){
        	log.debug("convertXMLStringToDocument xmlString="+xmlString);
        }
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		factory.setNamespaceAware(true);
		
        DocumentBuilder builder;  
        try  
        {  
            builder = factory.newDocumentBuilder();  
  
            // Use String reader  
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));
            
            return document;
        } catch (ParserConfigurationException e){  
            log.error(e.getMessage(), e);  
        } catch (SAXException e) {
        	log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}  
        return null;
	}
	
	
	/**
	 * Convert an object to a XML string
	 * 
	 * @param classToMarshall
	 * @param objectToMarshall
	 * @param rootElementName
	 * @return
	 * @throws ParseException
	 */
	public static <T> String convertObjectToXMLString(Class<T> classToMarshall,
			T objectToMarshall, String rootElementName) throws ParseException {

		DOMResult domResult = new DOMResult();

		try {
			final JAXBContext jaxbContext = JAXBContext
					.newInstance(classToMarshall);

			Marshaller marsh = jaxbContext.createMarshaller();
			marsh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// marsh.marshal(objectToMarshall, new FileOutputStream(file));
			marsh.marshal(new JAXBElement<T>(new QName(Constants.DEFAULT_URI,
					rootElementName), classToMarshall, objectToMarshall),
					domResult);

			// convert do XML string
			DOMSource domMSource = new DOMSource(domResult.getNode(),
					domResult.getSystemId());
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);

			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.transform(domMSource, result);

			return writer.toString();

		} catch (PropertyException e) {
			throw new ParseException("convertObjectToXMLString", e);
		} catch (JAXBException e) {
			throw new ParseException("convertObjectToXMLString", e);
		} catch (TransformerException e) {
			throw new ParseException("convertObjectToXMLString", e);
		}
	}
	
}
