package qa.qcri.qf.treemarker;

import java.util.List;

import qa.qcri.qf.trees.nodes.RichNode;

import com.google.common.collect.Lists;

/**
 * 
 * Marking strategy selecting the parent of the given node
 */
public class MarkParent implements MarkingStrategy {

	@Override
	public List<RichNode> getNodesToMark(RichNode node) {
		RichNode parent = node.getParent();
		if(parent == null) {
			return Lists.newArrayList();
		} else {
			return Lists.newArrayList(parent);
		}
	}

}
