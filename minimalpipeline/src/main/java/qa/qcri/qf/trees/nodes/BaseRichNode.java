package qa.qcri.qf.trees.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;

/**
 * Base node which implements the functionalities of the RichNode interface. All
 * the other rich nodes should extend this class
 */
public class BaseRichNode implements RichNode {

	protected Map<String, String> metadata;

	protected Set<String> additionalLabels;

	protected List<RichNode> children;

	protected RichNode parent;

	protected String value;

	public BaseRichNode() {
		this.metadata = new HashMap<>();
		this.additionalLabels = new HashSet<>();
		this.children = new ArrayList<>();
		this.parent = null;

		/*
		 * Placeholder value, a remainder for initialization
		 */
		this.value = "NOT_INITIALIZED";
	}

	@Override
	public Map<String, String> getMetadata() {
		return this.metadata;
	}

	@Override
	public List<String> getAdditionalLabels() {
		List<String> labels = Lists.newArrayList(this.additionalLabels);
		Collections.sort(labels);
		return labels;
	}

	@Override
	public RichNode addAdditionalLabel(String label) {
		this.additionalLabels.add(label);
		return this;
	}
	
	@Override
	public RichNode removeAdditionalLabel(String label) {
		this.additionalLabels.remove(label);
		return this;
	}


	@Override
	public List<RichNode> getChildren() {
		return this.children;
	}

	@Override
	public RichNode addChild(RichNode node) {
		node.setParent(this);
		this.children.add(node);
		return this;
	}
	
	@Override
	public RichNode addChild(int index, RichNode node) {
		node.setParent(this);
		this.children.add(index, node);
		return this;
	}
	
	@Override
	public boolean hasParent() {
		return this.parent != null;
	}

	@Override
	public RichNode getParent() {
		return this.parent;
	}

	@Override
	public RichNode setParent(RichNode node) {
		this.parent = node;
		return this;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public RichNode setValue(String value) {
		this.value = value;
		return this;
	}

	@Override
	public boolean isLeaf() {
		return this.children.isEmpty();
	}

	@Override
	public String getRepresentation(String parameterList) {
		return this.value;
	}
	
	@Override
	public String toString() {
		return getRepresentation(this.value);
	}

	@Override
	public boolean isPreterminal() {
		return 	this.children.size() == 1 &&
				this.children.get(0).isLeaf();
	}

	@Override
	public RichNode getGrandParent() {
		return this.getParent().getParent();
	}
	
}
