package qa.qcri.qf.pipeline.serialization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.input.ReaderInputStream;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.XMLSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;


public class UIMAFilePersistenceWithEncoding implements UIMAPersistence  {
	
	private String casDir;
	
	private String encoding;
	
	private final Logger logger = LoggerFactory
			.getLogger(UIMAFilePersistenceWithEncoding.class);

	public UIMAFilePersistenceWithEncoding(String path, String encoding) {
		if (path == null)
			throw new NullPointerException("path is null");
		if (encoding == null)
			throw new NullPointerException("encoding is null");
		
		this.casDir = path;
		this.encoding = encoding;
		logger.info("Using the CAS dir at: {}", path);
		try {
			Files.createDirectories(Paths.get(path));
		} catch (IOException e) {
			logger.error("Failed to create path: {}", path);
			e.printStackTrace();
		}
	}
	
	@Override
	public void serialize(JCas cas, String id) {
		try (OutputStreamWriter out = new OutputStreamWriter(
				new FileOutputStream(Paths.get(this.casDir, id).toFile()), this.encoding)) {
		//try (FileWriterWithEncoding out = new FileWriterWithEncoding(Paths.get(this.casDir, id).toFile(), this.encoding)) {
			XmiCasSerializer ser = new XmiCasSerializer(cas.getTypeSystem());
			XMLSerializer xmlSer = new XMLSerializer(out, false);
			logger.info("Serializing cas for the document: {}", id);
			ser.serialize(cas.getCas(), xmlSer.getContentHandler());
		} catch (SAXException | IOException e) {
			logger.error("Failed to serialize cas for the document: {}", id);
			e.printStackTrace();
		}
	}

	@Override
	public void deserialize(JCas cas, String id) {
		
		//new FileInputStream(Paths.get(casDir, id).toFile()), this.encoding)) {
		try (ReaderInputStream in =
				new ReaderInputStream(
					new InputStreamReader(
							new FileInputStream(Paths.get(casDir, id).toFile()), this.encoding))) {
			logger.info("Deserializing cas for the document: {}", id);
			XmiCasDeserializer.deserialize(in, cas.getCas());
		} catch (IOException | SAXException e) {
			logger.error("Failed to deserialize cas for the document: {}", id);
			e.printStackTrace();
		}
	}

	@Override
	public boolean isAlreadySerialized(String casXMLPath) {
		return new File(casDir, casXMLPath).exists();
	}
}
