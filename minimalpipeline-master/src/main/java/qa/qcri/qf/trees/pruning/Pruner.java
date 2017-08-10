package qa.qcri.qf.trees.pruning;

import java.util.List;

import qa.qcri.qf.trees.nodes.RichNode;

import com.google.common.base.Function;

public interface Pruner {

	/**
	 * Prunes a tree. An implementation should use the pruning criteria function
	 * to understand which nodes are eligible for pruning. The nodes to test
	 * should be collected from the tree and put in a list
	 * 
	 * @param tree
	 *            the tree to prune
	 * @param pruningCriteria
	 *            a function which takes a list of nodes collected from the tree
	 *            and returns a list of boolean values of the same size of the
	 *            nodes list. At each node corresponds a boolean value in this
	 *            list (at same index), indicating if the node should be pruned
	 *            or not.
	 * 
	 * @return the pruned tree
	 */
	public RichNode prune(RichNode tree,
			Function<List<RichNode>, List<Boolean>> pruningCriteria);

}
