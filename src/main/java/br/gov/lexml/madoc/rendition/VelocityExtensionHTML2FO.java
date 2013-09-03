package br.gov.lexml.madoc.rendition;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.MapUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
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
		return render(fo, ctx, velocityEngine);
	}
	
	/**
	 * Processa o renderizador Velocity do conteúdo de vtl
	 * @param vtl
	 * @return
	 */
	private String render(String vtl, VelocityContext ctx, VelocityEngine velocityEngine) {
		
		if (vtl == null) {
			return null;
		}
		
		StringWriter sw = new StringWriter();
		
		boolean success;
		if (velocityEngine == null) {
			success = Velocity.evaluate(ctx, sw, getClass().getName(), vtl);
		} else {
			success = velocityEngine.evaluate(ctx, sw, getClass().getName(), vtl);
		}
		
		return success? sw.toString(): vtl; 
	}
	
}
