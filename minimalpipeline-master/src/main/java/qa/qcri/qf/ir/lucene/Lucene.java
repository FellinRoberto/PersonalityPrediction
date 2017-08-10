package qa.qcri.qf.ir.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.google.common.io.Files;

public class Lucene {
	
	private IndexWriter writer;
	
	private Analyzer analyzer;
	
	public Lucene() {
		this(new StandardAnalyzer(Version.LUCENE_47));
	}
	
	public Lucene(Analyzer analyzer) {
		this.analyzer = analyzer;
	}
	
	
	public void createIndex(String indexPath) throws IOException {
		Files.createParentDirs(new File(indexPath));		
		this.writer = getWriter(indexPath);
	}
	
	public void addDocumentToIndex(Document doc) throws IOException {
		this.writer.addDocument(doc);
	}
	
	public void closeIndex() throws IOException {
		this.writer.close();
	}

	private IndexWriter getWriter(String indexPath) throws IOException {
		return new IndexWriter(getFSDirectory(indexPath), getIndexWriterConfig());
	}

	private IndexWriterConfig getIndexWriterConfig() {
		return new IndexWriterConfig(Version.LUCENE_47, this.analyzer);
	}

	private FSDirectory getFSDirectory(String indexPath) throws IOException {
		return FSDirectory.open(new File(indexPath));
	}
}
