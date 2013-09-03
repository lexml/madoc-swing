package br.gov.lexml.madoc.catalog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * %m - Model id %n - Resource name %v - Version
 * 
 * @author joao
 * 
 */
public class PatternUriBuilder implements UriBuilder {

	private final String pattern;

	public PatternUriBuilder(String pattern) {
		super();
		this.pattern = pattern;
	}

	private static String encode(String text) {
		try {
			return text == null ? "" : URLEncoder.encode(text, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(
					"Unexpected exception: platform must support utf-8 encoding",
					e);
		}
	}

	@Override
	public String buildUri(String modelId, String resourceName,
			String modelVersion) {
		if (pattern.length() < 2) {
			return pattern;
		}
		modelId = encode(modelId);
		resourceName = encode(resourceName);
		modelVersion = encode(modelVersion);
		StringBuilder sb = new StringBuilder();

		int len = pattern.length();
		for (int i = 0; i < len; i++) {
			char c1 = pattern.charAt(i);
			char c2 = i < (len - 1) ? pattern.charAt(i + 1) : '*';
			String repl = null;
			if (c1 == '%') {
				switch (c2) {
				case 'm':
					repl = modelId;
					break;
				case 'n':
					repl = resourceName;
					break;
				case 'v':
					repl = modelVersion;
					break;
				}
			}
			if (repl == null) {
				sb.append(c1);
			} else {
				sb.append(repl);
				i++;
			}
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("pattern", pattern).toString();
	}
}
