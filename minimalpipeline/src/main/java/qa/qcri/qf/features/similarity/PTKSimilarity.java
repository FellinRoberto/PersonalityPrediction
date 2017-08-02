package qa.qcri.qf.features.similarity;

import it.unitn.kernels.ptk.PTKernel;
import de.tudarmstadt.ukp.similarity.algorithms.api.SimilarityException;
import de.tudarmstadt.ukp.similarity.algorithms.api.TermSimilarityMeasureBase;

/**
 * Computes a similarity metric between two trees (represented as strings),
 * based on counts of common substructures.
 * 
 */
public class PTKSimilarity extends TermSimilarityMeasureBase {

	public static final double DEFAULT_SIMILARITY = 0.3;

	private static PTKernel ptk = null;

	public PTKSimilarity() {
		ptk = new PTKernel();
	}

	public PTKSimilarity(double arg0, double arg1, double arg2, double arg3) {
		ptk = new PTKernel(arg0, arg1, arg2, arg3);
	}

	/**
	 * The PTK library sometimes crashes. Maybe the problem is the size of the
	 * trees. Thus, the exception is captured and a default similarity value is
	 * returned.
	 */
	@Override
	public double getSimilarity(String tree1, String tree2)
			throws SimilarityException {
		try {
			return ptk.evaluateKernel(tree1, tree2);
		} catch (ArrayIndexOutOfBoundsException e) {
			return DEFAULT_SIMILARITY;
		}
	}
	
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

}
