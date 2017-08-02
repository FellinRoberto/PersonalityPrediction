package qa.qcri.qf.features.similarity.adaptor;

import qa.qcri.qf.features.representation.Representation;
import util.Pair;
import de.tudarmstadt.ukp.similarity.algorithms.api.SimilarityException;
import de.tudarmstadt.ukp.similarity.algorithms.api.TermSimilarityMeasure;

/**
 * The TermMeasureAdaptor is a wrapper for TermSimilarityMeasures. This kind of
 * measures work on strings
 */
public class TermMeasureAdaptor implements MeasureAdaptor {
	
	private TermSimilarityMeasure measure;
	
	public TermMeasureAdaptor(TermSimilarityMeasure measure) {
		this.measure = measure;
	}

	@Override
	public double getSimilarity(Pair<String, String> representations) {
		double featureValue;
		try {
			featureValue = this.measure.getSimilarity(
					representations.getA(),
					representations.getB());
		} catch (SimilarityException e) {
			featureValue = 0.0;
		}
		return featureValue;
	}
	
	@Override
	public String getName(Representation representation) {
		return this.measure.getName() + "_" + representation.getName();
	}

}
