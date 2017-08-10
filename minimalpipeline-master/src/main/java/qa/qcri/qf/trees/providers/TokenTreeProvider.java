package qa.qcri.qf.trees.providers;

import org.apache.uima.jcas.JCas;

import qa.qcri.qf.trees.TokenTree;

public interface TokenTreeProvider {
	
	public TokenTree getTree(JCas cas);
}
