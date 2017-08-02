package qa.qcri.qf.datagen.rr;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;

import qa.qcri.qf.datagen.DataObject;
import qa.qcri.qf.datagen.DataPair;
import qa.qcri.qf.datagen.Pairer;
import qa.qcri.qf.features.FeaturesUtil;
import qa.qcri.qf.features.PairFeatureFactory;
import qa.qcri.qf.fileutil.FileManager;
import qa.qcri.qf.pipeline.Analyzer;
import qa.qcri.qf.pipeline.GenericPipeline;
import qa.qcri.qf.pipeline.retrieval.SimpleContent;
import qa.qcri.qf.treemarker.MarkTreesOnRepresentation;
import qa.qcri.qf.trees.TokenTree;
import qa.qcri.qf.trees.TreeSerializer;
import qa.qcri.qf.trees.nodes.RichNode;
import qa.qcri.qf.trees.providers.TokenTreeProvider;
import qa.qcri.qf.trees.pruning.PosChunkPruner;
import qa.qcri.qf.trees.pruning.strategies.PruneIfNodeIsWithoutMetadata;
import util.Pair;
import util.Stopwords;
import cc.mallet.types.FeatureVector;

import com.google.common.base.Function;
import com.google.common.base.Joiner;

/**
 * 
 * Generates the training data for reranking
 * 
 * TODO: the logic about retrieving trees, the marking and producing the token
 * representation should be factored out to a central "settings" class
 * The logic for generating the res lines should be factored out
 */
public class RerankingTrainOnlyRel implements Reranking {

	public static final String DEFAULT_OUTPUT_TRAIN_FILE = "svm.train";
	
	public static final String DEFAULT_OUTPUT_TRAIN_RES_FILE = "svm.train.res";

	private FileManager fm;

	private String outputDir;

	private String outputFile;
	
	private String outputResFile;

	private Analyzer ae;

	private TreeSerializer ts;

	private PairFeatureFactory pairFeatureFactory;

	private TokenTreeProvider tokenTreeProvider;
	
	private MarkTreesOnRepresentation marker;
	
	private PosChunkPruner pruner;
	
	private Stopwords stopwords;
	
	private Function<List<RichNode>, List<Boolean>> pruningCriteria;
	
	private JCas questionCas;
	private JCas leftCandidateCas;
	private JCas rightCandidateCas;

	private String parameterList;

	public RerankingTrainOnlyRel(FileManager fm, String outputDir, Analyzer ae,
			TreeSerializer ts, PairFeatureFactory pairFeatureFactory, TokenTreeProvider tokenTreeProvider,
			MarkTreesOnRepresentation marker, Stopwords stopwords) throws UIMAException {
		this.fm = fm;
		this.outputDir = outputDir;
		this.outputFile = outputDir + DEFAULT_OUTPUT_TRAIN_FILE;
		this.outputResFile = outputDir + DEFAULT_OUTPUT_TRAIN_RES_FILE;
		this.ae = ae;

		this.ts = ts;

		this.pairFeatureFactory = pairFeatureFactory;
		
		this.tokenTreeProvider = tokenTreeProvider;
		
		this.marker = marker;
		
		this.stopwords = stopwords;
		
		this.pruner = new PosChunkPruner(-1);
		this.pruningCriteria = new PruneIfNodeIsWithoutMetadata(RichNode.REL_KEY);

		this.questionCas = JCasFactory.createJCas();
		this.leftCandidateCas = JCasFactory.createJCas();
		this.rightCandidateCas = JCasFactory.createJCas();

		this.parameterList = "";
	}

	/**
	 * Sets the outputFile. If the method is not called then the default output
	 * file name is used
	 * 
	 * @param outputFile
	 */
	public void setOutputFile(String outputFile) {
		this.outputFile = this.outputDir + outputFile;
		this.outputResFile = this.outputDir + outputFile + ".res";
	}

