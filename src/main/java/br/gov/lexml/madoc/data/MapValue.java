package br.gov.lexml.madoc.data;

import java.util.Collection;
import java.util.TreeMap;

public class MapValue extends TreeMap<String, Value> implements CollectionValue {

	private static final long serialVersionUID = 1L;

	@Override
	public Type getType() { return Type.MAP; }
	
	@Override
	public Collection<Value> query(String jxpath) {
		return DataSetUtil.query(this,jxpath);
	}
	@Override
	public Value query1(String jxpath) {
		return DataSetUtil.query1(this,jxpath);
	}
	@Override
	public Collection<String> queryStrings(String jxpath) {
		return DataSetUtil.queryStrings(this, jxpath);
	}
	@Override
	public String queryString(String jxpath, String defaultValue) {
		return DataSetUtil.queryString(this, jxpath,defaultValue);
	}
	@Override
	public String queryString(String jxpath) {
		return DataSetUtil.queryString(this, jxpath);
	}
}
