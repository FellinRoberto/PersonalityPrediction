package qa.qcri.qf.trees.nodes;

import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;

/**
 * 
 * The RichDependencyNode class wraps a Dependency object, a datatype from the
 * DKPro typesystem
 */
public class RichDependencyNode extends BaseRichNode {

	private Dependency dependency;

	public RichDependencyNode(Dependency dependency) {
		super();
		this.dependency = dependency;
		this.metadata.put(RichNode.TYPE_KEY, RichNode.TYPE_DEPENDENCY_NODE);
		this.value = dependency.getDependencyType();
	}

	/**
	 * 
	 * @return the DKPro Dependency object
	 */
	public Dependency getDependency() {
		return this.dependency;
	}

}
