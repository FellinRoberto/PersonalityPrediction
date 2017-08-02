package qa.qcri.qf.trees.nodes;

import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.constituent.Constituent;

/**
 * 
 * The RichConstituentNode class wraps a Constituent object, a datatype from the
 * DKPro typesystem
 */
public class RichConstituentNode extends BaseRichNode {

	private Constituent constituent;

	public RichConstituentNode(Constituent constituent) {
		super();
		this.constituent = constituent;
		this.metadata.put(RichNode.TYPE_KEY, RichNode.TYPE_CONSTITUENT_NODE);
		this.value = constituent.getConstituentType();
	}

	/**
	 * 
	 * @return the DKPro Constituent object
	 */
	public Constituent getConstituent() {
		return this.constituent;
	}

}