package br.gov.lexml.madoc.schema.parser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.gov.lexml.madoc.schema.Constants;
import br.gov.lexml.madoc.schema.entity.CatalogType;
import br.gov.lexml.madoc.schema.entity.MadocAnswerType;
import br.gov.lexml.madoc.schema.entity.MadocDocumentType;
import br.gov.lexml.madoc.schema.entity.MadocLibraryType;
import br.gov.lexml.madoc.schema.entity.MadocSkeletonType;
import br.gov.lexml.madoc.util.XMLUtil;
import br.gov.lexml.xloom.processor.DefaultURIResolver;

/**
 * Schema's parser: wizards or answers
 * 
 * @author lauro
 * 
 */
public final class SchemaParser {

	
	private static final Logger log = LoggerFactory.getLogger(SchemaParser.class);
	
	/**
	 * No instances
	 */
	private SchemaParser(){}
	
	/**
	 * Load a xml via jaxb (unmarshall)
	 * 
	 * @param file
	 * @param classToUnmarshall
	 * @return
	 */
	private static <T> T loadJaxb(Source source, Class<T> classToUnmarshall, URIResolver resolver)
			throws ParseException {
		
		log.debug("loadJaxb - starting XLoomParser");
		Source sourceProcessed = XLoomParser.processXLoom(source, resolver);
//		if(log.isDebugEnabled()) {
//			sourceProcessed = debugXMLSource("loadJaxb - XLoomParser Processado", sourceProcessed);
//		}
		log.debug("loadJaxb - ending XLoomParser");
		
		try {
			
			final JAXBContext unmarshallingClassJAXB = JAXBContext
					.newInstance("br.gov.lexml.madoc.schema.entity",
							CatalogType.class.getClassLoader());

			log.debug("unmarshallingClassJAXB classname: "+unmarshallingClassJAXB.getClass().getName());
			
			return unmarshallingClassJAXB.createUnmarshaller()
					.unmarshal(sourceProcessed, classToUnmarshall).getValue();
		} catch (JAXBException e) {
			throw new ParseException("Error in loadJaxb", e);
		}
	}
	
//	private static Source debugXMLSource(String label, Source source) {
//		
//		log.debug("--------------------------------------------------------");
//		log.debug(label);
//		
//		try {
//			Transformer transformer = TransformerFactory.newInstance().newTransformer();
//			
//			StringWriter sw = new StringWriter();
//			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//			transformer.transform(source, new StreamResult(sw));
//			
//			String xml = sw.toString();
//			
//			log.debug(xml);
//			
//			return new StreamSource(new StringReader(xml)); 
//		}
//		catch(Exception e) {
//			log.error("Unable to debug xml result after XLoomParser.", e);
//		}
//		
//		return null;
//	}

	private static <T> T loadJaxb(Source source, Class<T> classToUnmarshall) throws ParseException {
		return loadJaxb(source, classToUnmarshall, new DefaultURIResolver());
	}
	

	/**
	 * Save a Java structure to a xml file (marshall)
	 * 
	 * @param file
	 * @param classToMarshall
	 * @param objectToMarshall
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	private static <T> void saveToFile(File file, Class<T> classToMarshall,
			T objectToMarshall, String rootElementName) throws JAXBException,
			FileNotFoundException {

		final JAXBContext jaxbContext = JAXBContext
				.newInstance(classToMarshall);

		Marshaller marsh = jaxbContext.createMarshaller();
		marsh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		// marsh.marshal(objectToMarshall, new FileOutputStream(file));
		marsh.marshal(new JAXBElement<T>(
				new QName(Constants.DEFAULT_URI, rootElementName), classToMarshall,
				objectToMarshall), new FileOutputStream(file));

	}
	
	/*
	 * MadocDocumentType
	 */
	
	public static MadocDocumentType loadMadocDocument(File file, URIResolver resolver) throws ParseException {
		try {
			Source source = new StreamSource(new BufferedInputStream(
					new FileInputStream(file)));
			return loadMadocDocument(source, resolver);
		} catch (FileNotFoundException e) {
			throw new ParseException("error in loadMadocDocument: file = "
					+ file, e);
		}
	}

	public static MadocDocumentType loadMadocDocument(InputStream is, URIResolver resolver)
			throws ParseException {
		return loadMadocDocument(new StreamSource(is), resolver);
	}

	public static MadocDocumentType loadMadocDocument(Source source, URIResolver resolver)
			throws ParseException {
		
		MadocDocumentType mdt = (MadocDocumentType) loadJaxb(source, MadocDocumentType.class, resolver);
		
		if (log.isDebugEnabled()){
			log.debug(XMLUtil.convertObjectToXMLString(MadocDocumentType.class, mdt, Constants.MADOC_DOCUMENT_ROOT_ELEMENT));			
		}

		return mdt;
	}

	/*
	 * MadocSkeletonType
	 */
	
	public static MadocSkeletonType loadMadocSkeleton(InputStream is, URIResolver resolver)
			throws ParseException {
		return loadMadocSkeleton(new StreamSource(is), resolver);
	}

	public static MadocSkeletonType loadMadocSkeleton(Source source, URIResolver resolver)
			throws ParseException {
		return (MadocSkeletonType) loadJaxb(source, MadocSkeletonType.class, resolver);
	}
	
	/*
	 * MadocLibraryType
	 */
	
	public static MadocLibraryType loadMadocLibrary(InputStream is, URIResolver resolver)
			throws ParseException {
		return loadMadocLibrary(new StreamSource(is), resolver);
	}

	public static MadocLibraryType loadMadocLibrary(Source source, URIResolver resolver)
			throws ParseException {
		return (MadocLibraryType) loadJaxb(source, MadocLibraryType.class, resolver);
	}
	
	/*
	 * CatalogType
	 */
	
	public static CatalogType loadCatalog(InputStream is)
			throws ParseException{
		return (CatalogType) loadJaxb(new StreamSource(is), CatalogType.class);
	}


	
	/**
	 * Load an answer xml file
	 * 
	 * @param file
	 * @return
	 */
	public static MadocAnswerType loadAnswer(File file) throws ParseException {
		try {
			return loadAnswer(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new ParseException("error in loadAnswer: file = "
					+ file, e);
		}
	}

	/**
	 * Load an answer xml from InputStream
	 * 
	 * @param is
	 * @return
	 */
	public static MadocAnswerType loadAnswer(InputStream is) throws ParseException {
		Source source = new StreamSource(new BufferedInputStream(is));
		return (MadocAnswerType) loadJaxb(source, MadocAnswerType.class);
	}
	
	/**
	 * Save a wizard xml file
	 * 
	 * @param file
	 */
	public static void saveAnswer(File file, MadocAnswerType dwt) throws SaveException {
		try {
			saveToFile(file, MadocAnswerType.class, dwt,
					Constants.MADOC_ANSWER_ROOT_ELEMENT);
		} catch (FileNotFoundException e) {
			throw new SaveException(
					"saveAnswer: Exception while saving to file " + file, e);
		} catch (JAXBException e) {
			throw new SaveException("saveAnswer: Exception while marshalling",
					e);
		}
	}
	
}
