package qa.qcri.qf.datagen.rr;

import java.util.List;

import qa.qcri.qf.datagen.DataObject;

public interface Reranking {

	/**
	 * Generates the data related to the question (or generically and
	 * hypothesis) and its associated candidates (other hypothesis)
	 * 
	 * @param questionObject
	 * @param candidateObjects
	 */
	public void generateData(DataObject questionObject,
			List<DataObject> candidateObjects);
}
