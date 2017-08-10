package qa.qcri.qf.treemarker;

import java.util.List;

import qa.qcri.qf.trees.nodes.RichNode;

import com.google.common.collect.Lists;

/**
 * 
 * Marking strategy selecting the same node passed as argument
 */
public class MarkThisNode implements MarkingStrategy {

	@Override
	public List<RichNode> getNodesToMark(RichNode node) {
		return Lists.newArrayList(node);
	}

}
