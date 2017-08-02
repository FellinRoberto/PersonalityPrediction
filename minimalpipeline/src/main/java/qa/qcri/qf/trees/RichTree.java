package qa.qcri.qf.trees;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.Lists;

import qa.qcri.qf.trees.nodes.BaseRichNode;
import qa.qcri.qf.trees.nodes.RichChunkNode;
import qa.qcri.qf.trees.nodes.RichConstituentNode;
import qa.qcri.qf.trees.nodes.RichDependencyNode;
import qa.qcri.qf.trees.nodes.RichNode;
import qa.qcri.qf.trees.nodes.RichPosNode;
import qa.qcri.qf.trees.nodes.RichTokenNode;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.constituent.Constituent;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;

/**
 * 
 * RichTree is a factory class for creating rich trees from annotated CASes
 */
public class RichTree {

	public static final String ROOT_LABEL = "ROOT";
	public static final String SENTENCE_LABEL = "S";

	/**
	 * Builds a POS+CHUNK tree from an annotated CAS. The CAS must contain
	 * sentence boundaries, tokens, POStags and chunks A POS+CHUNK tree is a
	 * tree with two salient layer. The bottom layer is made by tokens with
	 * their POStags. These nodes are grouped by chunk nodes.
	 * 
	 * @param cas
	 *            the UIMA JCas
	 * @return the POS+CHUNK tree, as a TokenTree
	 * 
	 * @see TokenTree
	 */
	public static TokenTree getPosChunkTree(JCas cas) {
		TokenTree root = new TokenTree();
		root.setValue(ROOT_LABEL);

		for (Sentence sentence : JCasUtil.select(cas, Sentence.class)) {
			RichNode sentenceNode = new BaseRichNode().setValue(SENTENCE_LABEL);

			for (Chunk chunk : JCasUtil.selectCovered(cas, Chunk.class,
					sentence)) {
				RichNode chunkNode = new RichChunkNode(chunk);
				for (Token token : JCasUtil.selectCovered(cas, Token.class,
						chunk)) {
					RichNode posNode = new BaseRichNode().setValue(token
							.getPos().getPosValue());

					RichTokenNode tokenNode = new RichTokenNode(token);

					chunkNode.addChild(posNode.addChild(tokenNode));

					root.addToken(tokenNode);
				}
				sentenceNode.addChild(chunkNode);
			}

			if(!sentenceNode.isLeaf()) {
				root.addChild(sentenceNode);
			}
		}

		return root;
	}

	/**
	 * Builds a Constituency tree from an annotated CAS. The CAS must contain
	 * sentence boundaries, tokens, POStags and constituents
	 * 
	 * @param cas
	 *            the UIMA JCas
	 * @return the Constituency tree, as a TokenTree
	 * 
	 * @see TokenTree
	 */
	public static TokenTree getConstituencyTree(JCas cas) {
		
		TokenTree root = new TokenTree();
		root.setValue(ROOT_LABEL);

		List<Constituent> roots = new ArrayList<>();

		for (Constituent constituent : JCasUtil.select(cas, Constituent.class)) {
			  if (constituent.getConstituentType().equals(ROOT_LABEL) ) {
				roots.add(constituent);
			}
		}

		for (Constituent node : roots) {
			
			RichNode subTrees = getConstituencySubTree(node, root);
			
			for(RichNode subTree : subTrees.getChildren()) {
				root.addChild(subTree);
			}
		}

		return root;
	}

	/**
	 * Recursive method for producing constituency trees out of a node
	 * 
	 * @param subTreeRoot
	 *            the root node of the subtree
	 * @param root
	 *            the root of the tree
	 * @return a serialized constituency subtree
	 */
	private static RichNode getConstituencySubTree(Constituent subTreeRoot,
			TokenTree root) {
		RichNode subTree = new RichConstituentNode(subTreeRoot);
		
		Collection<Constituent> constituents = JCasUtil.select(
				subTreeRoot.getChildren(), Constituent.class);

		for (Constituent constituent : constituents) {
			subTree.addChild(getConstituencySubTree(constituent, root));
		}

		List<Token> tokens = Lists.newArrayList(JCasUtil.select(subTreeRoot.getChildren(), Token.class));
		Collections.reverse(tokens);
		for (Token token : tokens) {
			RichNode posNode = new BaseRichNode().setValue(
					token.getPos().getPosValue());

			RichTokenNode tokenNode = new RichTokenNode(token);

			int insertionIndex = 0;
			for(Constituent constituent : constituents) {
				if(token.getBegin() > constituent.getBegin()) {
					insertionIndex++;
				}
			}
			
			subTree.addChild(insertionIndex, posNode.addChild(tokenNode));

			root.addToken(tokenNode);
		}

		return subTree;
	}

