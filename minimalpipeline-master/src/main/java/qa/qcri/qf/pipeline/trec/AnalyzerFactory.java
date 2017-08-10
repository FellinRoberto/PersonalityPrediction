package qa.qcri.qf.pipeline.trec;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qa.qcri.qf.annotators.IllinoisChunker;
import qa.qcri.qf.pipeline.Analyzer;
import qa.qcri.qf.pipeline.serialization.UIMAPersistence;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordParser;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordSegmenter;

public abstract class AnalyzerFactory {

	public static final String QUESTION_ANALYSIS = "QUESTION_ANALYSIS";
	private static final Logger logger = LoggerFactory
			.getLogger(AnalyzerFactory.class);

	public static Analyzer newTrecPipeline(String lang,
			UIMAPersistence persistence) throws UIMAException {

		if (lang == null) {
			throw new NullPointerException("The String parameter lang is null");
		}

		if (persistence == null) {
			throw new NullPointerException(
					"The UIMAPersistence parameter persistence is null");
		}

		Analyzer analyzer = null;

		switch (lang) {
		case "en":
			logger.info("instantiating en analyzer");
			analyzer = newTrecPipelineEnAnalyzer(persistence);
			break;
		default:
			logger.info("instantiating en analyzer (default)");
			analyzer = newTrecPipelineEnAnalyzer(persistence);
			break;
		}

		return analyzer;
	}

	private static Analyzer newTrecPipelineEnAnalyzer(
			UIMAPersistence persistence) throws UIMAException {
		assert persistence != null;

		Analyzer ae = new Analyzer(persistence);

		AnalysisEngine stanfordSegmenter = AnalysisEngineFactory
				.createEngine(createEngineDescription(StanfordSegmenter.class));

		/**
		 * StanfordPosTagger puts wrong POS-tags on parentheses
		 * The OpenNlpPosTagger is our choice for now.
		 */
		AnalysisEngine posTagger = AnalysisEngineFactory
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


		ae.addAE(stanfordSegmenter)
			.addAE(posTagger)
			.addAE(stanfordLemmatizer)
			.addAE(illinoisChunker)
			.addAE(stanfordNamedEntityRecognizer);

		ae.addAE(stanfordSegmenter, QUESTION_ANALYSIS)
			.addAE(posTagger, QUESTION_ANALYSIS)
			.addAE(stanfordLemmatizer, QUESTION_ANALYSIS)
			.addAE(illinoisChunker, QUESTION_ANALYSIS)
			.addAE(stanfordNamedEntityRecognizer, QUESTION_ANALYSIS)
			.addAE(stanfordParser, QUESTION_ANALYSIS);

		return ae;
	}


}