	@Override
	public void generateData(DataObject questionObject,
			List<DataObject> candidateObjects) {
		List<DataPair> pairs = pairQuestionWithCandidates(questionObject,
				candidateObjects);

		
		this.ae.analyze(this.questionCas,
				new SimpleContent(questionObject.getId(), ""));
	
		List<Pair<DataPair, DataPair>> trainingPairs = Pairer.pair(pairs);

		for (Pair<DataPair, DataPair> trainingPair : trainingPairs) {
			DataPair leftPair = trainingPair.getA();
			DataPair rightPair = trainingPair.getB();

			DataObject lCandidate = leftPair.getB();
			DataObject rCandidate = rightPair.getB();			
			
			TokenTree leftQuestionTree = this.tokenTreeProvider.getTree(this.questionCas);
			TokenTree rightQuestionTree = this.tokenTreeProvider.getTree(this.questionCas);
						
			this.ae.analyze(this.leftCandidateCas,
					new SimpleContent(lCandidate.getId(), ""));
			this.ae.analyze(this.rightCandidateCas,
					new SimpleContent(rCandidate.getId(), ""));

			TokenTree leftCandidateTree = this.tokenTreeProvider.getTree(this.leftCandidateCas);
			TokenTree rightCandidateTree = this.tokenTreeProvider.getTree(this.rightCandidateCas);

			/**
			 * Mark the trees with the relational tag on the nodes sharing the same lemmas.
			 * Stopwords are not considered for matchings
			 */
			this.marker.markTrees(leftQuestionTree, leftCandidateTree, this.parameterList);
			this.marker.markTrees(rightQuestionTree, rightCandidateTree, this.parameterList);
			
			/**
			 * Perform the pruning
			 */			
			//this.pruner.prune(leftCandidateTree, this.pruningCriteria);		
			//this.pruner.prune(rightCandidateTree, this.pruningCriteria);
			
			/**
			 * Produce the feature vectors
			 */
			FeatureVector leftFv = this.pairFeatureFactory.getPairFeatures(
					this.questionCas, this.leftCandidateCas, this.parameterList, this.stopwords);
			FeatureVector rightFv = this.pairFeatureFactory.getPairFeatures(
					this.questionCas, this.rightCandidateCas, this.parameterList, this.stopwords);

			StringBuffer sb = new StringBuffer(1024 * 4);
			String label = leftPair.isPositive() ? "+1" : "-1";
			sb.append(label);
			sb.append(" |BT| ");
			sb.append(this.ts.serializeTree(leftQuestionTree,
					this.parameterList));
			sb.append(" |BT| ");
			sb.append(this.ts.serializeTree(leftCandidateTree,
					this.parameterList));
			sb.append(" |BT| ");
			sb.append(this.ts.serializeTree(rightQuestionTree,
					this.parameterList));
			sb.append(" |BT| ");
			sb.append(this.ts.serializeTree(rightCandidateTree,
					this.parameterList));
			sb.append(" |ET| ");
			sb.append(FeaturesUtil.serialize(leftFv));
			sb.append(" |BV| ");
			sb.append(FeaturesUtil.serialize(rightFv));
			sb.append(" |EV| ");

			this.fm.writeLn(this.outputFile, sb.toString());
			
			sb = new StringBuffer(1024);
			
			sb.append(Joiner.on(" ").join(
					questionObject.getId(),
					lCandidate.getId(),
					rCandidate.getId(),
					lCandidate.getMetadata().get(GenericPipeline.SEARCH_ENGINE_POSITION_KEY),
					rCandidate.getMetadata().get(GenericPipeline.SEARCH_ENGINE_POSITION_KEY),
					label));
			
			this.fm.writeLn(this.outputResFile, sb.toString());
		}
	}

	/**
	 * Set the list of parameters used to retrieve the token representation
	 * 
	 * @param parameterList
	 * @return
	 */
	public RerankingTrainOnlyRel setParameterList(String parameterList) {
		this.parameterList = parameterList;
		return this;
	}

	/**
	 * Produces a list of DataPair. The right part of the pair is the question
	 * DataObject, the left part of the pair is the candidate DataObject
	 * 
	 * @param question
	 * @param candidates
	 * @return the list of DataPair
	 */
	private List<DataPair> pairQuestionWithCandidates(DataObject question,
			List<DataObject> candidates) {

		List<DataPair> pairs = new ArrayList<>();

		for (DataObject candidate : candidates) {
			DataPair pair = new DataPair(
					candidate.getLabel(), 
					question.getId() + "-" + candidate.getId(),
					DataObject.newFeaturesMap(),
					DataObject.newMetadataMap(), 
					question, 
					candidate);
			pairs.add(pair);
			/*
			pairs.add(new DataPair(candidate.getLabel(), question.getId() + "-"
					+ candidate.getId(), DataObject.newFeaturesMap(),
					DataObject.newMetadataMap(), question, candidate));
			*/
		}
		
		/*
		for (DataObject candidate : candidates) {					
			pairs.add(new DataPair(candidate.getLabel(), question.getId() + "-"
					+ candidate.getId(), DataObject.newFeaturesMap(),
					DataObject.newMetadataMap(), question, candidate));
		}
		**/

		return pairs;
	}
}
