package br.gov.lexml.madoc.catalog.store;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegexRewriter implements Rewriter {

	private static final Logger log = LoggerFactory.getLogger(RegexRewriter.class);
	
	private final Pattern pat;
	
	private final String replacement;
		
	public RegexRewriter(String pat, String replacement) {
		super();
		this.pat = Pattern.compile(pat);
		this.replacement = replacement;
	}

	@Override
	public String rewriteUri(String docUri) {
		if(log.isDebugEnabled()) {
			log.debug("rewriteUri: this = " + this + ", docUri = " + docUri);			
		}
		Matcher m = pat.matcher(docUri);
		if(m.matches()) {			
			String repl = m.replaceAll(replacement);
			if(log.isDebugEnabled()) {
				log.debug("rewriteUri: repl = " + repl);
			}
			return repl;
		} else {
			log.debug("rewriteUri: no match!");
			return null;
		}				
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("pat",pat.pattern())
			.append("replacement",replacement)
			.toString();
	}
}
