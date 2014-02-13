package br.gov.lexml.madoc.test;

import java.io.File;
import java.io.IOException;

import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;

import br.gov.lexml.madoc.MadocException;
import br.gov.lexml.madoc.catalog.CatalogException;
import br.gov.lexml.madoc.catalog.CatalogService;
import br.gov.lexml.madoc.catalog.CatalogServiceFactory;
import br.gov.lexml.madoc.catalog.local.SDLegLocalCatalogServiceBuilder;
import br.gov.lexml.madoc.catalog.sdleg.SDLegCatalogServiceBuilder;
import br.gov.lexml.madoc.data.DataSets;
import br.gov.lexml.madoc.execution.swing.WizardExecutionSwing;
import br.gov.lexml.madoc.schema.entity.MadocAnswerType;
import br.gov.lexml.madoc.schema.parser.ParseException;
import br.gov.lexml.madoc.schema.parser.SchemaParser;
import br.gov.lexml.madoc.wrappers.MadocLibraryWrapper;

public class MadocMain {

	// PRODUCTION
	/*
	 * private static final String versionedUrlPattern =
	 * "http://www19.senado.gov.br/sdleg-getter/public/getDocument?docverid=$1;$2"
	 * ; private static final String versionLessUrlPattern =
	 * "http://www19.senado.gov.br/sdleg-getter/public/getDocument?docverid=$1";
	 * private static final String CATALOG_RESOURCE_NAME =
	 * "urn:sf:sistema;sdleg:id;8edaf559-789d-48d0-90ed-ee31ac5404c3";
	 */

	// DESENVOLVIMENTO
	
	private static final String SDLEG_VERSIONED_URL_PATTERN = "http://www6gdsv.senado.gov.br/sdleg-getter/public/getDocument?docverid=$1;$2";
	private static final String SDLEG_VERSION_LESS_URL_PATTERN = "http://www6gdsv.senado.gov.br/sdleg-getter/public/getDocument?docverid=$1";
	private static final String SDLEG_CATALOG_RESOURCE_NAME = "urn:sf:sistema;sdleg:id;9110bad3-04aa-4da4-9328-fbae1dd311ed";
	
	
	
	// HOMOLOGAÇÃO
	/*private static final String SDLEG_VERSIONED_URL_PATTERN = "http://www6ghml.senado.gov.br/sdleg-getter/public/getDocument?docverid=$1;$2";
	private static final String SDLEG_VERSION_LESS_URL_PATTERN = "http://www6ghml.senado.gov.br/sdleg-getter/public/getDocument?docverid=$1";
	private static final String SDLEG_CATALOG_RESOURCE_NAME = "urn:sf:sistema;sdleg:id;e3638f33-2c70-4ea6-8a8b-1416edaf4001";
*/	

	private CatalogService catalogService;
	
	// Cria serviço de catálogo
	private final String configDirCache = "target/cache";
	private final String targetRenditions = "target/renditions/";
	private final String targetSaved = "src/test/testAnswers.xml";
	//private static final String configDirLocalModels = "src/test/modelos";

	
	{
		File cache = new File(configDirCache);
		
		try {
			if (cache.exists()){
				FileUtils.deleteDirectory(cache);
				System.out.println("Cache deleted: "+cache.getName());
			}
		} catch (IOException e1) {
			System.err.println("Could not delete "+cache.getName());
			e1.printStackTrace();
		}
		
		try {
			SDLegCatalogServiceBuilder csf = new SDLegCatalogServiceBuilder();
			csf.setCacheDir(cache)
					.setCatalogResourceName(SDLEG_CATALOG_RESOURCE_NAME)
					.setVersionedUrlPattern(SDLEG_VERSIONED_URL_PATTERN)
					.setVersionLessUrlPattern(SDLEG_VERSION_LESS_URL_PATTERN);

			catalogService = csf.createCatalogService();
			
		} catch (CatalogException e) {
			throw new RuntimeException("Error creating catalog service. "
					+ e.getMessage(), e);
		}

	}
	
