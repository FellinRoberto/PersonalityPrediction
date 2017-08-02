package qa.qcri.qf.datagen.rr;

import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;

import qa.qcri.qf.datagen.DataObject;
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
import util.Stopwords;
import cc.mallet.types.FeatureVector;

import com.google.common.base.Function;
import com.google.common.base.Joiner;

/**
 * 
 * Generates the test data for reranking
 * 
 * TODO: the logic about marking and producing the token
 * representation should be factored out to a central "settings" class
 * The logic for generating the res lines should be factored out
 */
public class RerankingTestOnlyRel implements Reranking {

	public static final String DEFAULT_OUTPUT_TEST_FILE = "svm.test";

	public static final String DEFAULT_OUTPUT_TEST_RES_FILE = "svm.test.res";

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

	private JCas candidateCas;

	private String parameterList;

	public RerankingTestOnlyRel(FileManager fm, String outputDir, Analyzer ae,
			TreeSerializer ts, PairFeatureFactory pairFeatureFactory,
			TokenTreeProvider tokenTreeProvider, MarkTreesOnRepresentation marker, Stopwords stopwords) throws UIMAException {
		this.fm = fm;
		this.outputDir = outputDir;
		this.outputFile = outputDir + DEFAULT_OUTPUT_TEST_FILE;
		this.outputResFile = outputDir + DEFAULT_OUTPUT_TEST_RES_FILE;
		this.ae = ae;

		this.ts = ts;

		this.pairFeatureFactory = pairFeatureFactory;

		this.tokenTreeProvider = tokenTreeProvider;
		
		this.marker = marker;
		
		this.stopwords = stopwords;
		
		this.pruner = new PosChunkPruner(-1);	
		this.pruningCriteria = new PruneIfNodeIsWithoutMetadata(RichNode.REL_KEY);

		this.questionCas = JCasFactory.createJCas();
		this.candidateCas = JCasFactory.createJCas();

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
		this.outputResFile = outputDir + DEFAULT_OUTPUT_TEST_RES_FILE;
	}

	@Override
	public void generateData(DataObject questionObject,
			List<DataObject> candidateObjects) {
		this.ae.analyze(this.questionCas,
				new SimpleContent(questionObject.getId(), ""));

		for (DataObject candidateObject : candidateObjects) {
			TokenTree questionTree = this.tokenTreeProvider.getTree(this.questionCas);

			this.ae.analyze(this.candidateCas, new SimpleContent(
					candidateObject.getId(), ""));

			TokenTree candidateTree = this.tokenTreeProvider.getTree(this.candidateCas);

			/**
			 * Mark the trees with the relational tag on the nodes sharing the same lemmas.
			 * Stopwords are not considered for matchings
			 */
			this.marker.markTrees(questionTree, candidateTree, this.parameterList);
						
			/**
			 * Perform the pruning
			 */			
			//this.pruner.prune(candidateTree, this.pruningCriteria);		
			
			/**
			 * Produce the feature vectors
			 */
			FeatureVector fv = this.pairFeatureFactory.getPairFeatures(
					this.questionCas, this.candidateCas, this.parameterList, this.stopwords);

			StringBuffer sb = new StringBuffer(1024 * 4);
			String label = candidateObject.isPositive() ? "+1" : "-1";
			sb.append(label);
			sb.append(" |BT| ");
			sb.append(this.ts.serializeTree(questionTree, this.parameterList));
			sb.append(" |BT| ");
			sb.append(this.ts.serializeTree(candidateTree, this.parameterList));
			sb.append(" |BT| ");
			sb.append(" |BT| ");
			sb.append(" |ET| ");
			sb.append(FeaturesUtil.serialize(fv));
			sb.append(" |BV| ");
			sb.append(" |EV| ");

			this.fm.writeLn(this.outputFile, sb.toString());

			sb = new StringBuffer(1024);

			sb.append(Joiner.on(" ").join(
					questionObject.getId(),
					candidateObject.getId(),
					candidateObject.getMetadata().get(
							GenericPipeline.SEARCH_ENGINE_POSITION_KEY),
					candidateObject.getMetadata().get(
							GenericPipeline.SEARCH_ENGINE_POSITION_KEY),
					label.equals("+1") ? "true" : "false"));

			this.fm.writeLn(this.outputResFile, sb.toString());
		}
	}

	/**
	 * Set the list of parameters used to retrieve the token representation
	 * 
	 * @param parameterList
	 * @return
	 */
	public RerankingTestOnlyRel setParameterList(String parameterList) {
		this.parameterList = parameterList;
		return this;
	}
}
