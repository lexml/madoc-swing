package br.gov.lexml.madoc.schema;

import java.text.Collator;
import java.util.Locale;

import br.gov.lexml.madoc.schema.entity.CatalogItemType;
import br.gov.lexml.madoc.schema.entity.MadocAnswerType;
import br.gov.lexml.madoc.schema.entity.MadocDocumentType;
import br.gov.lexml.madoc.schema.entity.MadocDocumentsCatalogItemType;
import br.gov.lexml.madoc.schema.entity.ResourcesCatalogItemType;

public final class Constants {
	
	private Constants(){}

	public static final Locale DEFAULT_LOCALE = new Locale("pt", "BR");
	public static final Collator DEFAULT_COLLATOR = Collator.getInstance(DEFAULT_LOCALE);
	
	public static final String DATE_FORMAT = "dd/MM/yyyy";
	
	public static final String FULL_DATE_FORMAT = "d 'de' MMMM 'de' yyyy";
	public static final String FALSE_STRING = "";
	public static final String TRUE_STRING = "true";
	public static final String SPLIT_TOKEN_VALUES = ";;";
	
	public static final String REQUIRED_FIELD_TEXT = "<html>&nbsp;<sup>(requerido)</sup></html>";
	
	public final static String DEFAULT_URI = "http://www.lexml.gov.br/madoc/1.0";
	public static final String XLOOM_NAMESPACE = "http://www.lexml.gov.br/schema/xloom";
	
	public final static String SHORT_NAMESPACE = "madoc:";
	
	public final static String JXPATH_DEFAULT_REGEX = "^\\{[\\w\\W]*\\}$";
	
	public final static String REPLACEMENT_PREFIX = "@@"; //it used to be: "\\$"
	public final static String REPLACEMENT_SUFFIX = "@@"; //it used to be: "\\$"
	
	public static final long CATALOG_DEFAULT_MAX_AGE_MILLIS = 90 * 1000L; // 1m30s seconds

	
	/**
	 * Refers to {@link MadocAnswerType}
	 */
	public final static String MADOC_ANSWER_ROOT_ELEMENT = "MadocAnswer";
	
	/**
	 * Refers to {@link MadocDocumentType}
	 */
	public final static String MADOC_DOCUMENT_ROOT_ELEMENT = "MadocDocument";
	
	/**
	 * Refers to {@link MadocSkeletonsCatalogItemType} and {@link CatalogItemType}
	 */
	public final static String MADOC_SKELETON_ROOT_ELEMENT = "MadocSkeleton";
	
	/**
	 * Refers to {@link MadocLibraryType}
	 */
	public final static String MADOC_LIBRARY_ROOT_ELEMENT = "MadocLibrary";
	
	/**
	 * Refers to {@link MadocDocumentsCatalogItemType} and {@link CatalogItemType}
	 */
	public final static String MADOC_DOCUMENTS_CATALOG_ELEMENT = "MadocDocuments";
	
	/**
	 * Refers to {@link MadocSkeletonsCatalogItemType} and {@link CatalogItemType}
	 */
	public final static String MADOC_SKELETONS_CATALOG_ELEMENT = "MadocSkeletons";
	
	/**
	 * Refers to {@link MadocLibrariesCatalogItemType} and {@link CatalogItemType}
	 */
	public final static String MADOC_LIBRARIES_CATALOG_ELEMENT = "MadocLibraries";

	/**
	 * Refers to {@link ResourcesCatalogItemType} and {@link CatalogItemType}
	 */
	public final static String RESOURCE_ELEMENT = "Resource";
	
	/**
	 * Refers to {@link ResourcesCatalogItemType} and {@link CatalogItemType}
	 */
	public final static String RESOURCES_CATALOG_ELEMENT = "Resources";

	
}
