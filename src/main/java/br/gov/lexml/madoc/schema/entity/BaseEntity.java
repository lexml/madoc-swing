package br.gov.lexml.madoc.schema.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class BaseEntity {

	private static final Logger log = LoggerFactory.getLogger(BaseEntity.class);

	@Override
	public boolean equals(Object obj) {

		//BaseWizardType objects are equals when they have the same id.
		if (this instanceof BaseWizardType && 
				obj instanceof BaseWizardType) {
			return ((BaseWizardType)obj).getId().equals(((BaseWizardType)this).getId());
		}
		
		//Objects are equals when they have the same properties values.
		boolean eq = EqualsBuilder.reflectionEquals(this, obj);
		if (!eq && log.isDebugEnabled()){
			log.debug(this + " not equal to "+obj);
		}
		return eq;
	}
	
	
	
}