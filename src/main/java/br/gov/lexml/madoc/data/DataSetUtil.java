package br.gov.lexml.madoc.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.lexml.madoc.schema.entity.BaseDataSetEntryType;
import br.gov.lexml.madoc.schema.entity.DataSetListType;
import br.gov.lexml.madoc.schema.entity.DataSetMapType;
import br.gov.lexml.madoc.schema.entity.DataSetType;
import br.gov.lexml.madoc.schema.entity.DataSetValueType;
import br.gov.lexml.madoc.schema.entity.DataSetsType;
import br.gov.lexml.madoc.schema.entity.ListEntryType;
import br.gov.lexml.madoc.schema.entity.MapEntryType;

public class DataSetUtil {
	
	private static final Log log = LogFactory.getLog(DataSetUtil.class);
	
	public static MapValue fromDataSets(DataSetsType dataSets) {
		MapValue mv = new MapValue();
		for (DataSetType ds : dataSets.getDataSet()) {
			String id = ds.getId();
			Value v = valueFromEntry(ds);
			if (v != null) {
				mv.put(id, v);
			}
		}
		return mv;
	}

	public static CollectionValue valueFromDataSetValue(DataSetValueType value) {
		if (value instanceof DataSetListType) {
			DataSetListType l = (DataSetListType) value;
			ListValue lv = new ListValue();
			for (ListEntryType e : l.getEntry()) {
				Value v = valueFromEntry(e);
				if (v != null) {
					lv.add(v);
				}
			}
			return lv;
		} else if (value instanceof DataSetMapType) {
			DataSetMapType m = (DataSetMapType) value;
			MapValue mv = new MapValue();
			for (MapEntryType e : m.getEntry()) {
				Value v = valueFromEntry(e);
				String key = e.getKey();
				if (v != null) {
					mv.put(key, v);
				}
			}
			return mv;
		}
		return null;
	}

	private static Value valueFromEntry(BaseDataSetEntryType entry) {
		List<Value> v = new ArrayList<Value>();
		for (Serializable o : entry.getContent()) {
			if (o instanceof String) {
				v.add(new StringValue((String) o));
			} else if (o instanceof JAXBElement<?>) {
				JAXBElement<?> e = (JAXBElement<?>) o;
				if (DataSetValueType.class.isAssignableFrom(e.getDeclaredType())) {
					DataSetValueType t = (DataSetValueType) e.getValue();
					Value vv = valueFromDataSetValue(t);
					if (vv != null) {
						v.add(vv);
					}
				}
			}
		}
		if (v.size() > 1) {
			for (Value vv : v) {
				if (vv instanceof CollectionValue) {
					return vv;
				}
			}
		}
		if (!v.isEmpty()) {
			return v.get(0);
		}
		return null;
	}
	
	
	public static Collection<Value> query(Queriable context,String jxpath) {
		JXPathContext ctx = JXPathContext.newContext(context);
		Object o;
		try {
			o = ctx.selectNodes(jxpath);
			//o = ctx.getValue(jxpath);
		} catch(JXPathException e){
			log.debug(e.getMessage(), e);
			o = null;
		}
		if(o instanceof Value) {
			return Collections.singleton((Value)o);
		} else if (o instanceof Collection<?>) {
			List<Value> v = new ArrayList<Value>();
			for(Object o1 : ((Collection<?>) o)) {
				if(o1 instanceof Value) {
					v.add((Value) o1);
				}
			}
			return v;
		} else {
			return new ArrayList<Value>();
		}
	}
	
	public static Value query1(Queriable context,String jxpath) {
		Collection<Value> r = query(context,jxpath);
		if(r.isEmpty()) { 
			return null;
		} else {
			return r.iterator().next();
		}
	}
	
	public static Collection<String> queryStrings(Queriable context,String jxpath) {
		JXPathContext ctx = JXPathContext.newContext(context);
		Object o;
		try {
			o = ctx.getValue(jxpath);
		} catch(JXPathException e){
			log.debug(e.getMessage(), e);
			o = null;
		}
		if(o instanceof StringValue) {
			return Collections.singleton(((StringValue)o).getValue());
		} else if (o instanceof String) {
			return Collections.singleton((String)o);
		} else if (o instanceof Collection<?>) {
			List<String> v = new ArrayList<String>();
			for(Object o1 : ((Collection<?>) o)) {
				if(o1 instanceof StringValue) {
					v.add(((StringValue) o1).getValue());
				} else if (o1 instanceof String) {
					v.add((String)o1);
				}
			}
			return v;
		} else {
			return new ArrayList<String>();
		}
	}
	public static String queryString(Queriable context,String jxpath, String defaultValue) {
		Collection<String> r = queryStrings(context,jxpath);
		if(r.isEmpty()) { 
			return defaultValue;
		} else {
			StringBuilder sb = new StringBuilder();
			for (String s : r){
				if (sb.length() != 0){
					sb.append(", ");
				}
				sb.append(s);
			}
			
			return sb.toString();
		}
	}
	public static String queryString(Queriable context,String jxpath) {
		return queryString(context,jxpath,null);
	}
}
