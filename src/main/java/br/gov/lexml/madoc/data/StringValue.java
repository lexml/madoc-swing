package br.gov.lexml.madoc.data;

import java.util.Collection;

public final class StringValue implements Value {
	private final String value;
	public StringValue(String value) {
		this.value = value;
	}
	@Override
	public Type getType() { 
		return Type.STRING;
	}
	public String getValue() {
		return value;
	}

	public String toString() {
		return value;
	}
	
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
 