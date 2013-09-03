package br.gov.lexml.madoc.execution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.gov.lexml.madoc.data.DataSets;
import br.gov.lexml.madoc.data.Value;
import br.gov.lexml.madoc.schema.Constants;
import br.gov.lexml.madoc.schema.entity.BaseOptionInterface;
import br.gov.lexml.madoc.schema.entity.BaseOptionType;
import br.gov.lexml.madoc.schema.entity.CheckBoxGroupQuestionType;
import br.gov.lexml.madoc.schema.entity.ChoiceListQuestionType;
import br.gov.lexml.madoc.schema.entity.ComboQuestionType;
import br.gov.lexml.madoc.schema.entity.MadocDocumentType;
import br.gov.lexml.madoc.schema.entity.OptionableQuestionInterface;
import br.gov.lexml.madoc.schema.entity.RadioBoxGroupQuestionType;
import br.gov.lexml.madoc.schema.entity.visitor.BaseVisitor;
import br.gov.lexml.madoc.schema.entity.visitor.VisitorAction;
import br.gov.lexml.madoc.util.ReflectionUtil;

public class OptionsExpansion {
	
	private static final Logger log = LoggerFactory.getLogger(OptionsExpansion.class);

	private MadocDocumentType finalMadocDocument;

		
	/**
	 * Clone the MadocDocumentType and:
	 * 	1) expands options containing jxPath instructions
	 *  2) sort options if needed
	 * 
	 * @param madocDocument
	 * @return
	 */
	public MadocDocumentType cloneAndExpandOptions(
			final MadocDocumentType madocDocument) {

		//cloning madocDocument
		finalMadocDocument = (MadocDocumentType) madocDocument.clone();

		finalMadocDocument.accept(new BaseVisitor() {

			public <Q extends BaseOptionInterface<O>, O extends BaseOptionType> 
				VisitorAction processOptions(OptionableQuestionInterface<Q> aBean) {
				
				if (aBean.isSetOptions()){
					BaseOptionInterface<O> opt = aBean.getOptions();
						
					if ((opt.isSetDataSetBind() || opt.isSorted()) 
						&& opt.isSetOption()) {

						List<O> listSOT = new ArrayList<O>();
						
						//biding with dataset
						if (opt.isSetDataSetBind()){
							for (O o : opt.getOption()) {
								
								// getting the list of gets and sets 
								Map<String, ReflectionUtil.MethodPair> getsSets = getGetSetMethods(o);
								
								// if the options has no jxpath information, then they are kept with no change
								if (getsSets.isEmpty()){
									listSOT.add(o);
								} else {
									listSOT.addAll(createOptions(opt.getDataSetBind(), o, getsSets));
								}
							}
						} else {
							listSOT = opt.getOption();
						}
						
						//sorting
						if (opt.isSorted()){
							Collections.sort(listSOT, new Comparator<O>(){

								@Override
								public int compare(O o1, O o2) {
									String s1 = StringUtils.defaultString(o1.getDisplay(), o1.getValue());
									String s2 = StringUtils.defaultString(o2.getDisplay(), o2.getValue());
									
									return Constants.DEFAULT_COLLATOR.compare(s1, s2);
								}
								
							});
						}
						
						opt.getOption().clear();
						opt.getOption().addAll(listSOT);
					}
				}
					
				return VisitorAction.CONTINUE;
			}
			
			@Override
			public VisitorAction enter(ComboQuestionType aBean) {
				return processOptions(aBean);
			}
			
			@Override
			public VisitorAction enter(CheckBoxGroupQuestionType aBean) {
				return processOptions(aBean);
			}
			
			@Override
			public VisitorAction enter(RadioBoxGroupQuestionType aBean) {
				return processOptions(aBean);
			}
			
			@Override
			public VisitorAction enter(ChoiceListQuestionType aBean) {
				return processOptions(aBean);
			}

		});

		return finalMadocDocument;
	}

	/**
	 * Create a new list of Options. This method uses reflection to identify <br>
	 * string get/set methods that contains JXPath inside of {}. Then, it replaces the field content by <br>
	 * JXPath query result.<br>
	 * The option id is generated with the concatenation of original option id as a prefix and a counter as a suffix.  
	 * @param jxPathBind
	 * @param originalOption
	 * @return
	 */
	private <O extends BaseOptionType> List<O> createOptions(String jxPathBind, O originalOption, Map<String, ReflectionUtil.MethodPair> getSets) {
		
		List<O> list = new ArrayList<O>();

		// if there is no dataset, then there is nothing to be done
		if (!finalMadocDocument.isSetDataSets()){
			return list;
		}
		
		// do the bind on dataset 
		DataSets dataSets = DataSets.fromDataSets(finalMadocDocument.getDataSets());
		Collection<Value> nodes = dataSets.query(jxPathBind);
		
		if(log.isDebugEnabled()) {
			log.debug("nodes = " + nodes);
		}

		// iterating over bind result
		int seqId= 1;
		for (Value value : nodes) {
			
			//cloning original option
			@SuppressWarnings("unchecked")
			O sot = (O) originalOption.clone();
			
			for (Map.Entry<String, ReflectionUtil.MethodPair> entry : getSets.entrySet()) {
				
				ReflectionUtil.MethodPair mp = entry.getValue();
				String originalValue = mp.get(sot);
				String query = originalValue.substring(1, originalValue.length() - 1);
				if(log.isDebugEnabled()) {
					log.debug("query: " + query + ", value: " + value);
				}
				String newValue = value.queryString(query);
				if(log.isDebugEnabled()) {
					log.debug("newValue: " + newValue);
				}
				mp.set(sot, newValue);
			}
			//setting new ID
			sot.setId(originalOption.getId().concat("_").concat(String.valueOf(seqId++)));
			
			//adding new sot to the list
			list.add(sot);
		}
		return list;
	}
	
	/**
	 * Returns the map of field name and the get and set pair of methods from that field.<br>
	 * Only String fields that value matches to Constants.JXPATH_DEFAULT_REGEX, except the id, are considered.
	 * @param sot
	 * @return
	 */
	private Map<String, ReflectionUtil.MethodPair> getGetSetMethods(BaseOptionType sot){
		return ReflectionUtil.getGetSetMethods(sot, String.class, Constants.JXPATH_DEFAULT_REGEX, Arrays.asList("id"));
	}

}
