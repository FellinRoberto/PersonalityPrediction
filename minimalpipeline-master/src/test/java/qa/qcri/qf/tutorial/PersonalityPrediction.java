package qa.qcri.qf.tutorial;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
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

import au.com.bytecode.opencsv.CSVReader;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordParser;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordSegmenter;


public class PersonalityPrediction {

	public static final String POST_ANALYSIS = "POST_ANALYSIS";


	public static void main(String[] args) throws UIMAException, IOException {

		/**
		 * Instantiate a more complex multi-pipeline
		 */

		CSVReader X_train = new CSVReader(new FileReader("../dataset/X_train.csv"));
		CSVReader Y_train = new CSVReader(new FileReader("../dataset/y_train.csv"));
		CSVReader X_test = new CSVReader(new FileReader("../dataset/X_test.csv"));
		CSVReader Y_test = new CSVReader(new FileReader("../dataset/y_test.csv"));
		/*Scanner X_train = new Scanner(new File("../dataset/X_train.csv"), "ISO-8859-1");
		Scanner Y_train = new Scanner(new File("../dataset/y_train.csv"), "ISO-8859-1");
		Scanner X_test = new Scanner(new File("../dataset/X_test.csv"), "ISO-8859-1");
		Scanner Y_test = new Scanner(new File("../dataset/y_test.csv"), "ISO-8859-1");*/

		
		// TRAIN
		ArrayList<String> resultTrain = treeKernel(X_train, Y_train, 20, 10); // dataset, dataset, max number of word for each phrase delimited by dot, number of row used of the dataset(use big number to use entire dataset) 
		//String resultTrain = treeKernel(X_train, Y_train);
		
		String r=resultTrain.get(0);
		r = r.substring(0, r.length() - 1);// to delete the last /n
		try(  PrintWriter out = new PrintWriter( "TreeKernel/train1.dat" )  ){
			out.println( r );
		}
		
		r=resultTrain.get(1);
		r = r.substring(0, r.length() - 1);// to delete the last /n
		try(  PrintWriter out = new PrintWriter( "TreeKernel/train2.dat" )  ){
			out.println( r );
		}
		
		r=resultTrain.get(2);
		r = r.substring(0, r.length() - 1);// to delete the last /n
		try(  PrintWriter out = new PrintWriter( "TreeKernel/train3.dat" )  ){
			out.println( r );
		}
		
		r=resultTrain.get(3);
		r = r.substring(0, r.length() - 1);// to delete the last /n
		try(  PrintWriter out = new PrintWriter( "TreeKernel/train4.dat" )  ){
			out.println( r );
		}
		
		r=resultTrain.get(4);
		r = r.substring(0, r.length() - 1);// to delete the last /n
		try(  PrintWriter out = new PrintWriter( "TreeKernel/train5.dat" )  ){
			out.println( r );
		}
		
		// TEST
		ArrayList<String> resultTest = treeKernel(X_test, Y_test, 20, 5); // dataset, dataset, max number of word for each phrase delimited by dot, number of row used of the dataset(use big number to use entire dataset) 
		//String resultTrain = treeKernel(X_train, Y_train);
		
		r=resultTest.get(0);
		r = r.substring(0, r.length() - 1);// to delete the last /n
		try(  PrintWriter out = new PrintWriter( "TreeKernel/test1.dat" )  ){
			out.println( r );
		}
		
		r=resultTest.get(1);
		r = r.substring(0, r.length() - 1);// to delete the last /n
		try(  PrintWriter out = new PrintWriter( "TreeKernel/test2.dat" )  ){
			out.println( r );
		}
		
		r=resultTest.get(2);
		r = r.substring(0, r.length() - 1);// to delete the last /n
		try(  PrintWriter out = new PrintWriter( "TreeKernel/test3.dat" )  ){
			out.println( r );
		}
		
		r=resultTest.get(3);
		r = r.substring(0, r.length() - 1);// to delete the last /n
		try(  PrintWriter out = new PrintWriter( "TreeKernel/test4.dat" )  ){
			out.println( r );
		}
		
		r=resultTest.get(4);
		r = r.substring(0, r.length() - 1);// to delete the last /n
		try(  PrintWriter out = new PrintWriter( "TreeKernel/test5.dat" )  ){
			out.println( r );
		}
		
		//treeKernel(X_test, Y_test);





	}


	private static ArrayList<String> treeKernel(CSVReader X, CSVReader Y, int maxWord, int numberOfRowRead) throws UIMAException, IOException {
		X.readNext();
		Y.readNext();
		String r1="";
		String r2="";
		String r3="";
		String r4="";
		String r5="";
		ArrayList<String> result = new ArrayList<String>();
		Analyzer analyzer = instantiateTrecAnalyzer(new UIMANoPersistence());
		String [] nextLineX = null;
		
		int count = 0;
		while ((nextLineX = X.readNext()) != null && count<numberOfRowRead) {


			String x = nextLineX[0];

			boolean check=true;
			String[] split = x.split("\\.");
			for (String s: split) {           
		        //Do your stuff here
		        
		        if (s.trim().split("\\s+").length>maxWord) {
		        	System.out.println("ENTRATO!!!!\n \n \n"); 
		        	check=false;
		        	break;
		        }
		    }
			if (check) {

				Analyzable post = new SimpleContent("post", x);


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
				String[] prediction = Y.readNext();
				
				r1+=prediction[0]+" |BT| "+ts.serializeTree(questionTree, parameterList)+"  |ET|\n";
				r2+=prediction[1]+" |BT| "+ts.serializeTree(questionTree, parameterList)+"  |ET|\n";
				r3+=prediction[2]+" |BT| "+ts.serializeTree(questionTree, parameterList)+"  |ET|\n";
				r4+=prediction[3]+" |BT| "+ts.serializeTree(questionTree, parameterList)+"  |ET|\n";
				r5+=prediction[4]+" |BT| "+ts.serializeTree(questionTree, parameterList)+"  |ET|\n";
				
				count++;
				System.out.print(count+"\n");
			}
			else {
				Y.readNext();
			}
			
		}
		X.close();
		result.add(r1);
		result.add(r2);
		result.add(r3);
		result.add(r4);
		result.add(r5);
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