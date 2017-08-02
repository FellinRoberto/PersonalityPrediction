package qa.qcri.qf.tutorial;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.IOException;
import java.io.PrintWriter;

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


public class PersonalityPrediciton {

	public static final String POST_ANALYSIS = "POST_ANALYSIS";


	public static void main(String[] args) throws UIMAException, IOException {

		/**
		 * Instantiate a more complex multi-pipeline
		 */
		

		Scanner X_train = new Scanner(new File("../dataset/X_train2.csv"), "ISO-8859-1");
		Scanner Y_train = new Scanner(new File("../dataset/y_train.csv"), "ISO-8859-1");
		Scanner X_test = new Scanner(new File("../dataset/X_test.csv"), "ISO-8859-1");
		Scanner Y_test = new Scanner(new File("../dataset/y_test.csv"), "ISO-8859-1");
		String resultTrain = treeKernel(X_train, Y_train);
		
		try(  PrintWriter out = new PrintWriter( "TreeKernel/train.dat" )  ){
		    out.println( resultTrain );
		}
		//treeKernel(X_test, Y_test);
		
		



	}
	
	private static String treeKernel(Scanner X, Scanner Y) throws UIMAException {
		X.nextLine();
		Y.nextLine();
		String result2="";
		String result="";
		Analyzer analyzer = instantiateTrecAnalyzer(new UIMANoPersistence());
		while(X.hasNext()){
			
      
			String x = X.nextLine();
			Analyzable post = new SimpleContent("post", x);
			System.out.print(x+"\n");

			/**
			 * Instantiate two CASes for the different type of content
			 */
			JCas questionCas = JCasFactory.createJCas();

			/**
			 * Analyze the question and the passage with different sets of annotators
			 */
			analyzer.analyze(questionCas, post, POST_ANALYSIS);


			/**
			 * Set the output parameters for Tokens
			 */
			String parameterList = Joiner.on(",").join(
					new String[] { RichNode.OUTPUT_PAR_LEMMA,
							RichNode.OUTPUT_PAR_TOKEN_LOWERCASE });


			/**
			 * Build the trees from CASes
			 */		
			TokenTree questionTree = RichTree.getPosChunkTree(questionCas);


			/**
			 * Instantiate a tree serializer
			 */

			TreeSerializer ts = new TreeSerializer()
					.enableRelationalTags()
					.enableAdditionalLabels()
					.useRoundBrackets();

			/**
			 * Output the plain trees
			 */
			//System.out.println("Trees with lowercase lemmas");
			String prediction = Y.nextLine().split(",")[0];
			result+=prediction+" |BT| "+ts.serializeTree(questionTree, parameterList)+"  |ET|\n";

		}
		X.close();
		
		return result;
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
		ae.addAE(stanfordSegmenter, POST_ANALYSIS)
		.addAE(stanfordPosTagger, POST_ANALYSIS)
		.addAE(stanfordLemmatizer, POST_ANALYSIS)
		.addAE(illinoisChunker, POST_ANALYSIS)
		.addAE(stanfordNamedEntityRecognizer, POST_ANALYSIS)
		.addAE(stanfordParser, POST_ANALYSIS);

		return ae;
	}

}

