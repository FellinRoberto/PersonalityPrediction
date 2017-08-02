package qa.qcri.qf.datagen.ngram;

import java.util.Set;

/**
 * Store idf values for different strings.
 *
 */
public interface IdfModel {
	
	/**
	 * Get the idf value for a string.
	 * 
	 * @param str A string 
	 * @return The idf value associated with this string
	 */
	double getIdf(String str);
	
	/**
	 * Returns the string keys for the idf values in the model. 
	 * 
	 * @return The a set of keys
	 */
	Set<String> keySet();
	
	void saveModel(String filepath);

}
