package br.gov.lexml.madoc.rendition;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;

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
}
