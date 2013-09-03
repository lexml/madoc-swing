package br.gov.lexml.madoc.catalog.store;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.itextpdf.text.log.SysoLogger;

import br.gov.lexml.madoc.schema.Constants;

public class DirectoryCatalogStore implements DocumentStore {

	private static final Logger log = LoggerFactory
			.getLogger(DirectoryCatalogStore.class);
	
	private DirectoryCatalogStoreMetadataProcessor metadataProcessor = new DirectoryCatalogStoreMetadataProcessor();

	private final String catalogUri;
	private final File modelDirectory;

	public static void main(String args[]){
		Pattern fileNamePattern = Pattern
				.compile("^(.*?)(?:-(.+))?.(?:xml|XML)$");
		Matcher matcher = fileNamePattern.matcher("s001.xml");
		if (matcher.matches()) {
			String resourceName = matcher.group(1);
			String version = matcher.group(2);
			
			System.out.println("resourceName: "+resourceName);
			System.out.println("version: "+version);
		}
	}
	
	
	public DirectoryCatalogStore(String catalogUri, File modelDirectory) {
		super();
		this.catalogUri = catalogUri;
		this.modelDirectory = modelDirectory;
	}
	
	@Override
	public InputStream getDocument(String docUri) throws IOException {
		
		log.debug("DirectoryCatalogStore: "+docUri);
		
		if (!docUri.equals(catalogUri)) {
			return null;
		}
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();

			// creating Catalog
			Document resDoc = db.newDocument();
			Element catalogElement = resDoc.createElementNS(Constants.DEFAULT_URI, Constants.SHORT_NAMESPACE+"Catalog");
			resDoc.appendChild(catalogElement);
			
			// processing catalog content
			processCatalogElements(db, resDoc, catalogElement);
			
			// finalization
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			DOMSource ds = new DOMSource(resDoc);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			StreamResult res = new StreamResult(bos);
			t.transform(ds, res);
			IOUtils.closeQuietly(bos);
			return new ByteArrayInputStream(bos.toByteArray());
		} catch (XPathExpressionException e) {
			throw new RuntimeException("Error compiling xpath expression: " + e.getMessage(), e);
		} catch (SAXException e) {
			throw new RuntimeException("Error processing file. " + e.getMessage(), e);
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException("Error configuring rendering: "
					+ e.getMessage(), e);
		} catch (TransformerException e) {
			throw new RuntimeException("Error rendering document: "
					+ e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Error configuring parser: "
					+ e.getMessage(), e);
		}
	}
	
	private void processCatalogElements(DocumentBuilder db, Document resDoc, Element catalogElement) throws SAXException, IOException, XPathExpressionException{

		//parsing files
		Items itens = processItems(db); 
				
		//creating MadocDocuments tag
		Element madocDocuments = getMadocDocumentBaseCatalogItemType(Constants.MADOC_DOCUMENTS_CATALOG_ELEMENT, Constants.MADOC_DOCUMENT_ROOT_ELEMENT, resDoc, itens);
		if (madocDocuments!= null){
			catalogElement.appendChild(madocDocuments);
		}
		
		//creating MadocSkeleton tag
		Element skeletonDocuments = getMadocDocumentBaseCatalogItemType(Constants.MADOC_SKELETONS_CATALOG_ELEMENT, Constants.MADOC_SKELETON_ROOT_ELEMENT, resDoc, itens);
		if (skeletonDocuments!= null){
			catalogElement.appendChild(skeletonDocuments);
		}
		
		//creating MadocLibrary tag
		Element libraryDocuments = getMadocDocumentBaseCatalogItemType(Constants.MADOC_LIBRARIES_CATALOG_ELEMENT, Constants.MADOC_LIBRARY_ROOT_ELEMENT, resDoc, itens);
		if (skeletonDocuments!= null){
			catalogElement.appendChild(libraryDocuments);
		}
		
		//creating Resources tag
		Element resources = getMadocDocumentBaseCatalogItemType(Constants.RESOURCES_CATALOG_ELEMENT, Constants.RESOURCE_ELEMENT, resDoc, itens);
		if (resources!= null){
			catalogElement.appendChild(resources);
		}
		
	}
	
	/**
	 * Process modelDirectory and build an Items object with lists of MadocDocument, SkeletonDocument, LibraryDocument and Resources. 
	 * @param db
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 */
	private Items processItems(DocumentBuilder db) throws SAXException, IOException{
		
		Items itens = new Items();
		
		//processing XML items
		
		for (File f : FileUtils.listFiles(modelDirectory, new String[] { "xml" }, true)) {
			Document doc = db.parse(f);
			
			String rootElementName = doc.getDocumentElement().getNodeName();
			if (rootElementName!= null){
				rootElementName = rootElementName.replaceFirst("^.*:", ""); 
			}
			if (rootElementName.equals(Constants.MADOC_DOCUMENT_ROOT_ELEMENT) || 
					rootElementName.equals(Constants.MADOC_SKELETON_ROOT_ELEMENT) ||
					rootElementName.equals(Constants.MADOC_LIBRARY_ROOT_ELEMENT)){
				itens.add(rootElementName, f, doc);
			}
		}
		
		// processing resources documents
		
		for (File f : FileUtils.listFiles(modelDirectory, FileFilterUtils.notFileFilter(FileFilterUtils.suffixFileFilter("xml")), null)) {
			itens.add(f);
		}
		
		return itens;
	}
	
	private Element getMadocDocumentBaseCatalogItemType(String tagName, String type, Document resDoc, Items itens) throws XPathExpressionException{
		Element skeletonDocuments = resDoc.createElementNS(Constants.DEFAULT_URI, Constants.SHORT_NAMESPACE+tagName);
		//catalogElement.appendChild(skeletonDocuments);
		
		for (Item item : itens.getItens(type)){
			Element e = metadataProcessor.getMetadataElement(item.doc);
			if (e != null) {
				Element itemElement = resDoc.createElementNS(Constants.DEFAULT_URI, Constants.SHORT_NAMESPACE+type);

				if (!StringUtils.isEmpty(item.version)){
					itemElement.setAttribute("version", item.version);
				}
				itemElement.setAttribute("resourceName", item.resourceName);
				itemElement.setAttribute("fileSuffix", item.fileSuffix);
				itemElement.setAttribute("mimetype", item.mimetype);
				itemElement.setAttribute("size", Long.toString(item.size));
				itemElement.setAttribute("obsolete", item.obsolete ? "true" : "false");
				itemElement.appendChild(resDoc.importNode(e, true));
					
				skeletonDocuments.appendChild(itemElement);
			}
		}
		
		return skeletonDocuments;
	}
}


