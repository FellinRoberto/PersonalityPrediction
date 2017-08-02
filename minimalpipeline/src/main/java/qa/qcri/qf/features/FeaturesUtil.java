package qa.qcri.qf.features;

import cc.mallet.types.FeatureVector;

public class FeaturesUtil {

	/**
	 * Serializes a feature vector in the widespread index:value format
	 * 
	 * @param fv
	 *            the feature vector
	 * @return the serialized feature vector
	 */
	public static String serialize(FeatureVector fv) {
		StringBuffer sb = new StringBuffer(1024);
		int numLocations = fv.numLocations();
		int[] indices = fv.getIndices();
		for (int index = 0; index < numLocations; index++) {
			int featureIndex = indices[index];
			double value = fv.value(featureIndex);
			if (Double.compare(value, 0.0) != 0) {
				sb.append(featureIndex + 1); // Serialized features start from 1
				sb.append(":");
				sb.append(value);
				sb.append(" ");
			}
		}
		return sb.toString().trim();
	}
}
