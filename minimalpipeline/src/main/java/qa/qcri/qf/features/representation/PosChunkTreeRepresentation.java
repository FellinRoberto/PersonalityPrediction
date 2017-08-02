package qa.qcri.qf.features.representation;

import org.apache.uima.jcas.JCas;

import qa.qcri.qf.trees.RichTree;
import qa.qcri.qf.trees.TreeSerializer;
import util.Pair;

public class PosChunkTreeRepresentation implements Representation {

	private String parameterList;

	public PosChunkTreeRepresentation(String parameterList) {
		this.parameterList = parameterList;
	}

	@Override
	public Pair<String, String> getRepresentation(JCas aCas, JCas bCas) {
		return new Pair<>(
			getRepresentation(aCas, this.parameterList),
			getRepresentation(bCas, this.parameterList)
		);
	}

	private String getRepresentation(JCas cas, String parameterList) {
		return new TreeSerializer().serializeTree(
				RichTree.getPosChunkTree(cas), parameterList);
	}

	@Override
	public String getName() {
		return "PosChunkTreeRepresentation";
	}
}
