package qa.qcri.qf.features;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;

import qa.qcri.qf.features.providers.BowProvider;
import qa.qcri.qf.features.representation.PosChunkTreeRepresentation;
import qa.qcri.qf.features.representation.Representation;
import qa.qcri.qf.features.representation.TokenRepresentation;
import qa.qcri.qf.features.similarity.CosineSimilarityBow;
import qa.qcri.qf.features.similarity.adaptor.MeasureAdaptor;
import qa.qcri.qf.features.similarity.adaptor.TermMeasureAdaptor;
import qa.qcri.qf.features.similarity.adaptor.TextMeasureAdaptor;
import qa.qcri.qf.trees.nodes.RichNode;
import util.Pair;
import util.Stopwords;
import cc.mallet.types.Alphabet;
import cc.mallet.types.AugmentableFeatureVector;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.NormalizedDotProductMetric;
import de.tudarmstadt.ukp.similarity.algorithms.api.TermSimilarityMeasure;
import de.tudarmstadt.ukp.similarity.algorithms.api.TextSimilarityMeasure;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.ngrams.WordNGramContainmentMeasure;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.ngrams.WordNGramJaccardMeasure;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.CosineSimilarity;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.GreedyStringTiling;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.JaroWinklerSecondStringComparator;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.LongestCommonSubsequenceComparator;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.LongestCommonSubsequenceNormComparator;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.LongestCommonSubstringComparator;
import de.tudarmstadt.ukp.similarity.algorithms.lexical.string.MongeElkanSecondStringComparator;

/**
 * 
 * This class provides the features to compute on pair of objects
 */
public class PairFeatureFactory {

	/**
	 * Mallet Alphabet is used to index features
	 */
	private Alphabet alphabet;
	
	/**
	 * Path of the file containing the idf values to use in similarity metrics
	 */
	private String idfValuesPath;

	/**
	 * Some DKPro features works only if provided with list of tokens. Thus, it
	 * is necessary to distinguish them and provide them with the right input.
	 * The MeasureAdaptor wraps different measures and hides the underlying
	 * implementation
	 */
	private List<Pair<MeasureAdaptor, Representation>> measures;

	/**
	 * Instantiates the feature factory with a feature index that should be
	 * shared among the modules working on the same datasets
	 * 
	 * @param alphabet
	 *            the Mallet alphabet object.
	 */
	public PairFeatureFactory(Alphabet alphabet) {
		this.alphabet = alphabet;
		this.measures = new ArrayList<>();
	}
	
	/**
	 * Sets the path of the file containing the idf values
	 * @param idfValuesPath
	 */
	public void setIdfValues(String idfValuesPath) {
		this.idfValuesPath = idfValuesPath;
	}
	
	public void setupMeasures(String parameterList) {
		this.setupMeasures(parameterList, new Stopwords());
	}

