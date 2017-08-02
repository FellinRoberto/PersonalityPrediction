package qa.qcri.qf.chunker;

import java.util.Collection;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.base.Predicate;

import qa.qcri.qf.trees.RichTree;
import qa.qcri.qf.trees.TokenTree;
import qa.qcri.qf.trees.nodes.RichConstituentNode;
import qa.qcri.qf.trees.nodes.RichNode;
import qa.qcri.qf.trees.nodes.RichTokenNode;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.constituent.Constituent;

@TypeCapability(
		inputs = {
			"de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS",
			"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
			"de.tudarmstadt.ukp.dkpro.core.api.syntax.type.constituent.Constituent"
			
		},
		outputs = {
			"de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk"	
		}
)
/**
 * Constituency Tree Chunker.
 * 
 * 1) you put <top> on level 1
 * 2) you put all the leaves on level 3
 * 3) in the original tree, for each  leaf L:
 * --you go one level up to get its parent node.
 * if all the children of this parent node are leaves, you put this
 * parent node on level 2 of the chunk tree, you link it to the <top> and
 * link each child to it.
 * else (== if the parent node has at least one non-leaf child), you
 * create an artificial node, duplicating the pos-tag of L. You put the
 * artificial node on level 2, link it to the top and link L to it.
 * 
 * =======
 * 
 * example: (S (NP (A x) (NP (B y) (C z))))
 * 
 * this should generate
 * 
 * (TOP (A (A x)) (NP (B y) (C z)))
 */
public class ConstituencyTreeChunker extends JCasAnnotator_ImplBase {
	
	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);
		System.err.println("Informazioni: Launching ConstituencyTreeChunker...");
	}
	
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		TokenTree tree = RichTree.getConstituencyTree(jcas);
		List<RichTokenNode> tokens = tree.getTokens();
		for (int i = 0; i < tokens.size(); ) {
			RichTokenNode tokenNode = tokens.get(i);
			RichNode prePreTerminal = tokenNode.getGrandParent();			
			List<RichNode> children = prePreTerminal.getChildren();
			
			if (all(isPreTerminal, children)) { 
				Constituent con = ((RichConstituentNode) prePreTerminal).getConstituent();
				Chunk chunk = new Chunk(jcas, con.getBegin(), con.getEnd());
				chunk.setChunkValue(con.getConstituentType());
				chunk.addToIndexes();
				i += children.size();
			} else {
				Token token = tokenNode.getToken();
				Chunk chunk = new Chunk(jcas, token.getBegin(), token.getEnd());
				chunk.setChunkValue(token.getPos().getPosValue());
				chunk.addToIndexes();
				i++;
			}
		}
		
	} 
	
	private static Predicate<RichNode> isLeaf = new Predicate<RichNode>() {
		@Override
		public boolean apply(RichNode node) {
			return node.isLeaf();
		}		
	};
	
	private static Predicate<RichNode> isPreTerminal = new Predicate<RichNode>() {
		@Override
		public boolean apply(RichNode node) { 
			return node.isPreterminal();
		}
	};
	
	private static <E> boolean all(Predicate<? super E> predicate, Collection<E> collection) { 
		for (E elem : collection) 
			if (!predicate.apply(elem)) 
				return false;
		return true;		
	}
	
	

}
