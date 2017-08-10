package qa.qcri.qf.trees;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.JCasUtil;

import qa.qcri.qf.trees.nodes.RichTokenNode;
import util.Pair;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * 
 * Class for retrieving tokens from token trees
 * 
 */
public class TokenSelector {

	/**
	 * Retrieves the specified annotation and the tokens in the tree which are
	 * covered by that annotation
	 * 
	 * @param cas
	 *            the cas containing the covering annotation
	 * @param tree
	 *            the tokenTree built from the same cas
	 * @param type
	 *            the type of the covering annotation
	 * @return a list of pair <annotation, list of RichTokenNode>
	 */
	public static <T extends Annotation> List<Pair<T, List<RichTokenNode>>> selectTokenNodeCovered(
			JCas cas, TokenTree tree, Class<T> type) {
		List<Pair<T, List<RichTokenNode>>> mappings = new ArrayList<>();

		Map<Integer, Pair<T, List<RichTokenNode>>> indexToAnnotation = new HashMap<>();

		for (T annotation : JCasUtil.select(cas, type)) {
			Pair<T, List<RichTokenNode>> annotationAndTokenListPair = new Pair<T, List<RichTokenNode>>(
					annotation, new ArrayList<RichTokenNode>());

			mappings.add(annotationAndTokenListPair);

			/**
			 * We build an index that maps each offset covered by the
			 * annotation, to the data associated with it. In this way we can
			 * lookup the starting index of a token and recover the annotation
			 * covering it.
			 */

			int begin = annotation.getBegin();
			int end = annotation.getEnd();

			for (int i = begin; i < end; i++) {
				indexToAnnotation.put(i, annotationAndTokenListPair);
			}
		}

		for (RichTokenNode tokenNode : tree.getTokens()) {
			Token token = tokenNode.getToken();

			Pair<T, List<RichTokenNode>> annotationAndTokenListPair = indexToAnnotation
					.get(token.getBegin());

			if (annotationAndTokenListPair != null) {
				annotationAndTokenListPair.getB().add(tokenNode);
			}
		}

		return mappings;
	}

}