	public void setupMeasures(String parameterList, Stopwords stopwords) {
		this.measures.clear();

		/**
		 * Prepares the token and tree representations we want to use for
		 * computing the features (e.g. we would like to compute cosine
		 * similarity between stems and lemmas
		 */
		Representation tokens = new TokenRepresentation(parameterList, new Stopwords());
		Representation tokensNoStopwords = new TokenRepresentation(parameterList, stopwords);
		Representation trees = new PosChunkTreeRepresentation(parameterList);
		
		Representation postags = new TokenRepresentation(RichNode.OUTPUT_PAR_POSTAG, stopwords);
		
		/**
		 * BOW Features
		 */
		
		NormalizedDotProductMetric metric = new NormalizedDotProductMetric();
		
		BowProvider bow;
		
		int[][] lemmaIntervals = new int[][]{
				new int[]{1, 1},
				new int[]{1, 2},
				new int[]{1, 3},
				new int[]{2, 3},
				new int[]{2, 4},
				new int[]{3, 4},
				};
		
		int[][] posIntervals = new int[][]{
				new int[]{1, 3},
				new int[]{1, 4},
				new int[]{2, 4},
				};
		
		for(int[] interval : lemmaIntervals) {
			int from = interval[0];
			int to = interval[1];
			
			bow = new BowProvider(this.alphabet, parameterList, from, to,
					new Stopwords());
			this.addTextMeasure(new CosineSimilarityBow(bow, metric), tokens);
			
			bow = new BowProvider(this.alphabet, parameterList, from, to, stopwords);
			this.addTextMeasure(new CosineSimilarityBow(bow, metric), tokens);
		}
		
		for(int[] interval : posIntervals) {
			int from = interval[0];
			int to = interval[1];
			
			bow = new BowProvider(this.alphabet, RichNode.OUTPUT_PAR_POSTAG, from, to, new Stopwords());
			this.addTextMeasure(new CosineSimilarityBow(bow, metric), postags);
		}

		/**
		 * DKPro 2012 STS best system features
		 * 
		 * String features
		 */
		this.addTermMeasure(new GreedyStringTiling(3), tokens);
		this.addTermMeasure(new LongestCommonSubsequenceComparator(), tokens);
		this.addTermMeasure(new LongestCommonSubsequenceNormComparator(), tokens);
		this.addTermMeasure(new LongestCommonSubstringComparator(), tokens);
		
		this.addTermMeasure(new GreedyStringTiling(3), tokensNoStopwords);
		this.addTermMeasure(new LongestCommonSubsequenceComparator(), tokensNoStopwords);
		this.addTermMeasure(new LongestCommonSubsequenceNormComparator(), tokensNoStopwords);
		this.addTermMeasure(new LongestCommonSubstringComparator(), tokensNoStopwords);
		
		/**
		 * n-grams
		 */
		this.addTextMeasure(new WordNGramJaccardMeasure(1), tokens);
		this.addTextMeasure(new WordNGramJaccardMeasure(2), tokens);
		this.addTextMeasure(new WordNGramJaccardMeasure(3), tokens);
		this.addTextMeasure(new WordNGramJaccardMeasure(4), tokens);
		this.addTextMeasure(new WordNGramContainmentMeasure(1), tokens);
		this.addTextMeasure(new WordNGramContainmentMeasure(2), tokens);
		
		this.addTextMeasure(new WordNGramJaccardMeasure(1), tokensNoStopwords);
		this.addTextMeasure(new WordNGramJaccardMeasure(2), tokensNoStopwords);
		this.addTextMeasure(new WordNGramJaccardMeasure(3), tokensNoStopwords);
		this.addTextMeasure(new WordNGramJaccardMeasure(4), tokensNoStopwords);
		this.addTextMeasure(new WordNGramContainmentMeasure(1), tokensNoStopwords);
		this.addTextMeasure(new WordNGramContainmentMeasure(2), tokensNoStopwords);

		/**
		if(this.idfValuesPath != null) {
			try {
				 this.addTextMeasure(new CharacterNGramMeasure(2, this.idfValuesPath), tokens);
				 this.addTextMeasure(new CharacterNGramMeasure(3, this.idfValuesPath), tokens);
				 this.addTextMeasure(new CharacterNGramMeasure(4, this.idfValuesPath), tokens);
			} catch (IOException e) {
				e.printStackTrace();
		}
		*/
		
		this.addTextMeasure(new MongeElkanSecondStringComparator(), tokens);
		this.addTextMeasure(new JaroWinklerSecondStringComparator(), tokens);
		
		this.addTextMeasure(new MongeElkanSecondStringComparator(), tokensNoStopwords);
		this.addTextMeasure(new JaroWinklerSecondStringComparator(), tokensNoStopwords);
		 
		/**
		 * ESA_Wiktionary ESA_WordNet
		 */

		/**
		 * Additional DKPro features
		 */
		this.addTermMeasure(new CosineSimilarity(), tokens);
		this.addTermMeasure(new CosineSimilarity(), tokensNoStopwords);

		/**
		 * iKernels features
		 */
		//this.addTermMeasure(new PTKSimilarity(), trees);
	}

	public FeatureVector getPairFeatures(JCas aCas, JCas bCas,
			String parameterList, Stopwords stopwords) {
		
		this.setupMeasures(parameterList, stopwords);

		AugmentableFeatureVector fv = new AugmentableFeatureVector(
				this.alphabet);
		
		try {

			for (Pair<MeasureAdaptor, Representation> measureAndRepresentation : this.measures) {
				MeasureAdaptor measure = measureAndRepresentation.getA();
				Representation representation = measureAndRepresentation.getB();
				Pair<String, String> representations = representation
						.getRepresentation(aCas, bCas);
	
				String featureName = measure.getName(representation);
				double featureValue = measure.getSimilarity(representations);
	
				fv.add(featureName, featureValue);
			}
		} catch (StringIndexOutOfBoundsException ex) {
			
		}

		return fv;
	}

	public void addTextMeasure(TextSimilarityMeasure measure,
			Representation representation) {
		this.measures.add(new Pair<MeasureAdaptor, Representation>(
				new TextMeasureAdaptor(measure), representation));
	}

	public void addTermMeasure(TermSimilarityMeasure measure,
			Representation representation) {
		this.measures.add(new Pair<MeasureAdaptor, Representation>(
				new TermMeasureAdaptor(measure), representation));
	}

}
