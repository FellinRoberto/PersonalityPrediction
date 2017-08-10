package qa.qcri.qf.trees.pruning;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import qa.qcri.qf.trees.TokenTree;
import qa.qcri.qf.trees.TreeSerializer;
import qa.qcri.qf.trees.TreeUtil;
import qa.qcri.qf.trees.nodes.RichNode;
import qa.qcri.qf.trees.nodes.RichTokenNode;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class PosChunkPruner implements Pruner {

	private int radius;

	public PosChunkPruner(int radius) {
		this.radius = radius;
	}

	@Override
	public RichNode prune(RichNode tree, Function<List<RichNode>, List<Boolean>> pruningCriteria) {
		List<RichNode> chunks = TreeUtil.getLeavesGrandParents(tree);
		
		if(chunks.isEmpty()) {
			return tree;
		}
		
		List<Boolean> chunksToPruneIndexes = pruningCriteria.apply(chunks);

		/**
		 * In the work published until now, when the pruning criteria satisfies all
		 * nodes because there is no relational information attached to them,
		 * the tree is returned as is, without applying any pruning.
		 */
		if (!chunksToPruneIndexes.contains(false))
			return tree;

		final int chunksSize = chunks.size();

		assert chunksSize == chunksToPruneIndexes.size();

		String startingTree = (new TreeSerializer()).serializeTree(tree);

		if (this.radius > 0) {
			Boolean[] pruneIndexes = new Boolean[chunksSize];
			for (int i = 0; i < chunksSize; i++) {
				pruneIndexes[i] = true;
			}

			for (int i = 0; i < chunksSize; i++) {
				if (chunksToPruneIndexes.get(i) == false) {
					/**
					 * If a node must not be pruned we give the same status to
					 * its neighbors within the radius
					 */
					for (int j = i - this.radius; j <= i + this.radius; j++) {
						if (0 <= j && j < chunksSize) {
							pruneIndexes[j] = false;
						}
					}
				}
			}

			chunksToPruneIndexes = Lists.newArrayList(pruneIndexes);
		}
		
		Set<RichTokenNode> prunedTokens = new HashSet<>();

		for (int i = 0; i < chunksSize; i++) {
			if (chunksToPruneIndexes.get(i)) {
				try {
					RichNode chunk = chunks.get(i);
					RichNode sentence = chunk.getParent();
					sentence.getChildren().remove(chunk);
					
					for(RichNode tokenNode : TreeUtil.getLeaves(chunk)) {
						prunedTokens.add((RichTokenNode) tokenNode);
					}

					/**
					 * Recursively remove
					 */
					removeEmptySubtree(sentence);

				} catch (NullPointerException ex) {
					System.out.println("Current chunk: " + chunks.get(i).getValue());
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

		while (tokensIterator.hasNext()) {
			RichTokenNode token = tokensIterator.next();
			if (prunedTokens.contains(token)) {
				tokensIterator.remove();
			}
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
