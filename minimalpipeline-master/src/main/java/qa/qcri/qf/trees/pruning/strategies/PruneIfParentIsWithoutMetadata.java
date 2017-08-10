package qa.qcri.qf.trees.pruning.strategies;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import qa.qcri.qf.trees.nodes.RichNode;

import com.google.common.base.Function;

public class PruneIfParentIsWithoutMetadata implements Function<List<RichNode>, List<Boolean>> {
	
	private String key;
	
	private String value;

	public PruneIfParentIsWithoutMetadata(String key) {
		this(key, null);
	}
	
	public PruneIfParentIsWithoutMetadata(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	@Override
	public List<Boolean> apply(List<RichNode> nodes) {
		List<Boolean> nodesToPruneIndexes = new ArrayList<>();
		for(RichNode node : nodes) {
			RichNode parent = node.getParent();		
			if(parent == null) {
				continue;
			}
			
			Map<String, String> metadata = parent.getMetadata();
			if(metadata.containsKey(this.key)) {
				if(this.value == null) {
					nodesToPruneIndexes.add(false);
				} else {
					if(metadata.get(this.key).equals(this.value)) {
						nodesToPruneIndexes.add(false);
					} else {
						nodesToPruneIndexes.add(true);
					}
				}
			} else {
				nodesToPruneIndexes.add(true);
			}
		}
		return nodesToPruneIndexes;
	}

}
