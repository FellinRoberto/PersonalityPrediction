package qa.qcri.qf.features.representation;

import java.util.Iterator;
import java.util.List;

import org.apache.uima.jcas.JCas;

import qa.qcri.qf.pipeline.UimaUtil;
import qa.qcri.qf.trees.nodes.RichNode;
import qa.qcri.qf.trees.nodes.RichTokenNode;
import util.Pair;
import util.Stopwords;

import com.google.common.base.Joiner;

public class TokenRepresentation implements Representation {

	private String parameterList;
	
	private boolean removeStopwords;
	
	private Stopwords stopwords;

	public TokenRepresentation(String parameterList) {
		this.parameterList = parameterList;
		this.removeStopwords = false;
	}
	
	public TokenRepresentation(String parameterList, Stopwords stopwords) {
		this.parameterList = parameterList;
		this.removeStopwords = true;
		this.stopwords = stopwords;
	}

	@Override
	public Pair<String, String> getRepresentation(JCas aCas, JCas bCas) {
		return new Pair<>(
				getRepresentation(aCas, this.parameterList),
				getRepresentation(bCas, this.parameterList)
			);
	}

	private String getRepresentation(JCas cas, String parameterList) {
		List<RichTokenNode> richTokens = UimaUtil.getRichTokens(cas);
		
		if(this.removeStopwords) {
			Iterator<RichTokenNode> i = richTokens.iterator();
			while(i.hasNext()) {
				RichTokenNode token = i.next();
				if(this.stopwords.contains(token.getRepresentation(RichNode.OUTPUT_PAR_TOKEN_LOWERCASE))) {
					i.remove();
				}
			}
		}
		
		List<String> tokens = UimaUtil.getRichTokensRepresentation(
				richTokens, parameterList);
		
		return Joiner.on(" ").join(tokens);
	}

	@Override
	public String getName() {
		return "TokenRepresentation";
	}

}
