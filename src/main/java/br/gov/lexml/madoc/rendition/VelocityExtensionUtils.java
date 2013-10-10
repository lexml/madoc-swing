package br.gov.lexml.madoc.rendition;

import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import br.gov.lexml.madoc.schema.Constants;

final class VelocityExtensionUtils {

	private VelocityExtensionUtils(){}
	
	static Double stringToDouble(String value) throws NumberFormatException{
		if (value == null){
			return new Double(0);
		}
		
		try{
			DecimalFormat df = new DecimalFormat();
			df.setDecimalFormatSymbols(new DecimalFormatSymbols(Constants.DEFAULT_LOCALE));
			return df.parse(value).doubleValue();
		} catch (ParseException e){
			try {
				return Double.valueOf(value.trim());
			} catch (NumberFormatException e1){
				return new Double(0);
			}
		}
	}
	
	static String formatCurrencyNumber(String value) throws NumberFormatException{
		double number = stringToDouble(value);
		
		NumberFormat formatter = NumberFormat.getCurrencyInstance();

		return formatter.format(number);
	}

	/**
	 * Processa o renderizador Velocity do conteúdo de vtl
	 */
	public static String render(String vtl, VelocityContext ctx, VelocityEngine velocityEngine) {
		
		if (vtl == null) {
			return null;
		}
		
		StringWriter sw = new StringWriter();
		
		boolean success;
		if (velocityEngine == null) {
			success = Velocity.evaluate(ctx, sw, VelocityExtensionHTML2FO.class.getName(), vtl);
		} else {
			success = velocityEngine.evaluate(ctx, sw, VelocityExtensionHTML2FO.class.getName(), vtl);
		}
		
		return success? sw.toString(): vtl; 
	}
}
