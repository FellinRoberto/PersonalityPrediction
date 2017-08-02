package qa.qcri.qf.trees;

import java.util.ArrayList;
import java.util.List;

import qa.qcri.qf.trees.nodes.RichNode;

import com.google.common.base.Joiner;

/**
 * 
 * Serializes a tree made of RichNodes
 */
public class TreeSerializer {

	/**
	 * Additional labels are combined and joined by this separator
	 */
	private static final String LABEL_SEPARATOR = "-";

	/**
	 * Default parenthesis used in serialization
	 */
	private String lbr = "(";
	private String rbr = ")";

	/**
	 * Outputting additional labels is optional and thus must be explicitly
	 * specified
	 */
	private boolean enableAdditionalLabels = false;

	/**
	 * Outputting relational tags is optional and thus must be explicitly
	 * specified
	 */
	private boolean enableRelationalTags = false;
	
	/**
	 * Adopts square brackets in the serialized trees
	 * 
	 * @return the current TreeSerialized instance (for chaining)
	 */
	public TreeSerializer useSquareBrackets() {
		this.lbr = "[";
		this.rbr = "]";
		return this;
	}

	/**
	 * Adopts round brackets in the serialized trees
	 * 
	 * @return the current TreeSerialized instance (for chaining)
	 */
	public TreeSerializer useRoundBrackets() {
		this.lbr = "(";
		this.rbr = ")";
		return this;
	}

	/**
	 * Enables additional labels in the output
	 * 
	 * @return the current TreeSerialized instance (for chaining)
	 */
	public TreeSerializer enableAdditionalLabels() {
		this.enableAdditionalLabels = true;
		return this;
	}

	/**
	 * Disables additional labels in the output
	 * 
	 * @return the current TreeSerialized instance (for chaining)
	 */
	public TreeSerializer disableAdditionalLabels() {
		this.enableAdditionalLabels = false;
		return this;
	}

	/**
	 * Enables relational tags in the output
	 * 
	 * @return the current TreeSerialized instance (for chaining)
	 */
	public TreeSerializer enableRelationalTags() {
		this.enableRelationalTags = true;
		return this;
	}

	/**
	 * Disables relational tags in the output
	 * 
	 * @return the current TreeSerialized instance (for chaining)
	 */
	public TreeSerializer disableRelationalTags() {
		this.enableRelationalTags = false;
		return this;
	}

	/**
	 * Serializes a tree starting from the specified node
	 * 
	 * @param node
	 *            the root node of the tree
	 * @param parameterList
	 *            the parameter list for the node output
	 * @return the serialized tree
	 */
	public String serializeTree(RichNode node, String parameterList) {
		List<String> leftParts = new ArrayList<>();
		List<String> rightParts = new ArrayList<>();

		leftParts.add(this.lbr);

		List<String> labels = new ArrayList<>();
		
		String nodeValue = node.getRepresentation(parameterList)
			.replaceAll("\\(", "{")
			.replaceAll("\\)", "}");
		
		labels.add(nodeValue);
		
		if (this.enableAdditionalLabels) {
			labels.addAll(node.getAdditionalLabels());
		}

		if (this.enableRelationalTags) {
			if (node.getMetadata().containsKey(RichNode.REL_KEY)) {
				labels.add(node.getMetadata().get(RichNode.REL_KEY));
			}
		}

		leftParts.add(Joiner.on(LABEL_SEPARATOR).join(labels));

		if (!node.isLeaf()) {
			leftParts.add(" ");
		}
		rightParts.add(0, this.rbr);

		for (RichNode child : node.getChildren()) {
			leftParts.add(serializeTree(child, parameterList));
		}

		leftParts.addAll(rightParts);

		return Joiner.on("").join(leftParts);
	}
	
	/**
	 * Serializes a tree starting from the specified node
	 * using an empty parameter list
	 * 
	 * @param node
	 *            the root node of the tree
	 * @return the serialized tree
	 */
	public String serializeTree(RichNode node) {
		return this.serializeTree(node, "");
	}
}
