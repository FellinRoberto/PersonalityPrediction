package qa.qcri.qf.treemarker;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qa.qcri.qf.trees.nodes.RichNode;

import com.google.common.collect.Lists;

/**
 * 
 * Marking strategy selecting the current node if it has at least a percentage
 * of children having RELATIONAL information
 */
public class MarkIfThisNodeHasRelChildren implements MarkingStrategy {

	private Double percentage;

	private final Logger logger = LoggerFactory
			.getLogger(MarkIfThisNodeHasRelChildren.class);

	public MarkIfThisNodeHasRelChildren() {
		this.percentage = 1.0;
	}

	/**
	 * 
	 * @param percentageOfChildrenWithTag
	 *            the percentage of children having relational information
	 *            required to mark this node
	 */
	public MarkIfThisNodeHasRelChildren(Double percentageOfChildrenWithTag) {
		if (!(percentageOfChildrenWithTag >= 0.0 && percentageOfChildrenWithTag <= 1.0)) {
			this.percentage = 1.0;
			this.logger.error("The percentage of children is not a number between 0.0 and 1.0");
		}
		this.percentage = percentageOfChildrenWithTag;
	}

	@Override
	public List<RichNode> getNodesToMark(RichNode node) {
		List<RichNode> children = node.getChildren();

		/**
		 * Handles edge case of node not having children
		 */
		if (children.isEmpty()) {
			return Lists.newArrayList();
		}

		/**
		 * Counts the number of children having RELATIONAL information
		 */
		int numberOfChildrenWithRelTag = 0;
		for (RichNode child : children) {
			if (child.getMetadata().containsKey(RichNode.REL_KEY)) {
				numberOfChildrenWithRelTag++;
			}
		}

		/**
		 * Performs the percentage check
		 */
		if ((numberOfChildrenWithRelTag / children.size()) >= this.percentage) {
			return Lists.newArrayList(node);
		} else {
			return Lists.newArrayList();
		}
	}

}
