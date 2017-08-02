package qa.qcri.qf.datagen;

/**
 * 
 * Interface implemented by classes having a label This should work both for
 * regression and classification The binary labels have some predefined values
 * 1.0 positive 2.0 negative
 */
public interface Labelled {

	public Double POSITIVE_LABEL = 1.0;

	public Double NEGATIVE_LABEL = 0.0;

	public Double getLabel();

	/**
	 * 
	 * @return true if the label is positive
	 */
	public boolean isPositive();
}
