package qa.qcri.qf.tutorial;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import qa.qcri.qf.annotators.IllinoisChunker;
import qa.qcri.qf.features.PairFeatureFactory;
import qa.qcri.qf.pipeline.Analyzer;
import qa.qcri.qf.pipeline.retrieval.Analyzable;
import qa.qcri.qf.pipeline.retrieval.SimpleContent;
import qa.qcri.qf.pipeline.serialization.UIMAFilePersistence;
import qa.qcri.qf.trees.RichTree;
import qa.qcri.qf.trees.TokenTree;
import qa.qcri.qf.trees.TreeSerializer;
import qa.qcri.qf.trees.TreeUtil;
import qa.qcri.qf.trees.nodes.RichNode;
import util.Stopwords;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;

import com.google.common.base.Function;
import com.google.common.base.Joiner;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordParser;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordSegmenter;

public class AnalyzerAndTreeFrameworkTutorial {
	
	public static void main(String[] args) throws UIMAException, IOException {
		
		/**
		 * Let's instantiate some annotators for our pipelines
		 */
		
		AnalysisEngine stanfordSegmenter = AnalysisEngineFactory
				.createEngine(createEngineDescription(StanfordSegmenter.class));

		AnalysisEngine stanfordPosTagger = AnalysisEngineFactory
				.createEngine(createEngineDescription(OpenNlpPosTagger.class,
						 StanfordPosTagger.PARAM_LANGUAGE, "en"));

		AnalysisEngine stanfordLemmatizer = AnalysisEngineFactory
				.createEngine(createEngineDescription(StanfordLemmatizer.class));

		AnalysisEngine illinoisChunker = AnalysisEngineFactory
				.createEngine(createEngineDescription(IllinoisChunker.class));

		AnalysisEngine stanfordParser = AnalysisEngineFactory
				.createEngine(createEngineDescription(StanfordParser.class));
		
		/**
		 * Now we create our Analyzer which uses the serialization mechanism
		 * to save on disk annotations about the analyzed pieces of content 
		 */
		
		Analyzer analyzer = new Analyzer(new UIMAFilePersistence("CASes/iyas-tutorial/"));
		
		/**
		 * We add four annotators. Notice that we do not specify a pipeline identifier
		 * after the annotator objects. These annotators will be added by default
		 * to the main pipeline.
		 */
		analyzer.addAE(stanfordSegmenter)
			.addAE(stanfordPosTagger)
			.addAE(stanfordLemmatizer)
			.addAE(illinoisChunker);
		
		/**
		 * On some pieces of content we want to run a parser. Since parsing is expensive
		 * we will build a separate pipeline for parsing, reusing the other annotators.
		 * The pipeline will have the "parsing" identifier. The same identifier will be
		 * specified when using the analyzer on the content we want to parse.
		 */
		
		analyzer.addAE(stanfordSegmenter, "parsing")
			.addAE(stanfordPosTagger, "parsing")
			.addAE(stanfordLemmatizer, "parsing")
			.addAE(illinoisChunker, "parsing")
			.addAE(stanfordParser, "parsing");
		
		/**
		 * Now we create some content to analyze. Analyzable content implements the
		 * "Analyzable" interface. The SimpleContent class will be sufficient for
		 * most of your needs.
		 */
		
		Analyzable question = new SimpleContent("question-1", // Unique identifier
				"Who was elected President of South Africa in 1994?", "en");
		
		Analyzable passage = new SimpleContent("passage-1-1",
				"Chirac's stops in Namibia, Angola and Mozambique are the first visits ever by a "
				+ "French president. The late President Francois Mitterand visited South Africa "
				+ "soon after Nelson Mandela was elected president in 1994.");
		
		/**
		 * We instantiate two CASes for the different type of content
		 * 
		 * It is strongly advised to instantiate a small number of CASes and reuse them.
		 * However in order to avoid problems due to UIMA internal caching of annotations,
		 * it is always better to use separate CASes for the different type of content
		 * that we are handling at the same moment in a piece of code.
		 * Thus, if for a reranking task, you are working with a question related in
		 * different ways to two passages, and you are working with annotations after the
		 * analysis, do not reuse the same CAS for every piece of content, but allocate
		 * a CAS for the question, a CAS for the first passage and a CAS for the second one.
		 */
		JCas questionCas = JCasFactory.createJCas();
		JCas passageCas = JCasFactory.createJCas();
		
		/**
		 * Run the analysis. For the question we use the "parsing" pipeline
		 */
		analyzer.analyze(questionCas, question, "parsing");
		analyzer.analyze(passageCas, passage);
		
		List<String> tokens = new ArrayList<>();
		for(Token token : JCasUtil.select(questionCas, Token.class)) {
			tokens.add(token.getLemma().getValue());
		}
		System.out.println("[Lemmatized question]: " + Joiner.on(" ").join(tokens) + "\n\n");
		
		/***************************************************************************
		 * Let's explore some functionalities of the framework for handling trees
		 ***************************************************************************/
		
		/**
		 * We build a constituency tree from the annotated question.
		 * We use the factory method getConstituencyTree from the RichTree class
		 */		
		TokenTree questionTree = RichTree.getConstituencyTree(questionCas);
		
		/**
		 * We modify the root node
		 */
		questionTree.setValue("QUESTION-CONSTITUENCY-TREE");
		
		/**
		 * We instantiate a serializer for trees
		 */
		TreeSerializer ts = new TreeSerializer()
			.enableAdditionalLabels()
			.useSquareBrackets();
		
		/**
		 * We print the serialized tree, outputting lemmas in the leaves
		 */
		System.out.println(ts.serializeTree(questionTree, RichNode.OUTPUT_PAR_LEMMA) + "\n\n");
		
		/**
		 * Now we want to mark prepositions or subordinating conjunctions with an IGNORE label
		 * 
		 * We have several ways to do that:
		 * 1) we can take tokens, which are leaves with TreeUtil.getLeaves(...) and select
		 *    nodes' parents having label "IN"
		 * 2) we can take nodes with label "IN" using TreeUtil.getNodesWithLabel(...)
		 * 3) we can use a custom filtering criteria. Let's use this method.
		 */
		
		List<RichNode> inNodes = TreeUtil.getNodesWithFilter(questionTree, new Function<RichNode, Boolean>() {
			@Override
			public Boolean apply(RichNode node) {
				if(node.getValue().equals("IN") && !node.isLeaf()) {
					return true;
				} else {
					return false;
				}
			}
		});
		
		/**
		 * We put an additional label on the retrieved nodes
		 */
		for(RichNode inNode : inNodes) {
			inNode.addAdditionalLabel("IGNORE");
		}
		
		/**
		 * We serialize the tree, but now we use lowercase tokens as leaves output
		 */
		System.out.println(ts.serializeTree(questionTree, RichNode.OUTPUT_PAR_TOKEN_LOWERCASE) + "\n\n");
		
		/**
		 * Now we want to implement a slightly more complex selection filter
		 * Let's take the first word of compound proper nouns
		 */
		
		List<RichNode> fwNodes = TreeUtil.getNodesWithFilter(questionTree, new Function<RichNode, Boolean>() {
			@Override
			public Boolean apply(RichNode node) {
				if(node.getValue().equals("NNP")) {
					RichNode parent = node.getParent();
					// Always better to check if the node has really a parent
					if(parent != null) { 
						if( // if our node is the first...
							parent.getChildren().get(0) == node &&
							// and there are other children
							parent.getChildren().size() >= 2 &&
							// and the adjacent node is also a proper noun
							parent.getChildren().get(1).getValue().equals("NNP")) {
							
							return true;
						}
					}
				}
				return false;
			}
		});
		
		/**
		 * We put an additional label on the retrieved nodes
		 */
		for(RichNode fwNode : fwNodes) {
			fwNode.addAdditionalLabel("FW");
		}
		
		/**
		 * We serialize the tree
		 */
		System.out.println(ts.serializeTree(questionTree) + "\n\n");
		
		/**
		 * Let's print a POS+CHUNK tree of the passage
		 */		
		System.out.println(ts.serializeTree(RichTree.getPosChunkTree(passageCas)) + "\n\n");
		
		/**
		 * Now we use the similarity factory to extract some similarity measures
		 * between question and answer.
		 * 
		 * We instantiate the feature factory with its feature alphabet. The Alphabet
		 * parameter is handy for keeping a consistent feature space between train/test data.
		 */
		PairFeatureFactory pf = new PairFeatureFactory(new Alphabet());
		
		/**
		 * We compute the features between question and passage, using node lemmas
		 */
		FeatureVector fv = pf.getPairFeatures(questionCas, passageCas, RichNode.OUTPUT_PAR_LEMMA, new Stopwords());
		
		/**
		 * We print the features
		 */
		System.out.println(fv);
	}

}
