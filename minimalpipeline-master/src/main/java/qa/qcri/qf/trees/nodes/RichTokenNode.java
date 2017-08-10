package qa.qcri.qf.trees.nodes;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * 
 * The RichTokenNode class wraps a Token object, a datatype from the DKPro
 * typesystem
 * 
 * The class overrides the getValue() method, which returns the text covered by
 * the token
 * 
 * Moreover, it understands a set of parameters for producing different token
 * representations
 */
public class RichTokenNode extends BaseRichNode {

	private Token token;
	
	public RichTokenNode(Token token) {
		super();
		this.token = token;
		this.metadata.put(RichNode.TYPE_KEY, RichNode.TYPE_TOKEN_NODE);
	}

	public Token getToken() {
		return this.token;
	}

	@Override
	public String getValue() {
		return this.token.getCoveredText();
	}

	/**
	 * Produces a string representation of the node which may be affected by the
	 * provided parameter list. A node can parse this list and react according
	 * to it. In the default case the implementation should return the same
	 * value of getValue()
	 * 
	 * Supported parameters
	 * 
	 * - RichNode.OUTPUT_PAR_TOKEN Return the text covered by the token, which
	 * is also the default behaviour
	 * 
	 * - RichNode.OUTPUT_PAR_LEMMA Return the lemma of the token
	 * 
	 * - RichNode.OUTPUT_PAR_STEM Return the stem of the token
	 * 
	 * - RichNode.OUTPUT_PAR_TOKEN_LOWERCASE Return the lowercased current
	 * representation
	 * 
	 * - RichNode.OUTPUT_PAR_POSTAG Return the postag of the current token
	 * 
	 * - RichNode.OUTPUT_PAR_SEMANTIC_KERNEL Return the form used by the semantic
	 *       kernel (lemma::1st-char-of-postag
	 * 
	 * Pay attention to the order of these parameters in the list. TOKEN and
	 * LEMMA override each other, so the parameter later in the list prevails.
	 * 
	 * @param parameterList
	 *            parameter list (strings separated by comma)
	 * @return the node representation
	 */
	@Override
	public String getRepresentation(String parameterList) {
		String output = this.getValue();
		
		POS pos = null;

		if (parameterList.isEmpty()) {
			return output;
		}

		boolean lowercase = false;

		String[] fields = parameterList.split(",");
		for (String field : fields) {
			switch (field) {
			case RichNode.OUTPUT_PAR_TOKEN:
				output = this.token.getCoveredText();
				break;
			case RichNode.OUTPUT_PAR_LEMMA:				
				if (output.equals("(")) {
					output = "-LRB-";
				} else if (output.equals(")")) {
					output = "-RRB-";
				} else {
					Lemma lemma = this.token.getLemma();
					if(lemma != null) {
						output = lemma.getValue();
					}
				}
				break;
			case RichNode.OUTPUT_PAR_STEM:
				output = this.token.getStem().getValue();
				break;
			case RichNode.OUTPUT_PAR_TOKEN_LOWERCASE:
				lowercase = true;
				break;			
			case RichNode.OUTPUT_PAR_POSTAG:
				pos = this.token.getPos();
				output = pos.getPosValue();
				break;
			case RichNode.OUTPUT_PAR_SEMANTIC_KERNEL:
				pos = this.token.getPos();
				String lemma = this.token.getLemma().getValue();
				output = lemma + "::" + pos.getPosValue().toLowerCase().charAt(0);
				break;
			}
		}

		/**
		 * Some tokens (e.g. _ ) may not have a requested representation
		 */
		if (output == null) {
			output = this.getValue();
		}

		if (lowercase) {
			output = output.toLowerCase();
		}

		return output;
	}
}
