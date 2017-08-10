package qa.qcri.qf.features.similarity;

import cc.mallet.types.StringKernel;
import de.tudarmstadt.ukp.similarity.algorithms.api.SimilarityException;
import de.tudarmstadt.ukp.similarity.algorithms.api.TermSimilarityMeasureBase;

/**
 * Computes a similarity metric between two strings,
 * based on counts of common subsequences of characters.
 *
 */
public class StringKernelSimilarity extends TermSimilarityMeasureBase {
	
	private StringKernel sk = null;
	
	public StringKernelSimilarity() {
		sk = new StringKernel();
	}
	
	public StringKernelSimilarity(boolean norm, double lam, int length) { 
		sk = new StringKernel(norm, lam, length);
	}
	
	/**
	 * 
	 * @param norm true if we lowercase all strings
	 * @param lam 0-1 penalty for gaps between matches
	 * @param length max length of subsequences to compare
	 * @param cache true if we should cache previous kernel computations, recommended!
	 */
	public StringKernelSimilarity(boolean norm, double lam, int length, boolean cache) {
		sk = new StringKernel(norm, lam, length, cache);
	}	

	@Override
	public double getSimilarity(String string1, String string2)
			throws SimilarityException {
		return sk.K(string1, string2);		
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
}
