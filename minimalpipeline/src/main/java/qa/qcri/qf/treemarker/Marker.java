package qa.qcri.qf.treemarker;

import java.util.List;

import org.apache.uima.jcas.JCas;

import qa.qcri.qf.trees.TokenSelector;
import qa.qcri.qf.trees.TokenTree;
import qa.qcri.qf.trees.TreeUtil;
import qa.qcri.qf.trees.nodes.RichNode;
import qa.qcri.qf.trees.nodes.RichTokenNode;
import util.Pair;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;

/**
 * 
 * Utility class for marking nodes
 */
public class Marker {

	/**
	 * Label for used for marking focus node
	 */
	public static final String FOCUS_LABEL = "FOCUS";
	
	/**
	 * Cached mark the same node strategy
	 */
	public static MarkingStrategy markThisNode = new MarkThisNode();
	
	/**
	 * Adds a relational tag to the node selected by the given marking strategy
	 * 
	 * @param node
	 *            the starting node
	 * @param strategy
	 *            the marking strategy
	 */
	public static void addRelationalTag(RichNode node, MarkingStrategy strategy) {
		for (RichNode nodeToMark : strategy.getNodesToMark(node)) {
			nodeToMark.getMetadata().put(RichNode.REL_KEY, RichNode.REL_KEY);
		}
	}
	
	/**
	 * Removes Relational information from a tree
	 * 
	 * @param tree the tree to clear
	 */
	public static void removeRelationalTagFromTree(RichNode tree) {
		for (RichNode node : TreeUtil.getNodes(tree)) {
			node.getMetadata().remove(RichNode.REL_KEY);
		}
	}

	/**
	 * Marks the named entities in a tree with their type
	 * 
	 * @param cas
	 *            the CAS from which the tree is extracted
	 * @param tree
	 *            the tree extracted from the CAS
	 * @param labelPrefix
	 *            the string to prepend to the label
	 */
	public static void markNamedEntities(JCas cas, TokenTree tree,
			String labelPrefix) {
		for (Pair<NamedEntity, List<RichTokenNode>> neAndToken : TokenSelector
				.selectTokenNodeCovered(cas, tree, NamedEntity.class)) {

			NamedEntity ne = neAndToken.getA();
			String namedEntityType = ne.getValue().toUpperCase();

			for (RichTokenNode tokenNode : neAndToken.getB()) {
				for (RichNode node : new MarkTwoAncestors()
						.getNodesToMark(tokenNode)) {
					String label = namedEntityType;
					if (!labelPrefix.isEmpty()) {
						label += labelPrefix + "-";
					}
					node.addAdditionalLabel(label);
				}
			}
		}
	}	
}
