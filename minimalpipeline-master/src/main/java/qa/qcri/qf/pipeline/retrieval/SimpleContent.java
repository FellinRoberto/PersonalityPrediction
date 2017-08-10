package qa.qcri.qf.pipeline.retrieval;

import com.google.common.base.Objects;

/**
 * 
 * Simple content contains just the data needed for carrying out analysis on
 * text
 */
public class SimpleContent implements Analyzable {

	private String id;
	private String content;
	private String lang = "en";

	public SimpleContent(String id, String content) {
		this.id = id;
		this.content = content;
	}
	
	public SimpleContent(String id, String content, String lang) {
		this.id = id;
		this.content = content;
		this.lang = lang;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getContent() {
		return this.content;
	}

	@Override
	public String getLanguage() {
		return this.lang;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("id", this.id)
				.add("content", this.content)
				.toString();
	}

}
