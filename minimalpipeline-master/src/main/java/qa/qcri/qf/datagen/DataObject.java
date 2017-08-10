package qa.qcri.qf.datagen;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * The DataObject class is convenient for moving around data related to an
 * object which needs to be serialized for machine learning applications
 * 
 * It can contain information about labels, features, metadata, and an id used
 * also by the serialization mechanism to materialize the annotation related to
 * that object
 */
public class DataObject implements Labelled {

	protected Double label;

	protected String id;

	protected Map<String, Double> features;

	protected Map<String, String> metadata;

	public DataObject(Double label, String id, Map<String, Double> features,
			Map<String, String> metadata) {
		this.label = label;
		this.id = id;
		this.features = features;
		this.metadata = metadata;
	}

	public String getId() {
		return this.id;
	}

	@Override
	public Double getLabel() {
		return this.label;
	}

	@Override
	public boolean isPositive() {
		return this.label.compareTo(Labelled.POSITIVE_LABEL) == 0;
	}

	public Map<String, Double> getFeatures() {
		return this.features;
	}

	public Map<String, String> getMetadata() {
		return this.metadata;
	}

	/**
	 * Static method for factoring a new feature map
	 * 
	 * @return an empty feature map
	 */
	public static Map<String, Double> newFeaturesMap() {
		return new HashMap<String, Double>();
	}

	/**
	 * Static method for factoring a new metadata map
	 * 
	 * @return an empty metadata map
	 */
	public static Map<String, String> newMetadataMap() {
		return new HashMap<String, String>();
	}

}
