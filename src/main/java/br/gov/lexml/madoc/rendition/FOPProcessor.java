package br.gov.lexml.madoc.rendition;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fop.apps.FOURIResolver;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.pdf.PDFAMode;
import org.apache.fop.render.pdf.PDFConfigurationConstants;
import org.dom4j.io.DocumentSource;
import org.joda.time.format.ISODateTimeFormat;
import org.xml.sax.SAXException;

import br.gov.lexml.madoc.catalog.CatalogService;
import br.gov.lexml.madoc.schema.Constants;
import br.gov.lexml.madoc.schema.entity.MadocAnswerType;
import br.gov.lexml.madoc.util.FOHelper;
import br.gov.lexml.madoc.util.XMLUtil;
import br.gov.lexml.pdfa.PDFA;
import br.gov.lexml.pdfa.PDFAttachmentFile;

public class FOPProcessor {

	private static final Log log = LogFactory.getLog(FOPProcessor.class);
	
	// configure fopFactory as desired
	private static final FopFactory fopFactory = FopFactory.newInstance();
	
	private boolean attachMadocAnswer = true;
	
	private boolean attachRtf = true;
	
	private List<PDFAttachmentFile> attachments = new ArrayList<PDFAttachmentFile>();
	
	static class ClasspathUriResolver implements URIResolver {
		private final CatalogService catalogService;
		ClasspathUriResolver(CatalogService catalogService){
			this.catalogService = catalogService;
		}
		
		public Source resolve(String href, String base)
				throws TransformerException {
			Source source = null;
			InputStream inputStream = FOPProcessor.class
					.getResourceAsStream("/pdfa-fonts/" + href);
			if (inputStream != null) {
				log.info("Font href=" + href + " found.");
				source = new StreamSource(inputStream);
			} else {
				log.error("Font href=" + href + " not found.");
				return catalogService.getURIResolver().resolve(href, base);
			}
			return source;
		}
	}

	public FOPProcessor(CatalogService catalogService) {
		
		// font resolver
		FOURIResolver uriResolver = (FOURIResolver) fopFactory.getURIResolver();
		uriResolver.setCustomURIResolver(new ClasspathUriResolver(catalogService));

		try {
			URL url = FOPProcessor.class.getResource("/fop.xconf");
			fopFactory.setUserConfig(url.toString());
		} catch (SAXException e) {
			log.error("static FOPProcessor SAXException when loading fop.xconf: "+ e.getMessage(), e);
		} catch (IOException e) {
			log.error("static FOPProcessor: IOException when loading fop.xconf: "+ e.getMessage(), e);
		}

		// fopFactory.getElementMappingRegistry().addElementMapping("ru.hobbut.fop.zxing.qrcode.QRCodeElementMapping");
		// fopFactory.getXMLHandlerRegistry().addXMLHandler("ru.hobbut.fop.zxing.qrcode.QRCodeXMLHandler");

	}

