package qa.qcri.qf.datagen;

import java.util.ArrayList;
import java.util.List;

import util.Pair;

/**
 * 
 * Produce pairs of object in order to form more complex data structures
 */
public class Pairer {

	/**
	 * Takes a list of DataPair (pair of DataObjects) and pairs the relevant
	 * with the irrelevant ones. First it determines the relevant and irrelevant
	 * pairs inspecting their labels. Then, it performs a Cartesian Product
	 * between the two lists, and produce pairs, swapping the position of the
	 * relevant and irrelevant object at each iteration
	 * 
	 * @param dataPairs
	 * @return a list of pairs of DataPairs
	 */
	public static List<Pair<DataPair, DataPair>> pair(List<DataPair> dataPairs) {
		List<DataPair> relevantPairs = new ArrayList<>();
		List<DataPair> irrelevantPairs = new ArrayList<>();

		for (DataPair dataPair : dataPairs) {
			if (dataPair.isPositive()) {
				relevantPairs.add(dataPair);
			} else {
				irrelevantPairs.add(dataPair);
			}
		}

		List<Pair<DataPair, DataPair>> pairs = new ArrayList<>();
		boolean flip = false;

		for (DataPair relevantPair : relevantPairs) {
			for (DataPair irrelevantPair : irrelevantPairs) {
				if (flip) {
					pairs.add(new Pair<DataPair, DataPair>(irrelevantPair,
							relevantPair));
				} else {
					pairs.add(new Pair<DataPair, DataPair>(relevantPair,
							irrelevantPair));
				}

				flip = !flip;
			}
		}

		return pairs;
	}
}
