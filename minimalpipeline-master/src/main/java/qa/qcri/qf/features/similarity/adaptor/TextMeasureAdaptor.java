package qa.qcri.qf.features.similarity.adaptor;

import qa.qcri.qf.features.representation.Representation;
import util.Pair;

import com.google.common.collect.Lists;

import de.tudarmstadt.ukp.similarity.algorithms.api.SimilarityException;
import de.tudarmstadt.ukp.similarity.algorithms.api.TextSimilarityMeasure;

/**
 * The TextMeasureAdaptor is a wrapper for TextSimilarityMeasures. This kind of
 * measures work on lists of strings
 */
public class TextMeasureAdaptor implements MeasureAdaptor {

	private TextSimilarityMeasure measure;

	public TextMeasureAdaptor(TextSimilarityMeasure measure) {
		this.measure = measure;
	}

	@Override
	public double getSimilarity(Pair<String, String> representations) {
		double featureValue;
		try {
			featureValue = this.measure.getSimilarity(
					Lists.newArrayList(representations.getA().split(" ")),
					Lists.newArrayList(representations.getB().split(" ")));
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