	/**
	 * Process FOP
	 * 
	 * @see http://xmlgraphics.apache.org/fop/quickstartguide.html
	 * @see http
	 *      ://svn.apache.org/viewvc/xmlgraphics/fop/trunk/examples/embedding
	 *      /java/embedding/ExampleFO2PDF.java?view=markup
	 * @param file
	 * @param mimeConstantsMime
	 * @param templateResult
	 */
	@SuppressWarnings("unchecked")
	public void processFOP(OutputStream outputStream, String mimeConstantsMime, String templateResult, MadocAnswerType madocAnswer) {

		try {
			// Setup output stream. Note: Using BufferedOutputStream
			// for performance reasons (helpful with FileOutputStreams).
			OutputStream out = null;
			try {

				// configure foUserAgent as desired
				FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

				// helper
				FOHelper helper = new FOHelper(templateResult);
				
				//fonte de dados
				Source src = new DocumentSource(helper.getFOPDocumentWithoutXmpmeta());

				if (!mimeConstantsMime.equals(MimeConstants.MIME_PDF) || (helper.getXmpmeta() == null)) { // if it isn't PDF
					out = new BufferedOutputStream(outputStream);
					
				} else { 
					out = new ByteArrayOutputStream();

					// PDF-A additional information
					if (helper.isPDFAMode()) {
						foUserAgent.getRendererOptions().put(
								PDFConfigurationConstants.PDF_A_MODE,
								helper.getPDFAModeNameFOP());
						
						if (helper.getPDFAModeNameFOP().equals(PDFAMode.PDFA_1A.getName())) {
							foUserAgent.setAccessibility(true);
						}

						// set createDate
						String cmpCreateDate = helper.getCmpCreateDate();
						if (cmpCreateDate == null) {
							log.info("cmpCreateDate is null");
						} else {
							 Date date = ISODateTimeFormat.dateTime().parseDateTime(cmpCreateDate).toDate();
							 foUserAgent.setCreationDate(date);
						}
					}
				}
				

				// Construct fop with desired output format
				Fop fop = fopFactory.newFop(mimeConstantsMime, foUserAgent, out);

				// Resulting SAX events (the generated FO) must be piped
				// through to FOP
				Result res = new SAXResult(fop.getDefaultHandler());

				// Start XSLT transformation and FOP processing
				// Setup JAXP using identity transformer
				Transformer transformer = TransformerFactory.newInstance().newTransformer(); // identity transformer
				transformer.transform(src, res);
				//IOUtils.closeQuietly(out);

				// putting XMPmeta, if it is a PDF
				if (out instanceof ByteArrayOutputStream) {
					byte[] data = ((ByteArrayOutputStream) out).toByteArray();
					
					//PDF/A:
					PDFA pdfa = PDFA.getNewInstance(outputStream, new ByteArrayInputStream(data), helper.getPDFAPart(), helper.getPDFAConformance());
					if (pdfa == null){
						log.error("Could not find a PDF/A part "+helper.getPDFAPart()+", conformance "+helper.getPDFAConformance()+" constructor on PDFA class.");
					} else {
						pdfa.addXMP(helper.getXmpmeta().getBytes());
						
						//adding madoc-fo
						pdfa.addAttachments(
								new PDFAttachmentFile(
									templateResult.getBytes(), 
									"fo.xml",
									"text/xml", 
									helper.getCmpCreateDate(), 
									PDFAttachmentFile.AFRelationShip.SOURCE));
						
						//adding madoc-answer
						if(attachMadocAnswer) {
							pdfa.addAttachments(
									new PDFAttachmentFile(
											XMLUtil.convertObjectToXMLString(MadocAnswerType.class, madocAnswer, Constants.MADOC_ANSWER_ROOT_ELEMENT).getBytes(), 
											"madoc-answer.xml",
											"text/xml", 
											helper.getCmpCreateDate(), 
											PDFAttachmentFile.AFRelationShip.DATA));
						}
						
						// Extra attachments
						for(PDFAttachmentFile attachment: attachments) {
							pdfa.addAttachment(attachment);
						}
						
						//adding RTF representation
						if(attachRtf) {
							byte[] rtfOutput = generateRTF(templateResult);
							if (rtfOutput != null){
								pdfa.addAttachments(
									new PDFAttachmentFile(
										rtfOutput, 
										"documento.rtf",
										MimeConstants.MIME_RTF, 
										helper.getCmpCreateDate(), 
										PDFAttachmentFile.AFRelationShip.ALTERNATIVE));
							}
						}

						//setting version
						pdfa.setVersion(PDFA.PDFVersion.PDF_VERSION_1_7);
						pdfa.close();
					}
				}
				
			} catch (Exception e) {
				throw new RuntimeException("Error processing FOP. " + e.getMessage(), e);
			} finally {
				if (out != null) {
					out.close();
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("I/O error when processing FOP. " + e, e);
		}
	}
	
	private byte[] generateRTF(String templateResult) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		processFOP(out, MimeConstants.MIME_RTF, templateResult, null);
		
		return out.toByteArray();
	}

	public void addAttachment(PDFAttachmentFile file) {
		attachments.add(file);
	}
	
	public void setAttachMadocAnswer(boolean attachMadocAnswer) {
		this.attachMadocAnswer = attachMadocAnswer;
	}
	
	public boolean isAttachMadocAnswer() {
		return attachMadocAnswer;
	}

	public void setAttachRtf(boolean attachRtf) {
		this.attachRtf = attachRtf;
	}
	
	public boolean isAttachRtf() {
		return attachRtf;
	}
	
}
