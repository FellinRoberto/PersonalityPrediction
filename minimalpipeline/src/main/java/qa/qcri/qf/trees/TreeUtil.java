package qa.qcri.qf.trees;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import qa.qcri.qf.trees.nodes.RichNode;
import qa.qcri.qf.trees.nodes.RichTokenNode;

import com.google.common.base.Function;
import com.google.common.base.Joiner;

public class TreeUtil {

	/**
	 * Traverses the tree in a recursive preorder fashion
	 * 
	 * @param tree
	 *            the tree to traverse
	 * @return a list of nodes
	 */
	public static List<RichNode> getNodes(RichNode tree) {
		List<RichNode> nodes = new ArrayList<>();

		nodes.add(tree);

		List<RichNode> children = tree.getChildren();
		if (children.isEmpty()) {
			return nodes;
		} else {
			for (RichNode child : children) {
				nodes.addAll(getNodes(child));
			}
		}

		return nodes;
	}

	/**
	 * Traverses the tree in a BFS fashion
	 * 
	 * @param tree
	 *            the tree to traverse
	 * @return a list of nodes
	 */
	public static List<RichNode> getNodesBFS(RichNode tree) {
		List<RichNode> visitList = new ArrayList<>();

		Queue<RichNode> queue = new LinkedList<RichNode>();
		queue.add(tree);
		while (!queue.isEmpty()) {
			RichNode node = queue.poll();
			visitList.add(node);
			for (RichNode child : node.getChildren()) {
				queue.add(child);
			}
		}

		return visitList;
	}

	/**
	 * Traverses the tree in a recursive preorder fashion A node is returned if
	 * it satisfies the filteringCriteria which performs a test on a RichNode
	 * and returns a boolean value
	 * 
	 * @param tree
	 *            the tree to traverse
	 * @param filteringCriteria
	 *            the test performed on each node
	 * @return a list of tree nodes satisfying the filtering criteria
	 */
	public static List<RichNode> getNodesWithFilter(RichNode tree,
			Function<RichNode, Boolean> filteringCriteria) {
		List<RichNode> nodes = new ArrayList<>();

		if (filteringCriteria.apply(tree)) {
			nodes.add(tree);
		}

		List<RichNode> children = tree.getChildren();
		if (children.isEmpty()) {
			return nodes;
		} else {
			for (RichNode child : children) {
				nodes.addAll(getNodesWithFilter(child, filteringCriteria));
			}
		}

		return nodes;
	}

	/**
	 * Returns the leaves of a tree
	 * 
	 * @param tree
	 *            the tree to traverse
	 * @return the leaves of the tree
	 */
	public static List<RichNode> getLeaves(RichNode tree) {
		return TreeUtil.getNodesWithFilter(tree,
			new Function<RichNode, Boolean>() {
				@Override
				public Boolean apply(RichNode node) {
					return node.isLeaf();
				}
			}
		);
	}

	/**
	 * Returns the nodes of the tree which are not leaves
	 * 
	 * @param tree
	 *            the tree to traverse
	 * @return the nodes of the tree which are not leaves
	 */
	public static List<RichNode> getNonLeaves(RichNode tree) {
		return TreeUtil.getNodesWithFilter(tree,
			new Function<RichNode, Boolean>() {
				@Override
				public Boolean apply(RichNode node) {
					return !node.isLeaf();
				}
			}
		);
	}

	/**
	 * Returns the parents of the leaves
	 * 
	 * @param tree
	 *            the tree to traverse
	 * @return the parent nodes of the leaves
	 */
	public static List<RichNode> getLeavesParents(RichNode tree) {
		List<RichNode> leavesParents = new ArrayList<>();

		List<RichNode> leaves = TreeUtil.getLeaves(tree);
		Set<RichNode> parents = new HashSet<>();

		for (RichNode leaf : leaves) {
			RichNode parent = leaf.getParent();
			if (!parents.contains(parent)) {
				leavesParents.add(parent);
				parents.add(parent);
			}
		}

		return leavesParents;
	}

	/**
	 * Returns the grandparents of the leaves
	 * 
	 * @param tree
	 *            the tree to traverse
	 * @return the grandparent nodes of the leaves
	 */
	public static List<RichNode> getLeavesGrandParents(RichNode tree) {
		List<RichNode> leavesGrandParents = new ArrayList<>();

		List<RichNode> leaves = TreeUtil.getLeaves(tree);
		Set<RichNode> grandParents = new HashSet<>();

		for (RichNode leaf : leaves) {
			RichNode parent = leaf.getParent();
			if (parent != null) {
				RichNode grandParent = parent.getParent();
				if (grandParent != null && !grandParents.contains(parent)) {
					leavesGrandParents.add(parent);
					grandParents.add(parent);
				}
			}
		}

		return leavesGrandParents;
	}

	/**
	 * Returns the text of the nodes separated by white spaces
	 * 
	 * @param nodes
	 *            the nodes from which the text is recovered
	 * @param outputParams
	 *            the output parameters sent to the nodes
	 * @return the string containing the text of the nodes
	 */
	public static String getText(List<RichNode> nodes, String outputParams) {
		List<String> strings = new ArrayList<>();
		for (RichNode node : nodes) {
			strings.add(node.getRepresentation(outputParams));
		}
		return Joiner.on(" ").join(strings);
	}

	/**
	 * Returns the values associated with the nodes separated by white spaces
	 * 
	 * @param nodes
	 *            the nodes from which the node values are recovered
	 * @return the string containing the values of the nodes
	 */
	public static String getValues(List<RichNode> nodes) {
		List<String> strings = new ArrayList<>();
		for (RichNode node : nodes) {
			strings.add(node.getValue());
		}
		return Joiner.on(" ").join(strings);
	}

	/**
	 * Shorthand method for getting all the nodes from a tree having the
	 * specified label
	 * 
	 * @param tree
	 *            the tree to traverse
	 * @param label
	 *            the label of the node to retrieve
	 * @return the nodes in the tree having the specified label
	 */
	public static List<RichNode> getNodesWithLabel(RichNode tree,final String label) {
		return TreeUtil.getNodesWithFilter(tree, new Function<RichNode, Boolean>() {
			@Override
			public Boolean apply(RichNode node) {
				return node.getValue().equals(label);
			}
		});
	}
	
	/**
	 * Join the leaves of a tree
	 * @param tree the tree containing the leaves to join
	 * @param outputParams the output parameters for the nodes output
	 * @return a string containing the text from the leaves
	 */
	public static String joinLeaves(RichNode tree, String outputParams) {
		return TreeUtil.getText(TreeUtil.getLeaves(tree), outputParams);
	}
	
	/**
	 * Removes RELATIONAL information from the tokens of a tree
	 * @param tree the tree to cleanup
	 */
	public static void removeRelTagsFromTokens(TokenTree tree) {
		for(RichTokenNode tokenNode : tree.getTokens()) {
			tokenNode.getMetadata().remove(RichNode.REL_KEY);
		}
	}
}
