package qa.qcri.qf.trees.providers;

import org.apache.uima.jcas.JCas;

import qa.qcri.qf.trees.RichTree;
import qa.qcri.qf.trees.TokenTree;

public class PosChunkTreeProvider implements TokenTreeProvider {

	@Override
	public TokenTree getTree(JCas cas) {
		return RichTree.getPosChunkTree(cas);
	}
	
}
