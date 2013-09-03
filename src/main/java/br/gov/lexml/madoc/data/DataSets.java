package br.gov.lexml.madoc.data;

import java.util.Collection;

import br.gov.lexml.madoc.schema.entity.DataSetsType;

public class DataSets implements Queriable {
	public static final DataSets EMPTY = new DataSets(new MapValue());
	private final MapValue dataSets;
	public DataSets(MapValue dataSets) {
		this.dataSets = dataSets;
	}
	
	
	public MapValue getDataSets() {
		return dataSets;
	}
	
	public static DataSets fromDataSets(DataSetsType dataSets) {
		return new DataSets(DataSetUtil.fromDataSets(dataSets));
	}


	@Override
	public Collection<Value> query(String jxpath) {
		return dataSets.query(jxpath);
	}


	@Override
	public Value query1(String jxpath) {
		return dataSets.query1(jxpath);
	}


	@Override
	public Collection<String> queryStrings(String jxpath) {
		return dataSets.queryStrings(jxpath);
	}


	@Override
	public String queryString(String jxpath, String defaultValue) {
		return dataSets.queryString(jxpath,defaultValue);
	}


	@Override
	public String queryString(String jxpath) {
		return dataSets.queryString(jxpath);
	}
	
	
}
