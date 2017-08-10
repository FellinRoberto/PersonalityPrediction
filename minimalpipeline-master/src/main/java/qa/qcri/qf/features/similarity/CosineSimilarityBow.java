package qa.qcri.qf.features.similarity;

import java.util.Collection;

import qa.qcri.qf.features.providers.BowProvider;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.NormalizedDotProductMetric;
import de.tudarmstadt.ukp.similarity.algorithms.api.SimilarityException;
import de.tudarmstadt.ukp.similarity.algorithms.api.TextSimilarityMeasureBase;

public class CosineSimilarityBow extends TextSimilarityMeasureBase {
	
	private BowProvider bowProvider;
	
	private NormalizedDotProductMetric metric;
	
	public CosineSimilarityBow(BowProvider bowProvider, NormalizedDotProductMetric metric) {
		this.bowProvider = bowProvider;
		this.metric = metric;
	}

	@Override
	public double getSimilarity(Collection<String> strings1,
			Collection<String> strings2) throws SimilarityException {
		
		FeatureVector fv1 = this.bowProvider.getFeatureVector(strings1);
		FeatureVector fv2 = this.bowProvider.getFeatureVector(strings2);
		
		double distance = this.metric.distance(fv1, fv2);
		
		if(Double.isNaN(distance)) {
			return 0.0;
		} else {
			return 1.0 - distance;
		}
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName() + "_"
				+ this.bowProvider.getParamsString();
	}
}
