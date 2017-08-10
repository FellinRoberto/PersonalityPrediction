package qa.qcri.qf.datagen;

import java.util.Map;

/**
 * 
 * DataPair extends DataObject because it can be seen as a more complex object
 * used for generating examples for training/testing Moreover, it can have
 * features and metadata associated to the pair of wrapped DataObject
 */
public class DataPair extends DataObject implements Labelled {

	private DataObject a;

	private DataObject b;

	public DataPair(Double label, String id, Map<String, Double> features,
			Map<String, String> metadata, DataObject a, DataObject b) {
		super(label, id, features, metadata);
		this.a = a;
		this.b = b;
	}

	public DataObject getA() {
		return this.a;
	}

	public DataObject getB() {
		return this.b;
	}

	@Override
	/**
	 * Returns the label associated to the right object of the pair
	 */
	public Double getLabel() {
		return this.b.getLabel();
	}

	@Override
	public boolean isPositive() {
		return this.b.isPositive();
	}

}
