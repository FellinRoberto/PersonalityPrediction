package qa.qcri.qf.ir.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

public class LuceneDocumentBuilder {
	
	private Document doc;

	public LuceneDocumentBuilder() {
		this.doc = new Document();
	}
	
	public LuceneDocumentBuilder addStringField(String id, String content) {
		this.doc.add(new StringField(id, content, Field.Store.YES));
		return this;
	}
	
	public LuceneDocumentBuilder addIndexedTextField(String id, String content) {
		this.doc.add(new TextField(id, content, Field.Store.YES));
		return this;
	}
	
	public LuceneDocumentBuilder addIntField(String id, int value) {
		this.doc.add(new IntField(id, value, Field.Store.YES));
		return this;
	}
	
	public Document build() {
		Document document =  this.doc;
		this.doc = new Document();
		return document;
	}
	
}
