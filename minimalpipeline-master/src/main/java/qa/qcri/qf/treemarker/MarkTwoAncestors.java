package qa.qcri.qf.treemarker;

import java.util.ArrayList;
import java.util.List;

import qa.qcri.qf.trees.nodes.RichNode;

/**
 * 
 * Marking strategy selecting the two ancestors of a given node
 */
public class MarkTwoAncestors implements MarkingStrategy {

	@Override
	public List<RichNode> getNodesToMark(RichNode node) {
		List<RichNode> nodes = new ArrayList<>();
		RichNode parent = node.getParent();
		if (parent != null) {
			nodes.add(parent);
			RichNode secondParent = parent.getParent();
			if (secondParent != null) {
				nodes.add(secondParent);
			}
		}
		return nodes;
	}

}
