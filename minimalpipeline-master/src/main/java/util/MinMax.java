package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class keeps track of the minimum and the maximum values (Double) of a
 * set of observations. Objects are associated to these values so it is possible
 * to retrieve them after all values are observed. Optionally, all the observed
 * values and objects can be stored and retrieved ordered by values, in
 * ascending order.
 * 
 * @param <V>
 *            the types to reference in the data structure
 */
public class MinMax<V> {

	private Double minValue;

	private Double maxValue;

	private V minObject;

	private V maxObject;

	private List<Pair<Double, V>> objectList;

	private boolean keepSortedList;

	public MinMax() {
		this.keepSortedList = false;
	}

	/**
	 * Enables the storing of the observed objects
	 * 
	 * @return the MinMax object instance for chaining
	 */
	public MinMax<V> keepSortedList() {
		this.keepSortedList = true;
		this.objectList = new ArrayList<Pair<Double, V>>();
		return this;
	}

	/**
	 * 
	 * @param value
	 *            the value
	 * @param object
	 *            the object associated to value
	 */
	public void look(Double value, V object) {
		if (this.minValue == null || value.compareTo(this.minValue) < 0) {
			this.minValue = value;
			this.minObject = object;
		}

		if (this.maxValue == null || value.compareTo(this.maxValue) > 0) {
			this.maxValue = value;
			this.maxObject = object;
		}

		if (this.keepSortedList) {
			this.objectList.add(new Pair<>(value, object));
		}
	}

	/**
	 * 
	 * @return the object associated to the minimum seen value
	 */
	public V getMin() {
		return this.minObject;
	}

	/**
	 * 
	 * @return the object associated to the maximum seen value
	 */
	public V getMax() {
		return this.maxObject;
	}

	/**
	 * 
	 * @return the list of seen objects ordered by value, in ascending order. Or
	 *         an empty list if keepSortedList() was not called on the object
	 */
	public List<Pair<Double, V>> getSortedList() {
		if (this.keepSortedList) {
			Collections.sort(this.objectList,
					Collections.reverseOrder(new PairCompareOnA<Double, V>()));
			return this.objectList;
		} else {
			return new ArrayList<Pair<Double, V>>();
		}
	}
}
