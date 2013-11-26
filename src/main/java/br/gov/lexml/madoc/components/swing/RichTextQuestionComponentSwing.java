package br.gov.lexml.madoc.components.swing;

import java.io.File;

import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.lexml.madoc.components.ComponentController;
import br.gov.lexml.madoc.components.swing.jcomponents.QuestionPanel;
import br.gov.lexml.madoc.execution.hosteditor.HostEditor;
import br.gov.lexml.madoc.schema.entity.QuestionAnswerType;
import br.gov.lexml.madoc.schema.entity.RichTextQuestionType;
import br.gov.lexml.madoc.util.XMLUtil;
import br.gov.lexml.swing.editorhtml.EditorHtml;
import br.gov.lexml.swing.editorhtml.EditorHtmlFactory;
import br.gov.lexml.swing.editorhtml.behaviors.AutoCurvedQuotesBehavior;
import br.gov.lexml.swing.editorhtml.behaviors.AutoFirstLineIndentBehavior;
import br.gov.lexml.swing.editorhtml.behaviors.CSSRulesBehavior;
import br.gov.lexml.swing.editorhtml.behaviors.OmissisBehavior;
import br.gov.lexml.swing.editorhtml.behaviors.SpellcheckBehavior;
import br.gov.lexml.swing.editorhtml.behaviors.ToggleParagraphIndentBehavior;
import br.gov.lexml.swing.editorhtml.handlers.CardLayoutMaximizeHandler;
import br.gov.lexml.swing.spellchecker.SpellcheckerInitializationException;

import com.hexidec.ekit.EkitCore;

class RichTextQuestionComponentSwing extends AbstractQuestionComponentSwing<RichTextQuestionType> {
	
	private static final Log log = LogFactory.getLog(RichTextQuestionComponentSwing.class);

	private EditorHtml editor;
	private JPanel cardLayoutMaximizePanel;
	private String defaultValue;
	
	// Se o editor não tiver sido alterado pelo usuário
	// o getValue retornará o textoOriginal.
	// Este comportamento é necessário para que não se perca
	// as larguras das colunas de tabela salvas no textoOrigial.
	private String textoOriginal = "";
	
	public RichTextQuestionComponentSwing(RichTextQuestionType question,
			ComponentController controller, JPanel cardLayoutMaximizePanel) {
		super(question, controller);
		this.cardLayoutMaximizePanel = cardLayoutMaximizePanel;
	}

	@Override
	protected QuestionPanel createComponent() {
		final QuestionPanel panel = createDefaultQuestionPanel(wizardElement, componentController.getCatalogService());

		boolean inline = wizardElement.isInline();
		
		editor = new EditorFactory(inline).createEditorHtml();
		editor.setHeigtInLines(wizardElement.getLines().intValue());
		
		editor.setBorder(null);

		// setar valor default depois do addListener
		editor.setDocumentText(hostEditorReplacer.replaceString(wizardElement.getDefaultValue()));
		
		defaultValue = editor.getDocumentBody(); // Armazena valor default processado pelo editor  
		
		// Criar handler para maximização do editor
		CardLayoutMaximizeHandler mh = new CardLayoutMaximizeHandler(editor, cardLayoutMaximizePanel) {
			
			@Override
			public void addEditorToDefaultContainer() {
				panel.add(editor);
			}
			
		};
		editor.setMaximizeHandler(mh);
		mh.addEditorToDefaultContainer();
		
		return panel;
	}
	
	@Override
	protected QuestionAnswerType createAnswer(QuestionAnswerType qat) {
		qat.setValue(editor.isDirty()? editor.getDocumentBody(): textoOriginal);
		return qat;
	}

	@Override
	protected void answerUpdated(QuestionAnswerType qat) {
		setValue(qat.getValue());
	}
	
	@Override
	public void setValue(String value) {
		textoOriginal = hostEditorReplacer.replaceString(value);
		editor.setDocumentText(textoOriginal);
	}
	
	@Override
	public void setDefaultValue(String value) {
		String currentValue = editor.getDocumentBody();
		boolean isEmpty = StringUtils.isEmpty(currentValue);
		if(isEmpty || defaultValue.equals(currentValue)) {
			setValue(value);
			defaultValue = editor.getDocumentBody(); // Armazena valor default processado pelo editor  
		}
	}
	
	@Override
	public void addValue(String value) {
		setValue(getValue()+hostEditorReplacer.replaceString(value));
	} 
	
	@Override
	public boolean isRequiredValueReached() {
			
		if (!super.isRequiredValueReached()){
			return false;
		}
		
		String v = XMLUtil.removeEntitiesAndNormalize(getValue());
		String d = XMLUtil.removeEntitiesAndNormalize(wizardElement.getDefaultValue());
		
		if (wizardElement.isSetDefaultValue() 
				&& wizardElement.isSetDefaultValueSatisfiesRequiredQuestion()
				&& !wizardElement.isDefaultValueSatisfiesRequiredQuestion()){
			return !v.equals(d);
		}
			
		return true;
	}

	
	private class EditorFactory extends EditorHtmlFactory {
		
		public EditorFactory(boolean inline) {
			
			setInlineEdit(inline);
			
			addToToolbar(TOOLBAR_DEFAULT);

			if(!inline) {
				addToToolbar("SP");
				addToToolbar(TOOLBAR_PARAGRAPH);
				addToToolbarAtPosition(EkitCore.KEY_TOOL_ALIGNJ, "SP", 
						ToggleParagraphIndentBehavior.KEY_TOOL_TOGGLE_PARAGRAPH_INDENT);
				addToToolbarAtPosition(EkitCore.KEY_TOOL_SPECIAL_CHAR, OmissisBehavior.KEY_TOOL_OMISSIS);
				
				addHTMLDocumentBehavior(new OmissisBehavior());
				addHTMLDocumentBehavior(new CSSRulesBehavior("p { text-align: justify; }"));
				addHTMLDocumentBehavior(new ToggleParagraphIndentBehavior());
				addHTMLDocumentBehavior(new AutoFirstLineIndentBehavior());
			}
			
			addHTMLDocumentBehavior(new AutoCurvedQuotesBehavior());
			
			try {
				configuraSpellcheck();
			} catch (SpellcheckerInitializationException e) {
				log.error("Coretor ortográfico não disponível.", e);
			}
			
			addToToolbar("SP");
			addToToolbar("MX");
		}
		
		public void configuraSpellcheck() throws SpellcheckerInitializationException {
			
			HostEditor he = componentController.getHostEditor();
			if(he != null) {
				File baseDir = he.getSpellcheckBaseDir();
				if(baseDir != null) {
					SpellcheckBehavior sb = new SpellcheckBehavior(baseDir);
					addHTMLDocumentBehavior(sb);
					addToToolbar("SP|SC");
				}
			}
			
		}

	}

}
