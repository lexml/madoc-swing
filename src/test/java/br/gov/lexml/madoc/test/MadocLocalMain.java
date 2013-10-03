package br.gov.lexml.madoc.test;

import java.io.File;
import java.io.IOException;

import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;

import br.gov.lexml.madoc.MadocException;
import br.gov.lexml.madoc.catalog.CatalogException;
import br.gov.lexml.madoc.catalog.CatalogService;
import br.gov.lexml.madoc.catalog.CatalogServiceFactory;
import br.gov.lexml.madoc.catalog.ModelInfo;
import br.gov.lexml.madoc.catalog.local.SDLegLocalCatalogServiceBuilder;
import br.gov.lexml.madoc.execution.swing.WizardExecutionSwing;
import br.gov.lexml.madoc.schema.entity.MadocAnswerType;
import br.gov.lexml.madoc.schema.parser.ParseException;
import br.gov.lexml.madoc.schema.parser.SchemaParser;

public class MadocLocalMain {

	// DESENVOLVIMENTO
	/*
	private static final String SDLEG_VERSIONED_URL_PATTERN = "http://www6gdsv.senado.gov.br/sdleg-getter/public/getDocument?docverid=$1;$2";
	private static final String SDLEG_VERSION_LESS_URL_PATTERN = "http://www6gdsv.senado.gov.br/sdleg-getter/public/getDocument?docverid=$1";
	private static final String SDLEG_CATALOG_RESOURCE_NAME = "urn:sf:sistema;sdleg:id;9110bad3-04aa-4da4-9328-fbae1dd311ed";
	*/
	
	private CatalogService catalogService;
	
	// Cria serviço de catálogo
	private final String configDirCache = "target/cache";
	private final String targetRenditions = "target/renditions/";
	private final String targetSaved = "src/test/testAnswers.xml";
	
	private static final File configLocalDirModels = new File("src/test/modelos");
	
	
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
			SDLegLocalCatalogServiceBuilder csf = new SDLegLocalCatalogServiceBuilder(configLocalDirModels);
			
			/*csf.setCacheDir(cache)
					.setCatalogResourceName(SDLEG_CATALOG_RESOURCE_NAME)
					.setVersionedUrlPattern(SDLEG_VERSIONED_URL_PATTERN)
					.setVersionLessUrlPattern(SDLEG_VERSION_LESS_URL_PATTERN);
					*/

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
				
				
				
				MadocLocalMain madoc = new MadocLocalMain();

				// load document 
				madoc.loadMadocDocumentFromSDLeg("m001");	// VETOS COMPLETO
				//madoc.loadMadocDocumentFromSDLeg("m011");
				
			
				// load answers
				//madoc.loadMadocAnswers(new File("src/test/testAnswers.xml"));
			}
			
		});
		
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
			
			for (ModelInfo mf : catalogService.getAvailableMadocDocumentModels()){
				
				System.out.println("--------------------------");
				System.out.println(mf.getModelId());
				System.out.println("--------------------------");
			}

			
			
			
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