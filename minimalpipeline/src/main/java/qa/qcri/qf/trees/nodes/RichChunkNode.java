package qa.qcri.qf.trees.nodes;

import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk;

/**
 * 
 * The RichChunkNode class wraps a Chunk object, a datatype from the DKPro
 * typesystem
 */
public class RichChunkNode extends BaseRichNode {

	private Chunk chunk;

	public RichChunkNode(Chunk chunk) {
		super();
		this.chunk = chunk;
		this.metadata.put(RichNode.TYPE_KEY, RichNode.TYPE_CHUNK_NODE);
		this.value = chunk.getChunkValue();
	}

	/**
	 * 
	 * @return the DKPro Chunk object
	 */
	public Chunk getChunk() {
		return this.chunk;
	}

}
