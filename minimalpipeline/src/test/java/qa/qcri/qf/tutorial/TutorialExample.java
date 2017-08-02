package qa.qcri.qf.tutorial;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;

import qa.qcri.qf.annotators.IllinoisChunker;
import qa.qcri.qf.features.PairFeatureFactory;
import qa.qcri.qf.pipeline.Analyzer;
import qa.qcri.qf.pipeline.retrieval.Analyzable;
import qa.qcri.qf.pipeline.retrieval.SimpleContent;
import qa.qcri.qf.pipeline.serialization.UIMAFilePersistence;
import qa.qcri.qf.pipeline.serialization.UIMANoPersistence;
import qa.qcri.qf.pipeline.serialization.UIMAPersistence;
import qa.qcri.qf.treemarker.MarkTreesOnRepresentation;
import qa.qcri.qf.treemarker.MarkTwoAncestors;
import qa.qcri.qf.trees.RichTree;
import qa.qcri.qf.trees.TokenTree;
import qa.qcri.qf.trees.TreeSerializer;
import qa.qcri.qf.trees.nodes.RichNode;
import qa.qcri.qf.trees.pruning.PosChunkPruner;
import qa.qcri.qf.trees.pruning.strategies.PruneIfNodeIsWithoutMetadata;
import util.Stopwords;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;

import com.google.common.base.Joiner;

import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordParser;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordSegmenter;

public class TutorialExample {
	
	public static final String QUESTION_ANALYSIS = "QUESTION_ANALYSIS";
	
	public static final String STOPWORDS_EN_PATH = "resources/stoplist-en.txt";
	
	public static void main(String[] args) throws UIMAException, IOException {
		
		/**
		 * Instantiate a more complex multi-pipeline
		 */
		Analyzer analyzer = instantiateTrecAnalyzer(new UIMANoPersistence());
		
		Analyzable question = new SimpleContent("tutorial-question",
				"Who was elected President of South Africa in 1994?");
		
		Analyzable passage = new SimpleContent("tutorial-passage",
				"Chirac's stops in Namibia, Angola and Mozambique are the first visits ever by a "
				+ "French president. The late President Francois Mitterand visited South Africa "
				+ "soon after Nelson Mandela was elected president in 1994.");
		
		/**
		 * Instantiate two CASes for the different type of content
		 */
		JCas questionCas = JCasFactory.createJCas();
		JCas passageCas = JCasFactory.createJCas();
		
		/**
		 * Analyze the question and the passage with different sets of annotators
		 */
		analyzer.analyze(questionCas, question, QUESTION_ANALYSIS);
		analyzer.analyze(passageCas, passage);
		
		/**
		 * Set the output parameters for Tokens
		 */
		String parameterList = Joiner.on(",").join(
				new String[] { RichNode.OUTPUT_PAR_LEMMA,
						RichNode.OUTPUT_PAR_TOKEN_LOWERCASE });
			
		/**
		 * Compute some features using the default ones instantiated by the factory
		 */
		PairFeatureFactory pf = new PairFeatureFactory(new Alphabet());
		FeatureVector fv = pf.getPairFeatures(questionCas, passageCas, parameterList, new Stopwords());	
		
		System.out.println(fv);
		
		/**
		 * Build the trees from CASes
		 */		
		TokenTree questionTree = RichTree.getPosChunkTree(questionCas);
		TokenTree passageTree = RichTree.getPosChunkTree(passageCas);
		
		/**
		 * Instantiate a tree serializer
		 */
		
		TreeSerializer ts = new TreeSerializer()
			.enableRelationalTags()
			.enableAdditionalLabels()
			.useSquareBrackets();
		
		/**
		 * Output the plain trees
		 */
		
		System.out.println("Trees with lowercase lemmas");
		System.out.println(ts.serializeTree(questionTree, parameterList));
		System.out.println(ts.serializeTree(passageTree, parameterList));
		
		/**
		 *  Instantiate a marker for adding RELATIONAL information to the trees
		 */
		
		MarkTreesOnRepresentation marker = new MarkTreesOnRepresentation(
				new MarkTwoAncestors()).useStopwords(STOPWORDS_EN_PATH);
		
		/**
		 * Mark the trees
		 */
		marker.markTrees(questionTree, passageTree, parameterList);
		
		System.out.println("\nTrees with RELATIONAL information");
		System.out.println(ts.serializeTree(questionTree, parameterList));
		System.out.println(ts.serializeTree(passageTree, parameterList));
		
		
		/**
		 * Applying pruning with radius = 2 to the passage tree.
		 * Chunk nodes without RELATIONAL information distant two chunk nodes
		 * from the ones having RELATIONAL information, are pruned
		 */
		
		PosChunkPruner pruner = new PosChunkPruner(2);
		pruner.prune(passageTree, new PruneIfNodeIsWithoutMetadata(RichNode.REL_KEY));
		
		System.out.println("\nPrevious tree with pruning");
		System.out.println(ts.serializeTree(questionTree, parameterList));
		System.out.println(ts.serializeTree(passageTree, parameterList));
	}
	
	private static Analyzer instantiateTrecAnalyzer(
			UIMAPersistence persistence) throws UIMAException {
		assert persistence != null;

		Analyzer ae = new Analyzer(persistence);

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

		AnalysisEngine stanfordNamedEntityRecognizer = AnalysisEngineFactory
				.createEngine(createEngineDescription(StanfordNamedEntityRecognizer.class,
						StanfordNamedEntityRecognizer.PARAM_LANGUAGE, "en",
						StanfordNamedEntityRecognizer.PARAM_VARIANT, "muc.7class.distsim.crf"));

		/**
		 * Pipeline for passages analysis
		 */
		ae.addAE(stanfordSegmenter)
			.addAE(stanfordPosTagger)
			.addAE(stanfordLemmatizer)
			.addAE(illinoisChunker)
			.addAE(stanfordNamedEntityRecognizer);

		/**
		 * Pipeline for question analysis
		 */
		ae.addAE(stanfordSegmenter, QUESTION_ANALYSIS)
			.addAE(stanfordPosTagger, QUESTION_ANALYSIS)
			.addAE(stanfordLemmatizer, QUESTION_ANALYSIS)
			.addAE(illinoisChunker, QUESTION_ANALYSIS)
			.addAE(stanfordNamedEntityRecognizer, QUESTION_ANALYSIS)
			.addAE(stanfordParser, QUESTION_ANALYSIS);

		return ae;
	}

}
