package qa.qcri.qf.trees;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.maltparser.core.helper.HashMap;

import qa.qcri.qf.trees.nodes.BaseRichNode;
import qa.qcri.qf.trees.nodes.RichTokenNode;

/**
 * 
 * TokenTree is a class which extends BaseRichNode and provides handy access to
 * a list of RichTokenNode
 * 
 * TokenTree object are used as Root node for several trees produced by the
 * framework
 * 
 * For example, POS+CHUNK roots are TokenTree objects
 */
public class TokenTree extends BaseRichNode {

	private List<RichTokenNode> tokens;

	private Map<String, RichTokenNode> idToToken;

	private int nextFreeId = 0;

	public TokenTree() {
		this.tokens = new ArrayList<>();
		this.idToToken = new HashMap<>();
	}

	/**
	 * 
	 * @return the list of RichTokenNode which ideally are descendants of this node
	 */
	public List<RichTokenNode> getTokens() {
		return this.tokens;
	}

	/**
	 * Adds a token to the list of tokens
	 * 
	 * @param token
	 * 
	 * @return the object instance for chaining
	 */
	public TokenTree addToken(RichTokenNode token) {
		this.tokens.add(token);
		this.idToToken.put(String.valueOf(nextFreeId++), token);
		return this;
	}

	/**
	 * Internally the nodes are associated with ids
	 * 
	 * This feature may be removed
	 * 
	 * @param id
	 * @return the RichTokenNode
	 */
	public RichTokenNode getTokenById(String id) {
		assert (this.idToToken.containsKey(id) == true);

		return this.idToToken.get(id);
	}

}
