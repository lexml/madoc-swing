package br.gov.lexml.madoc.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultipleStringReplacer {
	
	private Map<String, String> map;
	
	private Pattern findPattern;
	private Pattern replacePattern;
	
	public MultipleStringReplacer(Map<String, String> map, String prefix, String suffix) {
		this.map = map;
		findPattern = Pattern.compile(prefix);
		replacePattern = Pattern.compile(prefix + "(.+?)" + suffix);
	}
	
	public String replace(String src) {
		
		// Verifica se contém o prefixo
		if(findPattern.matcher(src)== null || !findPattern.matcher(src).find()) {
			return src; 
		}

		Matcher m = replacePattern.matcher(src);
		StringBuffer sb = new StringBuffer(); 
		while(m.find()) {
			String key = m.group(1);
			String value = map.get(key);
			if(value == null) {
				m.appendReplacement(sb, Matcher.quoteReplacement(m.group()));
			}
			else {
				m.appendReplacement(sb, Matcher.quoteReplacement(value));
			}
		}
		m.appendTail(sb);
		
		return sb.toString();
	}
	
//	public static void main(String[] args) {
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("hello", "Fulano");
//		MultipleStringReplacer replacer = new MultipleStringReplacer(map, "\\$", "\\$");
//		System.out.println(replacer.replace("Hello $hello$! Hello $outro$! Hello $hello$!"));
//	}

}
