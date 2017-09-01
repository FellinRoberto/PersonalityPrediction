package qa.qcri.qf.tutorial;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.tika.exception.TikaException;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.xml.sax.SAXException;

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
import com.vdurmont.emoji.EmojiParser;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordParser;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordSegmenter;

import org.apache.tika.exception.TikaException;
import org.apache.tika.language.LanguageIdentifier;
import org.xml.sax.SAXException;

public class PersonalityPrediction {

	public static final String POST_ANALYSIS = "POST_ANALYSIS";


	public static void main(String[] args) throws UIMAException, IOException, SAXException, TikaException {

		/**
		 * Instantiate a more complex multi-pipeline
		 */


		CSVReader X_train = new CSVReader(new FileReader("../dataset/X_train.csv"));
		CSVReader Y_train = new CSVReader(new FileReader("../dataset/y_train.csv"));
		CSVReader X_test = new CSVReader(new FileReader("../dataset/x_validation.csv"));
		CSVReader Y_test = new CSVReader(new FileReader("../dataset/y_validation.csv"));
		//Scanner X_train = new Scanner(new File("../dataset/X_train.csv"), "ISO-8859-1");

		// TRAIN
		int i=1;


		ArrayList<String> resultTrain = treeKernel(0, X_train, Y_train, 15, 20000, 4, 3); // dataset, dataset, max number of word for each phrase delimited by dot, number of row used of the dataset(use big number to use entire dataset), max number of phrase


		for (String r: resultTrain) {        
			r = r.substring(0, r.length() - 1);// to delete the last /n
			try(  PrintWriter out = new PrintWriter( "TreeKernel/train"+i+".dat" )  ){
				out.println( r );
			}
			i++;
		}

		// TEST
		ArrayList<String> resultTest = treeKernel(1, X_test, Y_test, 15, 15000, 4, 3); // dataset, dataset, max number of word for each phrase delimited by dot, number of row used of the dataset(use big number to use entire dataset), max number of phrase
		i=1;
		for (String r: resultTest) {        
			r = r.substring(0, r.length() - 1);// to delete the last /n
			try(  PrintWriter out = new PrintWriter( "TreeKernel/test"+i+".dat" )  ){
				out.println( r );
			}
			i++;
		}


		/*String str = "An 😀awesome 😃string with a few 😉emojis!";
		String result = EmojiParser.parseToAliases(str);
		System.out.println(result);*/
	}

	private static ArrayList<String> treeKernel(int w, CSVReader X, CSVReader Y, int maxWord, int numberOfRowRead, int maxPhrase, int minWord) throws UIMAException, IOException, SAXException, TikaException {
		X.readNext();
		Y.readNext();
		String r1="";
		String r2="";
		String r3="";
		String r4="";
		String r5="";
		CSVWriter writerTest = null;
		CSVWriter writerXTest = null;
		CSVWriter writerXTrain = null;
		CSVWriter writerTrain = null;
		if (w==1) {
			writerTest = new CSVWriter(new FileWriter("TreeKernel/y_testNew.csv",false),',','\0');
			writerXTest = new CSVWriter(new FileWriter("TreeKernel/x_testNew.csv"));
		
		}else {
			writerXTrain = new CSVWriter(new FileWriter("TreeKernel/x_trainNew.csv"));
			writerTrain = new CSVWriter(new FileWriter("TreeKernel/y_trainNew.csv",false),',','\0');
		}

		ArrayList<String> result = new ArrayList<String>();
		Analyzer analyzer = instantiateTrecAnalyzer(new UIMANoPersistence());
		String [] nextLineX = null;

		int count = 0;
		while ((nextLineX = X.readNext()) != null && count<numberOfRowRead) {

			String x = nextLineX[0];
			x=x.replaceAll("\\.+","."); // delete all dots and transform into a single dot
			String before = x;
			x= parserEmoji (x);
			System.out.println("Before: " + before);
			if (!x.equals(before)) {

				System.out.println(" After: " + x);
			}

			boolean check=true;
			String[] split = x.split("\\.");

			if (split.length>maxPhrase) {
				check=false;
			}
			for (String s: split) {           

				// check if there are phrase with more that maxWord word
				if (s.trim().split("\\s+").length>maxWord || s.trim().split("\\s+").length<minWord) {
					System.out.println("ENTRATO!!!!\n \n \n"); 
					check=false;
					break;
				}
			}

			LanguageIdentifier identifier = new LanguageIdentifier(x);
			String language = identifier.getLanguage();


			String[] prediction = Y.readNext();
			if (check && language.equals("en")) {
				try {
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


					//(ROOT)
					String treekernel = ts.serializeTree(questionTree, parameterList);
					if (!treekernel.equals("(ROOT)")){
						r1+=prediction[0]+" |BT| "+treekernel+" |ET|\n";
						r2+=prediction[1]+" |BT| "+treekernel+" |ET|\n";
						r3+=prediction[2]+" |BT| "+treekernel+" |ET|\n";
						r4+=prediction[3]+" |BT| "+treekernel+" |ET|\n";
						r5+=prediction[4]+" |BT| "+treekernel+" |ET|\n";

						//String[] entries = prediction[0]+"#"+prediction[0]+"#"+prediction[3]+"#"+prediction[4]+"#"+prediction[5].split("#");
						String [] out = new String[1];
						out[0]=x;
						if (w==1) {
							writerTest.writeNext(prediction);
							//String [] country = x.split("#");
							
							writerXTest.writeNext(out);
						}

						else {
							writerTrain.writeNext(prediction);
							
							
							writerXTrain.writeNext(out);

						}
						//System.out.print("out: "+out[0]+"\n");
						count++;
						System.out.print(count+"\n");
					} else {
						System.out.print("ROOT: "+x+" "+treekernel);
					}
				}catch (OutOfMemoryError e) {
					System.err.println("Caught OutOfMemoryError: " + e.getMessage());
				}
			}
			else {

			}

		}
		X.close();
		if (w==1) {
			writerXTest.close();
			writerTest.close();}
		else {
		
		writerXTrain.close();
		writerTrain.close();
		}
		result.add(r1);
		result.add(r2);
		result.add(r3);
		result.add(r4);
		result.add(r5);
		return result;
	}

	private static String parserEmoji(String x) {
		try {
			byte[] utf8Bytes = x.getBytes("UTF-8");

			x = new String(utf8Bytes, "UTF-8");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Pattern unicodeOutliers = Pattern.compile("[^\\x00-\\x7F]",
				Pattern.UNICODE_CASE | Pattern.CANON_EQ
				| Pattern.CASE_INSENSITIVE);
		Matcher unicodeOutlierMatcher = unicodeOutliers.matcher(x);

		//System.out.println("Before: " + x);
		x = unicodeOutlierMatcher.replaceAll(" ");
		// System.out.println("After: " + x);
		return x;
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
