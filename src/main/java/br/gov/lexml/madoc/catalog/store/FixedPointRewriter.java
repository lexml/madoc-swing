package br.gov.lexml.madoc.catalog.store;

public class FixedPointRewriter implements Rewriter {

	private final Rewriter baseRewriter;
			
	public FixedPointRewriter(Rewriter baseRewriter) {
		super();
		this.baseRewriter = baseRewriter;
	}

	@Override
	public String rewriteUri(String docUri) {
		String res = baseRewriter.rewriteUri(docUri);
		while(true) {
			String r = baseRewriter.rewriteUri(res);
			if(r == null || r.equals(res)) {
				return res;
			}
			res = r;
		}		
	}

}
