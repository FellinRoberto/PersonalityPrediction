package qa.qcri.qf.trees.pruning.strategies;

import java.util.ArrayList;
import java.util.List;

import qa.qcri.qf.trees.nodes.RichNode;

import com.google.common.base.Function;

public class PruneIfParentIsNotStartingWith implements Function<List<RichNode>, List<Boolean>> {
	
	private String prefix;
	
	public PruneIfParentIsNotStartingWith(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public List<Boolean> apply(List<RichNode> nodes) {
		List<Boolean> nodesToPruneIndexes = new ArrayList<>();
		for(RichNode node : nodes) {
			if(node.getParent().getValue().startsWith(this.prefix)) {
				nodesToPruneIndexes.add(false);
			} else {
				nodesToPruneIndexes.add(true);
			}
		}
		return nodesToPruneIndexes;
	}

}
