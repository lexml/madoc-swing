package br.gov.lexml.madoc.components.swing.jcomponents;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.gov.lexml.madoc.catalog.CatalogService;

public class HtmlContentPane extends JTextPane{
	
	private static final long serialVersionUID = 2528172317916643259L;

	private static final Logger log = LoggerFactory.getLogger(QuestionPanel.class);
	
	private static final Pattern imgSrcPattern = Pattern.compile("<img.*src=['\"](.*?)['\"]");
	
	/**
	 * Create a JTextPane using resources from CatalogService
	 * @param content
	 * @param catalogService
	 * @return
	 */

	public HtmlContentPane(final String content, final CatalogService catalogService){
		this.setContentType("text/html");
		this.setEditable(false);
		this.setBackground(SwingConstants.LABEL_COLOR);
		
		String contentReplaced = replaceImgSrcToURL(content);
		
		this.setText(contentReplaced);
		
		// controlling image cache
		addImageCache(this, catalogService, getListImageSrc(contentReplaced));
		
		// adding links listener	
		addLinkListener(this);
	}
	

	/**
	 * @param textPane
	 * @param catalogService
	 */
	private void addImageCache(JTextPane textPane, CatalogService catalogService, List<String> imagesSrc){
		
        try {
        	//getting image cache from JTextPane
			@SuppressWarnings("unchecked")
			Dictionary<URL, Image> cache= (Dictionary<URL, Image>)textPane.getDocument().getProperty("imageCache");
            if (cache == null) {
                cache = new Hashtable<URL, Image>();
                textPane.getDocument().putProperty("imageCache", cache);
            }

            for (String src : imagesSrc){
                cache.put(new URL(src), createImageFromSrc(src, catalogService));
            	
            	
            }
            
        } catch (MalformedURLException e) {
            log.error("MalformedURLException when creatig URL from imageSrc: "+e.getMessage(), e);
        }
	}
	
	/**
	 * Create an Image from a img's src property.
	 * @param imageSrc
	 * @param catalogService
	 * @return
	 */
	private Image createImageFromSrc(String imageSrc, CatalogService catalogService){
		try {
			Source source = catalogService.getURIResolver().resolve(imageSrc, null);
			if (source instanceof StreamSource){
				return Toolkit.getDefaultToolkit().createImage(IOUtils.toByteArray(((StreamSource) source).getInputStream()));
			}
		} catch (TransformerException e) {
			log.error("Exception resolving image source: "+imageSrc+" - "+e.getMessage(), e);
		} catch (IOException e) {
			log.error("IOException resolving image source: "+imageSrc+" - "+e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * Replace the img's src attributes with a URL complaint version 
	 * @param htmlContent
	 * @param imgSrcs
	 * @return
	 */
	private String replaceImgSrcToURL(final String htmlContent){
		
		String result = htmlContent;
		for (String src : getListImageSrc(result)){
			
			if (!src.startsWith("http") && !src.startsWith("https") && !src.startsWith("file") && !src.startsWith("ftp")){
				String newName = "http://localmadoc/"+src;
				result = result.replaceAll(src, newName);
			}
		}
		
		return result;
	}
	
	
	/**
	 * Create a list of img's src contents from htmlContet
	 * @param content
	 * @return
	 */
	private List<String> getListImageSrc(final String htlmContent){
		ArrayList<String> result = new ArrayList<String>();
		
		Matcher m = imgSrcPattern.matcher(htlmContent);
		
		while (m.find()){
			result.add(m.group(1));
		}
		
		return result;
	}
	
	/**
	 * @param textPane
	 */
	private void addLinkListener(JTextPane textPane){
		
		textPane.addHyperlinkListener(new HyperlinkListener(){

			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
					
		        if( !java.awt.Desktop.isDesktopSupported() ) {
		        	log.error("hyperlinkUpdate: Desktop is not supported.");
		        }   
		        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

		        if( !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) {
		        	log.error( "hyperlinkUpdate: Desktop doesn't support the browse action.");
		        }

				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
					log.debug("URL: "+e.getURL()+"; description: "+e.getDescription()+"; eventType: "+e.getEventType());
					try {
						desktop.browse(e.getURL().toURI());
					} catch (IOException e1) {
						log.error("hyperlinkUpdate: IOException on hyperlink ACTIVATED event - "+e1.getMessage(), e1);
					} catch (URISyntaxException e1) {
						log.error("hyperlinkUpdate: URISyntaxException on hyperlink ACTIVATED event - "+e1.getMessage(), e1);
					}
				}
			}
		});
	}
	
}
