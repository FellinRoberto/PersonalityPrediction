package qa.qcri.qf.trees.pruning;

import java.util.Iterator;
import java.util.List;

import qa.qcri.qf.trees.TokenTree;
import qa.qcri.qf.trees.TreeSerializer;
import qa.qcri.qf.trees.TreeUtil;
import qa.qcri.qf.trees.nodes.RichNode;
import qa.qcri.qf.trees.nodes.RichTokenNode;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class PosChunkLeavesPruner implements Pruner {

	private int radius;

	public PosChunkLeavesPruner(int radius) {
		this.radius = radius;
	}

	@Override
	public RichNode prune(RichNode tree, Function<List<RichNode>, List<Boolean>> pruningCriteria) {
		List<RichNode> leaves = TreeUtil.getLeaves(tree);
		
		if(leaves.isEmpty()) {
			return tree;
		}
		
		List<Boolean> leavesToPruneIndexes = pruningCriteria.apply(leaves);

		/**
		 * In the work published until now, when the pruning criteria satisfies all
		 * leaves because there is no relational information attached to them,
		 * the tree is returned as is, without pruning nodes.
		 */
		if (!leavesToPruneIndexes.contains(false))
			return tree;

		final int leavesSize = leaves.size();

		assert leavesSize == leavesToPruneIndexes.size();

		String startingTree = (new TreeSerializer()).serializeTree(tree);

		if (this.radius > 0) {
			Boolean[] pruneIndexes = new Boolean[leavesSize];
			for (int i = 0; i < leavesSize; i++) {
				pruneIndexes[i] = true;
			}

			for (int i = 0; i < leavesSize; i++) {
				if (leavesToPruneIndexes.get(i) == false) {
					/**
					 * If a node must not be pruned we give the same status to
					 * its neighbors within the radius
					 */
					for (int j = i - this.radius; j <= i + this.radius; j++) {
						if (0 <= j && j < leavesSize) {
							pruneIndexes[j] = false;
						}
					}
				}
			}

			leavesToPruneIndexes = Lists.newArrayList(pruneIndexes);
		}

		for (int i = 0; i < leavesSize; i++) {
			if (leavesToPruneIndexes.get(i)) {
				try {
					RichNode posTag = leaves.get(i).getParent();
					RichNode chunk = posTag.getParent();
					chunk.getChildren().remove(posTag);

					/**
					 * Recursively remove
					 */
					removeEmptySubtree(chunk);

				} catch (NullPointerException ex) {
					System.out.println("Current leaf: " + leaves.get(i).getValue());
					System.out.println(startingTree);
					System.out.println((new TreeSerializer()).serializeTree(tree));
					System.exit(1);
				}
			}
		}

		/**
		 * Remove the pruned tokens from the tokens list maintained by the
		 * TokenTree data structure
		 */

		List<RichTokenNode> tokens = ((TokenTree) tree).getTokens();
		Iterator<RichTokenNode> tokensIterator = tokens.iterator();
		int tokenIndex = 0;

		while (tokensIterator.hasNext()) {
			tokensIterator.next();
			if (leavesToPruneIndexes.get(tokenIndex)) {
				tokensIterator.remove();
			}
			tokenIndex++;
		}

		return tree;
	}

	private void removeEmptySubtree(RichNode node) {
		if (node.isLeaf()) {
			RichNode parent = node.getParent();
			if (parent != null) {
				parent.getChildren().remove(node);
				removeEmptySubtree(parent);
			}
		}
	}
}