class Items{

	private static final Pattern fileNamePattern = Pattern
			.compile("^(.*?)(?:-(.+))?.(?:xml|XML)$");

	Map<String, List<Item>> docsList = new HashMap<String, List<Item>>();
	
	/**
	 * Add a XML document 
	 * @param type
	 * @param file
	 * @param doc
	 */
	public void add(String type, File file, Document doc){
		
		if (!docsList.containsKey(type)){
			docsList.put(type, new ArrayList<Item>());
		}
		
		Item item = createItem(file, doc);
		if (item != null){
			docsList.get(type).add(item);
		}
	}
	
	/**
	 * Add a resource
	 * @param type
	 * @return
	 */
	public void add(File file){
		add(Constants.RESOURCES_CATALOG_ELEMENT, file, null);
	}
	
	public List<Item> getItens(String type){
		List<Item> itens = docsList.get(type);
		
		if (itens== null){
			return new ArrayList<Item>();
		}
		
		return itens;
	}
	
	private Item createItem(File file, Document doc){
		
		Matcher matcher = fileNamePattern.matcher(file.getName());
		if (matcher.matches()) {
			String resourceName = matcher.group(1);
			String version = matcher.group(2);
			
			String suffix = FilenameUtils.getExtension(file.getName());
		
			Item item = new Item();
		
			item.file = file;
			item.doc = doc;
			item.resourceName = resourceName;
			item.version = version;
			item.fileSuffix = suffix;
			item.size = FileUtils.sizeOf(file);
			item.obsolete = false;
			item.mimetype = new MimetypesFileTypeMap().getContentType(file);
			
			return item;
		} else {
			return null;
		}
	}

}

class Item{
	String resourceName;
	String version;
	String fileSuffix;
	File file;
	Document doc;
	String mimetype;
	long size;
	boolean obsolete;
}
