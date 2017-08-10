package qa.qcri.qf.treemarker;

import java.util.List;

import qa.qcri.qf.trees.nodes.RichNode;

/**
 * 
 * Interface used to implement strategies for selecting nodes to mark, starting
 * from a given node.
 */
public interface MarkingStrategy {

	/**
	 * @param node
	 *            the starting node
	 * @return the list of node to mark
	 */
	List<RichNode> getNodesToMark(RichNode node);
}
