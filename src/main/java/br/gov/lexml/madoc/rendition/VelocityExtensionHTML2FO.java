package br.gov.lexml.madoc.rendition;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.MapUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import br.gov.lexml.swing.editorhtml.util.HTML2FOConverter;

class VelocityExtensionHTML2FO {

	private static VelocityExtensionHTML2FO instance;
	
	private HTML2FOConverter html2foConverter;
	
	private VelocityExtensionHTML2FO() {
		Properties conf = new Properties();
		conf.put(HTML2FOConverter.CONF_PARAGRAPH_MARGIN_BOTTOM, "$pMarginBottomDefault");
		html2foConverter = new HTML2FOConverter(conf);
	}
	
	VelocityExtensionHTML2FO(@SuppressWarnings("rawtypes") Map mapConf) {
		Properties conf = MapUtils.toProperties(mapConf);
		html2foConverter = new HTML2FOConverter(conf);
	}
	
	static VelocityExtensionHTML2FO getInstance(){
		if (instance == null){
			instance = new VelocityExtensionHTML2FO();
		}
		return instance;
	}
	
	// HTML to XSL-FO
	public String html2fo(String html, VelocityContext ctx, VelocityEngine velocityEngine) {
		String fo = html2foConverter.html2fo(html);
		return VelocityExtensionUtils.render(fo, ctx, velocityEngine);
	}
	
}
