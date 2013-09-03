package br.gov.lexml.madoc.util;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fop.pdf.PDFAMode;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

public class FOHelper {

	private final String xml;
	
	private Document xmlDoc;
	private Node xmpmetaNode;
	private String xmpmetaString;
	
	private static final Log log = LogFactory.getLog(FOHelper.class);
	
	private String pdfaidPart;
	private String pdfaidConformance;
	private String dcCreator;
	private String xmpCreateDate;
	
    private static final Map<String,String> DEFAULT_URI = new HashMap<String,String>();
    {
	    DEFAULT_URI.put("x", "adobe:ns:meta/");
	    DEFAULT_URI.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	    DEFAULT_URI.put("xmp", "http://ns.adobe.com/xap/1.0/");
	    DEFAULT_URI.put("pdf", "http://ns.adobe.com/pdf/1.3/");
	    DEFAULT_URI.put("dc", "http://purl.org/dc/elements/1.1/");
	    DEFAULT_URI.put("pdfaid", "http://www.aiim.org/pdfa/ns/id/");
	    DEFAULT_URI.put("pdfaExtension", "http://www.aiim.org/pdfa/ns/extension/");
	    DEFAULT_URI.put("pdfaSchema", "http://www.aiim.org/pdfa/ns/schema#");
	    DEFAULT_URI.put("pdfaProperty", "http://www.aiim.org/pdfa/ns/property#");
    }

    /*
     * Constructor
     */
	public FOHelper(String xml){
		this.xml = xml;
		processXML();
	}
	
	/**
	 * Extract xmpmeta (if it exists) from the original document
	 */
	private void processXML(){
	    SAXReader xmlReader = new SAXReader();
	    try {
			xmlDoc = xmlReader.read(new StringReader(xml));
			
			// extracting information
	    	pdfaidPart = getStringValueByXPath("//pdfaid:part");
			pdfaidConformance = getStringValueByXPath("//pdfaid:conformance");
			dcCreator = getStringValueByXPath("//dc:creator");
			xmpCreateDate = getStringValueByXPath("//xmp:CreateDate");
			
		    // extracting and detaching xmpmetaElement from its parent
		    xmpmetaNode = getNodeByXPath("//x:xmpmeta");
		    if (xmpmetaNode!= null){
		    	xmpmetaNode.detach();
		    }
			
		} catch (DocumentException e) {
			log.error("Exception loading FOP XML: "+e.getMessage(), e);
		}
	}
	
	public String getXmpmeta(){
		if (xmpmetaString == null && xmpmetaNode != null){
			xmpmetaString = xmpmetaNode.asXML();
		}
		
		return xmpmetaString;
	}
	
	/**
	 * Convert the current xmlDoc to a org.w3c.dom.Document  
	 * @return
	 */
	public Document getFOPDocumentWithoutXmpmeta(){
		return xmlDoc;
	}
	
	public String getDCCreator(){
		return dcCreator;
	}
	
	public boolean isPDFAMode(){
	    return (pdfaidPart!= null && pdfaidConformance!= null && (pdfaidPart.equals("1") || pdfaidPart.equals("2") || pdfaidPart.equals("3")) );
	}
	
	public String getCmpCreateDate(){
		return xmpCreateDate;
	}
	
	/**
	 * Return a PDF/A mode name for FOP, limited to PFA/A-1A or PFA/A-1B  
	 * @return
	 */
	public String getPDFAModeNameFOP(){
	    if (pdfaidPart!= null && pdfaidConformance!= null && (pdfaidPart.equals("1") || pdfaidPart.equals("2") || pdfaidPart.equals("3"))){
	    	if (pdfaidConformance.equals("A")){
	    		return PDFAMode.PDFA_1A.getName();
	    	}
	    	if (pdfaidConformance.equals("B")){
	    		return PDFAMode.PDFA_1B.getName();
	    	}
	    }
	    return null;
	}
	
	public String getPDFAPart(){
	    return pdfaidPart;
	}
	
	public String getPDFAConformance(){
	    return pdfaidConformance;
	}
	
	private Node getNodeByXPath(String xpathString){
		XPath xpath = DocumentHelper.createXPath(xpathString);
	    xpath.setNamespaceURIs(DEFAULT_URI);
	    return xpath.selectSingleNode(xmlDoc);
	}
	
	private String getStringValueByXPath(String xpathString){
		Node node = getNodeByXPath(xpathString);
	    if (node!= null){
	    	String ret = node.getStringValue();
	    	if (ret!= null){
	    		return ret.trim();
	    	}
	    }
	    return null;
	}
	
}
