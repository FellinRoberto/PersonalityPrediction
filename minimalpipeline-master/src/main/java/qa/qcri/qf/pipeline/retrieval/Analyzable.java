package qa.qcri.qf.pipeline.retrieval;

/**
 * 
 * Each content class analyzable by the pipeline must implement this interface,
 * providing a unique id for the content and its textual representation
 */
public interface Analyzable {

	/**
	 * 
	 * @return the unique identifier of the content
	 */
	public String getId();

	/**
	 * 
	 * @return the text to analyze
	 */
	public String getContent();
	
	/**
	 * 
	 * @return the text language
	 */
	public String getLanguage();

}
