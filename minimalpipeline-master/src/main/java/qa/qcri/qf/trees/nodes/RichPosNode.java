package qa.qcri.qf.trees.nodes;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;

/**
 * 
 * The RichPosNode class wraps a POS object, a datatype from the DKPro
 * typesystem
 */
public class RichPosNode extends BaseRichNode {

	private POS pos;

	public RichPosNode(POS pos) {
		super();
		this.pos = pos;
		this.metadata.put(RichNode.TYPE_KEY, RichNode.TYPE_CHUNK_NODE);
		this.value = pos.getPosValue();
	}

	/**
	 * 
	 * @return the DKPro POS object
	 */
	public POS getPos() {
		return this.pos;
	}

}
