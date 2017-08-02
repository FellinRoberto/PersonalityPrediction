package qa.qcri.qf.features.similarity.adaptor;

import qa.qcri.qf.features.representation.Representation;
import util.Pair;

public interface MeasureAdaptor {

	/**
	 * Computes the similarity measure between a pair of strings
	 * 
	 * @param representations
	 *            the pair of string representing the object to compare
	 * @return the similarity measure value
	 */
	public double getSimilarity(Pair<String, String> representations);

	/**
	 * Builds and return a name for the measure, taking into account the
	 * representation name
	 * 
	 * @param representation
	 *            the representation of the compared objects
	 * @return the name of the measure
	 */
	public String getName(Representation representation);

}
