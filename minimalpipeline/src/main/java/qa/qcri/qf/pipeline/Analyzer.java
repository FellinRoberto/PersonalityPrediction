package qa.qcri.qf.pipeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qa.qcri.qf.pipeline.retrieval.Analyzable;
import qa.qcri.qf.pipeline.serialization.UIMANoPersistence;
import qa.qcri.qf.pipeline.serialization.UIMAPersistence;

public class Analyzer {

	public static final String MAIN_AES_LIST = "MAIN_AES_LIST";

	private Map<String, List<AnalysisEngine>> idToAEs;

	private UIMAPersistence persistence;

	private final Logger logger = LoggerFactory.getLogger(Analyzer.class);

	/**
	 * Constructor for Analyzer with no serialization
	 * 
	 * @throws UIMAException
	 */
	public Analyzer() throws UIMAException {
		this(new UIMANoPersistence());
	}

	/**
	 * Constructor for Analyzer
	 * 
	 * @param persistence
	 *            the object implementing the serialization mechanism
	 * @throws UIMAException
	 */
	public Analyzer(UIMAPersistence persistence) throws UIMAException {
		this.persistence = persistence;
		this.idToAEs = new HashMap<>();
		this.idToAEs.put(MAIN_AES_LIST, new ArrayList<AnalysisEngine>());
	}

	/**
	 * Adds an analysis engine to analysis engine list specified by the given id
	 * If the list with such id does not exist it is instantiated
	 * 
	 * @param ae
	 *            an analysis engine
	 * @param aesListid
	 *            the id of the list of analysis engine
	 * @return the Analyzer object instance for chaining
	 */
	public Analyzer addAE(AnalysisEngine ae, String aesListId) {	
		if(!this.idToAEs.containsKey(aesListId)) {
			this.idToAEs.put(aesListId, new ArrayList<AnalysisEngine>());
		}
		
		this.idToAEs.get(aesListId).add(ae);
		
		return this;
	}

	/**
	 * Adds an analysis engine to the main analysis engine list
	 * 
	 * @param ae
	 *            an analysis engine
	 * @return the Analyzer object instance for chaining
	 */
	public Analyzer addAE(AnalysisEngine ae) {
		return addAE(ae, MAIN_AES_LIST);
	}

	/**
	 * Carries out the analysis on a piece of content
	 * 
	 * @param cas
	 *            the CAS used to store the annotations
	 * @param analyzable
	 *            an Analyzable piece of content
	 */
	public void analyze(JCas cas, Analyzable analyzable) {
		analyze(cas, analyzable, MAIN_AES_LIST);
	}

	/**
	 * 
	 * @param cas
	 *            the CAS used to store the annotations
	 * @param analyzable
	 *            an Analyzable piece of content
	 * @param aesId
	 *            the id of the analysis engines list to use
	 */
	public void analyze(JCas cas, Analyzable analyzable, String aesListId) {
		/**
		 * Makes sure it works with a clean CAS
		 */
		cas.reset();

		/**
		 * Fills the CAS with the content to analyze
		 */
		cas.setDocumentText(analyzable.getContent());
		cas.setDocumentLanguage(analyzable.getLanguage());

		analyzeWithSerialization(cas, analyzable, aesListId);
	}
	
	private void analyzeWithSerialization(JCas cas, Analyzable analyzable,
			String aesListId) {
		/**
		 * Retrieves the content id, vital for the serialization mechanism to
		 * retrieve the content
		 */
		String id = analyzable.getId();

		if (this.persistence.isAlreadySerialized(id)) {
			this.persistence.deserialize(cas, id);
		} else {
			List<AnalysisEngine> aesList = this.idToAEs.get(aesListId);
			
			/**
			 * Fallback to main analysis engine list
			 */
			if(aesList == null) {
				aesList = this.idToAEs.get(MAIN_AES_LIST);
				logger.warn("AE list with id " + aesListId + " not found."
						+ " Falling back to the main analysis engine list.");
			}
			
			for (AnalysisEngine ae : aesList) {
				try {
					SimplePipeline.runPipeline(cas, ae);
				} catch (AnalysisEngineProcessException e) {
					logger.error("Failed to run annotator {} on content: {}",
							ae.getAnalysisEngineMetaData().getName(),
							cas.getDocumentText());
					e.printStackTrace();
				}
			}

			this.persistence.serialize(cas, id);
		}
	}

	/**
	 * Changes the persistence submodule
	 * 
	 * @param persistence
	 */
	public void setPersistence(UIMAPersistence persistence) {
		this.persistence = persistence;
	}

	/**
	 * Creates an analysis engine from its description and adds it to the
	 * main analysis engine list
	 * 
	 * @param engineDescription
	 * @return the Analyzer object instance for chaining
	 * @throws ResourceInitializationException 
	 */
	public Analyzer addAEDesc(AnalysisEngineDescription engineDescription) throws ResourceInitializationException {
		return this.addAE(AnalysisEngineFactory.createEngine(
				engineDescription));
	}

}
