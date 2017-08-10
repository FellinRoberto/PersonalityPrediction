package qa.qcri.qf.trees.pruning.strategies;

import java.util.ArrayList;
import java.util.List;

import qa.qcri.qf.trees.nodes.RichNode;

import com.google.common.base.Function;

public class PruneIfParentIsWithoutLabel implements Function<List<RichNode>, List<Boolean>> {
	
	private String label;
	
	public PruneIfParentIsWithoutLabel(String label) {
		this.label = label;
	}

	@Override
	public List<Boolean> apply(List<RichNode> nodes) {
		List<Boolean> nodesToPruneIndexes = new ArrayList<>();
		for(RichNode node : nodes) {
			if(node.getParent().getAdditionalLabels().contains(this.label)) {
				nodesToPruneIndexes.add(false);
			} else {
				nodesToPruneIndexes.add(true);
			}
		}
		return nodesToPruneIndexes;
	}

}
