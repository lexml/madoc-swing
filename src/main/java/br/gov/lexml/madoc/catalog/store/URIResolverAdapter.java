package br.gov.lexml.madoc.catalog.store;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URIResolverAdapter implements URIResolver {

	private static final Logger log = LoggerFactory.getLogger(URIResolverAdapter.class);

	private final DocumentStore store;

	public URIResolverAdapter(DocumentStore store) {
		super();
		this.store = store;
	}

	@Override
	public Source resolve(String href, String base) throws TransformerException {
		try {
			URI uri = new URI(href);
			if (base != null) {
				URI uriBase = new URI(href);
				uri = uriBase.resolve(uri);
			}
			InputStream is = store.getDocument(uri.toASCIIString());
			return new StreamSource(is);
		} catch (URISyntaxException e) {
			TransformerException ee = new TransformerException("Syntax error in uri. href = "
					+ href + ", base = " + base + ": " + e.getMessage(), e);
			log.error(e.getMessage(), e);
			throw ee;
		} catch (IOException e) {
			TransformerException ee = new TransformerException("Error retrieving document. href = "
					+ href + ", base = " + base + ": " + e.getMessage(), e);
			log.error(e.getMessage(), e);
			throw ee;
		}
	}

}
