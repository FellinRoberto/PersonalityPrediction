package util;

import java.util.Comparator;

public class PairCompareOnA<K, V> implements Comparator<Pair<K, V>> {

	@SuppressWarnings("unchecked")
	@Override
	public int compare(Pair<K, V> o1, Pair<K, V> o2) {
		
		Comparable<K> v1 = (Comparable<K>) o1.getA();
		Comparable<K> v2 = (Comparable<K>) o2.getA();
		
		return v2.compareTo((K) v1);
	}
}