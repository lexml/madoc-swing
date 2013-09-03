package br.gov.lexml.madoc.components;

import java.util.Set;

import br.gov.lexml.madoc.schema.entity.PageType;
import br.gov.lexml.madoc.schema.entity.SectionType;

public interface PageComponent<S extends BaseWizardComponent<SectionType, C>, C> 
	extends ParentComponent<PageType, C>, BaseWizardComponent<PageType, C>{
	
	/**
	 * Add each Panel from SectionsComponents in the resultPanel
	 * @param section
	 */
	void addSections(Set<S> sections);

}
