package qa.qcri.qf.trees.providers;

import org.apache.uima.jcas.JCas;

import qa.qcri.qf.trees.RichTree;
import qa.qcri.qf.trees.TokenTree;

public class ConstituencyTreeProvider implements TokenTreeProvider {

	@Override
	public TokenTree getTree(JCas cas) {
		return RichTree.getConstituencyTree(cas);
	}

}
