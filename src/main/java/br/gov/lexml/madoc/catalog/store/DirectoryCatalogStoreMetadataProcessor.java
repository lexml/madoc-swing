package br.gov.lexml.madoc.catalog.store;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import br.gov.lexml.madoc.schema.Constants;

/**
 * Process Metadata in DirectoryCatalogStore. Extract a metadata tag from a MadocDocumentBaseType.
 * @author lauro
 *
 */
class DirectoryCatalogStoreMetadataProcessor{
	
	private NamespaceContext ctx = new NamespaceContext() {

		@Override
		public String getNamespaceURI(String prefix) {
			if (prefix.equals("madoc")) {
				return Constants.DEFAULT_URI;
			} else {
				return null;
			}
		}

		@Override
		public String getPrefix(String namespaceURI) {
			if (namespaceURI.equals(Constants.DEFAULT_URI)) {
				return "madoc";
			}
			return null;
		}

		@Override
		public Iterator<?> getPrefixes(String namespaceURI) {
			return null;
		}

	};
	
	private XPathExpression pe;
	
	{
		// creating XPath parser
		XPathFactory pf = XPathFactory.newInstance();
		XPath p = pf.newXPath();
		p.setNamespaceContext(ctx);
		try {
			pe = p.compile("//madoc:Metadata");
		} catch (XPathExpressionException e) {
			throw new RuntimeException("Error compiling xpath expression: " + e.getMessage(), e);
		}
	}
	
	public Element getMetadataElement(Document doc) throws XPathExpressionException{
		return (Element) pe.evaluate(doc, XPathConstants.NODE);
	}
	
}