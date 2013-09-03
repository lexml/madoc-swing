package br.gov.lexml.madoc.data;

import java.util.Collection;

public interface Queriable {

	public abstract Collection<Value> query(String jxpath);

	public abstract Value query1(String jxpath);

	public abstract Collection<String> queryStrings(String jxpath);

	public abstract String queryString(String jxpath, String defaultValue);

	public abstract String queryString(String jxpath);

}