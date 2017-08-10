package qa.qcri.qf.tutorial;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.util.Collection;
import java.util.Map;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import qa.qcri.qf.pipeline.Analyzer;
import qa.qcri.qf.pipeline.retrieval.Analyzable;
import qa.qcri.qf.pipeline.retrieval.SimpleContent;
import qa.qcri.qf.pipeline.serialization.UIMAFilePersistence;
import qa.qcri.qf.tutorial.LowercaseAnnotator;
import qa.qcri.qf.type.Lowercase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;

public class LowercaseExample {
	
	public static void main(String[] args) throws UIMAException {
		/**
		 * Instantiate an analyzer with a persistence layer
		 */
		Analyzer analyzer = new Analyzer(new UIMAFilePersistence("CASes/tutorial"));
		
		/**
		 * Instantiate a segmenter which annotates sentences and tokens
		 */
		AnalysisEngine breakIteratorSegmenter = AnalysisEngineFactory.createEngine(
				createEngineDescription(BreakIteratorSegmenter.class));
		
		/**
		 * Instantiate our LowercaseAnnotator
		 */
		AnalysisEngine lowercaseAnnotator = AnalysisEngineFactory
				.createEngine(createEngineDescription(LowercaseAnnotator.class,
						LowercaseAnnotator.PARAM_LANGUAGE, "en"));
		
		/**
		 * Add these annotators to the main analysis pipeline
		 */
		analyzer.addAE(breakIteratorSegmenter)
			.addAE(lowercaseAnnotator);
		
		/**
		 * Create an analyzable content providing its identifier for the persistence layer
		 */
		Analyzable content = new SimpleContent("lowercase-example", "Qatar Foundation is a large, "
				+ "welcoming, and culturally diverse organization. A shared Dress Code Policy "
				+ "helps us present a consistent, professional and respectful image, both to "
				+ "each other and to external stakeholders.");
		
		/**
		 * Create the CAS which will store the results of the analysis
		 */
		JCas cas = JCasFactory.createJCas();
		
		/**
		 * Run the analysis pipeline
		 */
		analyzer.analyze(cas, content);
		
		/**
		 * Build an index of Tokens and the Lowercase annotations which are
		 * covered by the Tokens. In this case we have one to one correspondence
		 */	
		Map<Token, Collection<Lowercase>> token2Lowercase =
				JCasUtil.indexCovering(cas, Token.class, Lowercase.class);
		
		/**
		 * Iterate on the Tokens
		 */
		for(Token token : JCasUtil.select(cas, Token.class)) {
			/**
			 * Check if the current Token has an associated Lowercase annotation
			 */
			if(token2Lowercase.containsKey(token)) {
				/**
				 * Retrieve the (single) annotation associated to this Token
				 */
				Lowercase lowercase = token2Lowercase.get(token).iterator().next();
				
				/**
				 * Print some information related to this annotation
				 */
				System.out.println(lowercase.getCoveredText() + " "
						+ lowercase.getUppercaseVersion());
			}
			
		}
	}
}
