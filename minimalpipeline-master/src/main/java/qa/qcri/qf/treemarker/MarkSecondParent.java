package qa.qcri.qf.treemarker;

import java.util.ArrayList;
import java.util.List;

import qa.qcri.qf.trees.nodes.RichNode;

/**
 * 
 * Marking strategy selecting only the second ancestor of a given node
 */
public class MarkSecondParent implements MarkingStrategy {

	@Override
	public List<RichNode> getNodesToMark(RichNode node) {
		List<RichNode> nodes = new ArrayList<>();
		RichNode parent = node.getParent();
		if (parent != null) {
			RichNode secondParent = parent.getParent();
			if (secondParent != null) {
				nodes.add(secondParent);
			}
		}
		return nodes;
	}
}