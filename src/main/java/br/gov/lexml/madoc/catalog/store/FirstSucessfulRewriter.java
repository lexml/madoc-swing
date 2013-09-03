package br.gov.lexml.madoc.catalog.store;

import java.util.Arrays;
import java.util.List;

public class FirstSucessfulRewriter implements Rewriter {

	private final List<Rewriter> rewriters;
	
	public FirstSucessfulRewriter(Rewriter... rws) {
		rewriters = Arrays.asList(rws);
	}
	
	@Override
	public String rewriteUri(String docUri) {
		for(Rewriter r : rewriters) {
			String res = r.rewriteUri(docUri);
			if(res!=null) {
				return res;
			}
		}
		return null;
	}

}
