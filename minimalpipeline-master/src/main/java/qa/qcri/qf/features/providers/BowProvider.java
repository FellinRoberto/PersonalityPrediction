package qa.qcri.qf.features.providers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qa.qcri.qf.trees.nodes.RichNode;
import qa.qcri.qf.trees.nodes.RichTokenNode;
import util.Stopwords;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.FeatureVector;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.ngrams.util.NGramStringIterable;

/**
 * Generates n-grams FeatureVectors or FeatureSequences.
 * 
 */
public class BowProvider {
	
	final static int MIN_N = 1;
	
	final static int MAX_N = 1;
	
	final static String TOKEN_FORMAT_PARAM_LIST = RichNode.OUTPUT_PAR_TOKEN;
	
	private static final Logger logger = LoggerFactory.getLogger(BowProvider.class);
	
	private final int minN;
	
	private final int maxN;
	
	private Alphabet alphabet;
	
	private final String tokenFrmtParamList;

	private Stopwords stopwords;
	
	/**
	 * Construct a new BowPrivder instance
	 * 
	 * @param alphabet The underlying alphabet to use for features indexes
	 * @param tokensFrmtParamList A string holding the tokens formatting rules
	 * @param minN The smallest ngrams size to keep track
	 * @param maxN The largest ngrams size to keep track
	 * @param stopwords The stopword object
	 */
	public BowProvider(Alphabet alphabet, String tokensFrmtParamList,
			int minN, int maxN, Stopwords stopwords) {
		
		if (alphabet == null) {
			throw new NullPointerException("alphabet is null");
		}
		if (tokensFrmtParamList == null) {
			throw new NullPointerException("parametersList is null");
		}
		if (minN > maxN) {
			throw new IllegalArgumentException("minN > maxN");
		}
		if (stopwords == null) {
			throw new NullPointerException("stopwords is null");
		}
		
		this.minN = minN;
		this.maxN = maxN;
		this.alphabet = alphabet;
		this.tokenFrmtParamList = tokensFrmtParamList;
		this.stopwords = stopwords;
	}
	
	/**
	 * Returns a vector of n-gram features from a cas.
	 *  
	 * @param cas A uima cas object
	 * @return The n-grams FeatureVector object
	 */
	public FeatureVector getFeatureVector(JCas cas) {
		if (cas == null) {
			throw new NullPointerException("cas is null");
		}
		
		return	new FeatureVector(getNGramFeatureSeqFromCas(cas));
	}
	
	/**
	 * Returns a vector of n-gram features from a cas.
	 *  
	 * @param cas A uima cas object
	 * @return The n-grams FeatureVector object
	 */
	public FeatureVector getFeatureVector(Collection<String> strings) {
		return	new FeatureVector(getNGramFeatureSeq(strings));
	}
	
	private List<String> getNGrams(List<String> tokens) { 
		assert tokens != null;
		
		List<String> ngrams = new ArrayList<>();
		for (String ngram : new NGramStringIterable(tokens.toArray(new String[0]), minN, maxN)) {
			ngrams.add(ngram);
		}
		return ngrams;		
	}
	
	public List<String> getNGramsFromCas(JCas cas) {
		if (cas == null) {
			throw new NullPointerException("cas is null");
		}
		
		List<Token> tokens = new ArrayList<>();
		for (Token token : JCasUtil.select(cas, Token.class)) {
			tokens.add(token);			
		}
		List<String> filteredFrmtTokens = filterAndFrmtTokens(tokens);
		return getNGrams(filteredFrmtTokens);
	}
	
	private List<String> filterAndFrmtTokens(List<Token> tokens) {
		assert tokens != null;
				
		List<String> filteredFrmtTokens = new ArrayList<>();
		for (Token token : tokens) {
			String text = new RichTokenNode(token).getRepresentation(tokenFrmtParamList);
			
			if (text != null && (!stopwords.contains(text))) {
				filteredFrmtTokens.add(text);
			}
		}
		return filteredFrmtTokens;
	}
	
	private FeatureSequence getNGramFeatureSeq(List<String> ngrams) {
		assert ngrams != null;
		
		FeatureSequence featureSeq = new FeatureSequence(alphabet);
		for (String ngram : ngrams) { 
			featureSeq.add(ngram);
		}
		return featureSeq;
	}
	
	/**
	 * Construct a a new n-grams feature sequence from a cas.
	 * 
	 * @param cas A uima cas object
	 * @return The n-grams FeatureSequence object
	 */
	public FeatureSequence getNGramFeatureSeqFromCas(JCas cas) {
		if (cas == null) {
			throw new NullPointerException("cas is null");
		}		
		// Create a new sequence of features
		List<String> ngrams = getNGramsFromCas(cas);
		return getNGramFeatureSeq(ngrams);
	}
	
	public FeatureSequence getNGramFeatureSeq(Collection<String> strings) {
		List<String> ngrams = getNGrams(strings);
		return getNGramFeatureSeq(ngrams);
	}

	private List<String> getNGrams(Collection<String> strings) {
		assert strings != null;
		
		List<String> ngrams = new ArrayList<>();
		for (String ngram : new NGramStringIterable(strings.toArray(new String[0]), minN, maxN)) {
			ngrams.add(ngram);
		}
		return ngrams;		
	}
	
	public String getParamsString() {
		return this.tokenFrmtParamList + "_"
				+ this.minN + "_"
				+ this.maxN + "_"
				+ !this.stopwords.isEmpty();
	}

}
