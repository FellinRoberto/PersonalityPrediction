package qa.qcri.qf.applications;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpDependencyParser;
import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.maltparser.MaltParser;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordSegmenter;
import qa.qcri.qf.pipeline.Analyzer;
import qa.qcri.qf.pipeline.retrieval.SimpleContent;
import qa.qcri.qf.pipeline.serialization.UIMAFilePersistence;
import qa.qcri.qf.trees.RichTree;
import qa.qcri.qf.trees.TreeSerializer;
import qa.qcri.qf.trees.nodes.RichNode;

public class LctConverter {
	public static void main(String[] args) throws UIMAException {
		String question = "What instrument does Hendrix play?";
		question = "What is the oldest profession?";
		
		Analyzer analyzer = new Analyzer(new UIMAFilePersistence("CASes/lct"));
		
		AnalysisEngine segmenter = AnalysisEngineFactory.createEngine(
				createEngineDescription(ClearNlpSegmenter.class));
		
		AnalysisEngine tagger = AnalysisEngineFactory.createEngine(
				createEngineDescription(ClearNlpPosTagger.class));
		
		AnalysisEngine lemmatizer = AnalysisEngineFactory.createEngine(
				createEngineDescription(ClearNlpLemmatizer.class));
		
		AnalysisEngine parser = AnalysisEngineFactory.createEngine(
				createEngineDescription(ClearNlpDependencyParser.class));
		
		analyzer.addAE(segmenter)
			.addAE(tagger)
			.addAE(lemmatizer)
			.addAE(parser);
		
		JCas cas = JCasFactory.createJCas();
		
		analyzer.analyze(cas, new SimpleContent("question", question));
		
		TreeSerializer ts = new TreeSerializer().useSquareBrackets();

		System.out.println(ts.serializeTree(RichTree.getDependencyTree(cas),
				RichNode.OUTPUT_PAR_SEMANTIC_KERNEL + "," + RichNode.OUTPUT_PAR_TOKEN_LOWERCASE));
		
		System.out.println(ts.serializeTree(RichTree.getLctTree(cas),
				RichNode.OUTPUT_PAR_SEMANTIC_KERNEL + "," + RichNode.OUTPUT_PAR_TOKEN_LOWERCASE));
	}
}