	public static void main(String[] args) throws Exception {

		SwingUtilities.invokeAndWait(new Runnable() {
			
			@Override
			public void run() {
				
				
				
				MadocMain madoc = new MadocMain();

				// load document 
				//madoc.loadMadocDocumentFromLocalDir("m-teste", "1", new File(configDirLocalModels));
				//madoc.loadMadocDocumentFromSDLeg("m-button-teste");
				//madoc.loadMadocDocumentFromSDLeg("m-teste");
				//madoc.loadMadocDocumentFromSDLeg("m016"); // TESTE DO PDF
				//madoc.loadMadocDocumentFromSDLeg("m001"); 
				//madoc.loadMadocDocumentFromSDLeg("cvm001");	// VE TOS
				//madoc.loadMadocDocumentFromSDLeg("cvm002");	// VETOS COMPLETO
				//madoc.loadMadocDocumentFromSDLeg("m011");
				//madoc.loadMadocDocumentFromSDLeg("m016");
				
				//madoc.showLibraryContent();
				
				// load answers
				//madoc.loadMadocAnswers(new File("src/test/testAnswers.xml"));
				//madoc.loadMadocAnswers(new File("/home/lauroa/Downloads/skeleton-novo.xml"));
				madoc.loadMadocAnswers(new File("/home/lauroa/Downloads/skeleton-antigo.xml"));
			}
			
		});
		
	}
	
	public void showLibraryContent(){
		try {
			MadocLibraryWrapper madocLibraryWrapper = new MadocLibraryWrapper(catalogService, "dataset-library");
			
			DataSets ds = madocLibraryWrapper.getDataSetsWrapper();
			
			System.out.println("**** LibraryContent ****");
			for (Object s : ds.query("/senadores/*/Nome")){
				System.out.println(s.toString());
			}
			
		} catch (CatalogException e) {
			throw new RuntimeException("Error loading Madoc Library. " + e.getMessage(), e);
		} 
	}

	public void loadMadocAnswers(File f) {
		try {
			MadocAnswerType madocAnswers = SchemaParser.loadAnswer(f);
			
			try{
				WizardExecutionSwing wExecution = new WizardExecutionSwing(catalogService, madocAnswers);
				
				startInterview(wExecution);	
			} catch (MadocException e) {
				throw new RuntimeException("Error loading Madoc Document. " + e.getMessage(), e);
			}
		} catch (ParseException e) {
			throw new RuntimeException("Error loading Madoc documents. "
					+ e.getMessage(), e);
		}
	}

	public void loadMadocDocumentFromSDLeg(String id) {
		loadMadocDocumentFromSDLeg(id, null);
	}

	public void loadMadocDocumentFromSDLeg(String id, String version) {
		// load wizard
		try {
			

			
			WizardExecutionSwing wExecution = new WizardExecutionSwing(catalogService, id, version);
			
			startInterview(wExecution);
		} catch (Exception e) {
			throw new RuntimeException("Error loading Madoc documents.", e);
		}
	}
	
	public void loadMadocDocumentFromLocalDir(String id, String version, File directory) {
		try {
			CatalogServiceFactory csf = new SDLegLocalCatalogServiceBuilder(directory);

			catalogService = csf.createCatalogService();
			
		} catch (CatalogException e) {
			throw new RuntimeException("Error creating catalog service. "
					+ e.getMessage(), e);
		}

		// load wizard
		try {
			WizardExecutionSwing wExecution = new WizardExecutionSwing(catalogService, id, version);
			
			startInterview(wExecution);
		} catch (Exception e) {
			throw new RuntimeException("Error loading Madoc documents. " + e.getMessage(), e);
		}
	}
	

	/**
	 * Creates default frame
	 * @param wExecution
	 */
	public void startInterview(WizardExecutionSwing wExecution) {
		(new ExecutionMainFrame(targetRenditions, targetSaved, wExecution, catalogService)).showFrame();
	}
}