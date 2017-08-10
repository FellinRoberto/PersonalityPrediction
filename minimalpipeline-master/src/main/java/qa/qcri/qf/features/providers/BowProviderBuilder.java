package qa.qcri.qf.features.providers;

import util.Stopwords;
import cc.mallet.types.Alphabet;

/**
 * Builder class for BowProvider.
 *
 */
public class BowProviderBuilder {
	
	private int minN = BowProvider.MIN_N;
	
	private int maxN = BowProvider.MAX_N;
		
	private Alphabet alphabet = new Alphabet();
	
	private Stopwords stopwords;
	
	private String tokenFrmtParamList = BowProvider.TOKEN_FORMAT_PARAM_LIST;
	
	public BowProviderBuilder() {
		this.stopwords = new Stopwords();
	}
	
	/**
	 * Set the alphabet to use.
	 *  
	 * @param alphabet an Alphabet object
	 * @return This builder instance
	 */
	public BowProviderBuilder setAlphabet(Alphabet alphabet) {
		this.alphabet = alphabet;
		return this;
	}
	
	/**
	 * Set the n-grams smallest size.
	 * 
	 * @param minN An integer specifying the min n-grams size
	 * @return This builder instance
	 */
	public BowProviderBuilder setMinN(int minN) {
		this.minN = minN;
		return this;
	}
	
	/**
	 * Set the n-grams largest size.
	 * 
	 * @param maxN An integer specifying the max n-grams size
	 * @return This builder instance
	 */
	public BowProviderBuilder setMaxN(int maxN) {
		this.maxN = maxN;
		return this;
	}
	
	/**
	 * Set the tokens formatting rules.
	 * 
	 * @param tokenFrmtParamList A string of comma separated formatting rule (e.g. "TOKEN_LOWER,LEMMA")
	 * @return This Builder instance
	 */
	public BowProviderBuilder setTokenFrmtParamList(String tokenFrmtParamList) {
		this.tokenFrmtParamList = tokenFrmtParamList;
		return this;
	}
	
	/**
	 * Set the filterStopwords boolean flag.
	 * 
	 * @param filterStopwords A boolean indicating whether stopwords removal should be performed
	 * @return This Builder instance
	 */
	public BowProviderBuilder setStopwords(Stopwords stopwords) {
		this.stopwords = stopwords;
		return this;
	}

	
	/**
	 * Creates a new BowProvider instance.
	 * 
	 * @return The newly create BowProvider object
	 */
	public BowProvider build() {
		return
				new BowProvider(
						alphabet,
						tokenFrmtParamList,
						minN, maxN,
						stopwords);
	}

}
