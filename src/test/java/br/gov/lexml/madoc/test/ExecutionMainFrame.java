package br.gov.lexml.madoc.test;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import org.apache.commons.io.FileUtils;

import br.gov.lexml.madoc.MadocException;
import br.gov.lexml.madoc.catalog.CatalogService;
import br.gov.lexml.madoc.execution.swing.WizardExecutionSwing;
import br.gov.lexml.madoc.rendition.Rendition;
import br.gov.lexml.madoc.schema.entity.MadocAnswerType;
import br.gov.lexml.madoc.schema.entity.MapEntryType;
import br.gov.lexml.madoc.schema.entity.ObjectFactory;
import br.gov.lexml.madoc.schema.parser.SaveException;
import br.gov.lexml.madoc.schema.parser.SchemaParser;

class ExecutionMainFrame extends JFrame {

	private static final long serialVersionUID = 3516147565007458624L;
	
	private final String targetRenditions;
	private final String targetSaved;

	ExecutionMainFrame(String targetRenditions, 
			String targetSaved, 
			WizardExecutionSwing wExecution,
			CatalogService catalogService) {
		this.targetRenditions = targetRenditions;
		this.targetSaved = targetSaved;

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		createComponents(wExecution, catalogService);
	}

	private void createComponents(final WizardExecutionSwing wExecution,
			final CatalogService catalogService) {

		// creating control buttons
		// adding save button
		JButton save = new JButton("Salvar");
		save.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// save the answers in a file
				try {
					MadocAnswerType a = wExecution.createMadocAnswer();

					if (a == null) {
						System.out.println("a is null");
					}

					a.setMetadata(new ObjectFactory().createDataSetMapType());

					if (a.getMetadata() == null) {
						System.out.println("a metda is null");
					}

					MapEntryType m = new ObjectFactory().createMapEntryType();
					m.setKey("chave1");
					m.getContent().add("valor1");

					a.getMetadata().getEntry().add(m);

					m = new ObjectFactory().createMapEntryType();
					m.setKey("chave2");
					m.getContent().add("conteudo2");
					a.getMetadata().getEntry().add(m);

					SchemaParser.saveAnswer(
							new File(targetSaved), a);
					System.out.println("Answers saved.");
				} catch (SaveException e1) {
					System.err.println("Could not save answers!");
					e1.printStackTrace();
				}
			}
		});

		// adding changed button
		JButton changed = new JButton("Alterado?");
		changed.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, wExecution.isChanged());
			}
		});

		// adding changed button
		JButton filled = new JButton("Requeridos preencidos?");
		filled.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,
						wExecution.isRequiredQuestionsAnswered());
			}
		});

		// adding print PDF/RTF button
		JButton print = new JButton("Imprimir PDF/RTF/TXT");
		print.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				if (!wExecution.isRequiredQuestionsAnswered()) {
					JOptionPane.showMessageDialog(null,
							"Há campos requeridos não preenchidos");
				} else {
					File d = new File(targetRenditions);
					if (!d.exists()) {
						d.mkdirs();
					}

					Rendition rendition = new Rendition(catalogService, wExecution.createMadocAnswer(), wExecution.getCurrentMadocDocument());
					rendition.saveToPDF(new File(targetRenditions
							+ "rendition.pdf"));
					rendition.saveToRTF(new File(targetRenditions
							+ "rendition.rtf"));
					rendition.saveToTXT(new File(targetRenditions
							+ "rendition.txt"));
					
					try {
						FileUtils.writeStringToFile(new File(targetRenditions+"templateResult.xml"), rendition.getTemplateResult());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					

					JOptionPane.showMessageDialog(null, "Salvo em "
							+ targetRenditions);
				}
			}
		});

		JToolBar toolBar = new JToolBar();
		toolBar.add(save);
		toolBar.add(changed);
		toolBar.add(filled);
		toolBar.add(print);

		//panel title
		JPanel panelTitle = new JPanel();
		JLabel labelTitle = new JLabel();
		//labelTitle.setText(wExecution.getMadocDocument().getWizard().getDisplay());
		labelTitle.setText("Titulo de metadata: "+MetadataUtil.getTitulo(wExecution.getCurrentMadocDocument().getMetadata()));
		
		labelTitle.setFont(new Font(labelTitle.getName(), Font.BOLD, 18));
		panelTitle.add(labelTitle);
		
		JPanel mainPanelNorth = new JPanel();
		mainPanelNorth.setLayout(new BoxLayout(mainPanelNorth, BoxLayout.Y_AXIS));

		mainPanelNorth.add(toolBar);
		mainPanelNorth.add(panelTitle);
		
		//main Panel		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(mainPanelNorth, BorderLayout.NORTH);
		

		// adding the main panel
		try {
			mainPanel.add(wExecution.getComponent(), BorderLayout.CENTER);
		} catch (MadocException e1) {
			System.out.println(e1.getMessage());
			e1.printStackTrace();
		}

		add(mainPanel);
	}

	void showFrame() {
		this.setSize(800, 800);
		// pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

}
