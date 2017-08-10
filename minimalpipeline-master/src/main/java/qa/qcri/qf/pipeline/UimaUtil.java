package qa.qcri.qf.pipeline;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import qa.qcri.qf.trees.nodes.RichTokenNode;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class UimaUtil {

	/**
	 * Extract tokens from the CAS and wraps them in RichTokenNode objects
	 * 
	 * @param cas
	 * @return a list of RichTokenNode
	 */
	public static List<RichTokenNode> getRichTokens(JCas cas) {
		List<RichTokenNode> tokens = new ArrayList<>();
		for (Token token : JCasUtil.select(cas, Token.class)) {
			RichTokenNode richTokenNode = new RichTokenNode(token);
			tokens.add(richTokenNode);
		}
		return tokens;
	}

	/**
	 * Extracts the token representation from a list of RichTokenNode
	 * 
	 * @param tokens
	 *            the list of RichTokenNode
	 * @param parameterList
	 *            the parameters describing the representation of the tokens
	 * @return the desired representation of the tokens
	 */
	public static List<String> getRichTokensRepresentation(
			List<RichTokenNode> tokens, String parameterList) {
		List<String> representation = new ArrayList<>();
		for (RichTokenNode token : tokens) {
			representation.add(token.getRepresentation(parameterList));
		}
		return representation;
	}

	/**
	 * Extracts the token representation from the tokens in a cas
	 * 
	 * @param cas
	 * @param parameterList
	 *            the parameters describing the representation of the tokens
	 * @return the desired representation of the tokens
	 */
	public static List<String> getTokensRepresentation(JCas cas,
			String parameterList) {
		List<String> tokens = new ArrayList<>();
		for (Token token : JCasUtil.select(cas, Token.class)) {
			tokens.add(new RichTokenNode(token)
					.getRepresentation(parameterList));
		}
		return tokens;
	}
}