	/**
	 * Builds a Dependency tree from an annotated CAS. The CAS must contain
	 * sentence boundaries, tokens, POStags and dependencies
	 * 
	 * @param cas
	 *            the UIMA JCas
	 * @return the Dependency tree, as a TokenTree
	 * 
	 * @see TokenTree
	 */
	public static TokenTree getDependencyTree(JCas cas) {
		if (cas == null) {
			throw new NullPointerException("CAS is null");
		}

		TokenTree root = new TokenTree();
		root.setValue(ROOT_LABEL);

		Map<Token, RichTokenNode> nodeMap = new HashMap<>();

		Collection<Dependency> deps = JCasUtil.select(cas, Dependency.class);
		for (Dependency dep : deps) {
			RichTokenNode govNode = getOrAddIfNew(dep.getGovernor(), nodeMap);
			RichTokenNode depNode = getOrAddIfNew(dep.getDependent(), nodeMap);
			RichDependencyNode relNode = new RichDependencyNode(dep);

			relNode.addChild(depNode);
			govNode.addChild(relNode);
		}

		// Sort token nodes
		List<RichTokenNode> tokenNodes = new ArrayList<>(nodeMap.values());
		Collections.sort(tokenNodes, new Comparator<RichTokenNode>() {
			@Override
			public int compare(RichTokenNode o1, RichTokenNode o2) {
				return o1.getToken().getBegin() - o2.getToken().getBegin();
			}
		});

		// Add token nodes
		for (RichTokenNode tokenNode : tokenNodes) {
			root.addToken(tokenNode);

			if (!tokenNode.hasParent()) {
				// Insert a fake dependency node between the root and the first token node
				RichDependencyNode depNode = newRichDependencyNode(cas,tokenNode, ROOT_LABEL);
				root.addChild(depNode);
				depNode.addChild(tokenNode);
			}
		}
		
		return root;
	}
	
	/**
	 * Builds a Lexically Centered Tree (LCT) from an annotated CAS. The CAS must contain
	 * sentence boundaries, tokens, POStags and dependencies
	 * 
	 * @param cas
	 *            the UIMA JCas
	 * @return the LCT tree, as a TokenTree
	 * 
	 * @see TokenTree
	 */
	public static TokenTree getLctTree(JCas cas) {
		if (cas == null) {
			throw new NullPointerException("CAS is null");
		}
		
		// In literature SVMLightTK trees are wrapped in a root node labelled TOP.
		TokenTree root = new TokenTree();
		root.setValue("TOP");
		
		Map<Token, RichTokenNode> nodeMap = new HashMap<>();
		Map<RichTokenNode, Dependency> dependencyMap = new HashMap<>();

		Collection<Dependency> deps = JCasUtil.select(cas, Dependency.class);
		for (Dependency dep : deps) {
			RichTokenNode govNode = getOrAddIfNew(dep.getGovernor(), nodeMap);
			RichTokenNode depNode = getOrAddIfNew(dep.getDependent(), nodeMap);
			govNode.addChild(depNode);
			
			dependencyMap.put(depNode, dep);
		}

		// Sort token nodes
		List<RichTokenNode> tokenNodes = new ArrayList<>(nodeMap.values());
		Collections.sort(tokenNodes, new Comparator<RichTokenNode>() {
			@Override
			public int compare(RichTokenNode o1, RichTokenNode o2) {
				return o1.getToken().getBegin() - o2.getToken().getBegin();
			}
		});

		// Add token nodes
		for (RichTokenNode tokenNode : tokenNodes) {
			root.addToken(tokenNode);

			if (!tokenNode.hasParent()) {
				root.addChild(tokenNode);
			}
		}
		
		for (RichTokenNode tokenNode : tokenNodes) {
			// Add the postag of the current token
			tokenNode.addChild(new RichPosNode(tokenNode.getToken().getPos()));
			
			// Add the relation of the dependency label of the current token as last child
			if (dependencyMap.containsKey(tokenNode)) {
				RichDependencyNode dependencyNode = new RichDependencyNode(dependencyMap.get(tokenNode));
				tokenNode.addChild(dependencyNode);
			} else {
				RichDependencyNode dependencyNode = newRichDependencyNode(cas, tokenNode, ROOT_LABEL);
				tokenNode.addChild(dependencyNode);
			}
		}
		
		return root;
	}

	/**
	 * Utility method for retrieving a RichNode corresponding to a Token from a
	 * map. If the Token is not present it is wrapped in a RichNode and stored
	 * into the map, for the current and future lookup
	 * 
	 * @param token
	 *            the token to lookup or add as a RichToken
	 * @param nodeMap
	 *            the map of <Token, RichTokenNode>
	 * @return the RichTokenNode corresponding to the Token
	 */
	private static RichTokenNode getOrAddIfNew(Token token,
			Map<Token, RichTokenNode> nodeMap) {
		assert token != null;
		assert nodeMap != null;

		RichTokenNode node = nodeMap.get(token);
		if (node != null) {
			return node;
		} else {
			node = new RichTokenNode(token);
			nodeMap.put(token, node);
		}
		
		return node;
	}

	/**
	 * Produce a new RichDependencyNode wrapping a given Dependency
	 * @param cas the Cas from which the Dependency is produced
	 * @param depNode the node in the dependency
	 * @param dependencyType the type of dependency
	 * @return the RichDependencyNode
	 */
	private static RichDependencyNode newRichDependencyNode(JCas cas,
			RichTokenNode depNode, String dependencyType) {
		assert cas != null;
		assert depNode != null;
		assert dependencyType != null;

		Token dep = depNode.getToken();
		Dependency dependency = new Dependency(cas, dep.getBegin(), dep.getEnd());
		dependency.setDependencyType(dependencyType);

		return new RichDependencyNode(dependency);
	}

}
