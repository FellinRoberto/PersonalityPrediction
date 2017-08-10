package qa.qcri.qf.annotators;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import LBJ2.nlp.seg.WordsToTokens;
import LBJ2.parse.LinkedVector;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk;
import edu.illinois.cs.cogcomp.lbj.chunk.Chunker;

@TypeCapability(
		inputs = {
				"de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS",
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence" },
		outputs = {
				"de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk"

		})
public class IllinoisChunker extends JCasAnnotator_ImplBase {
	/**
	 * Chunk annotator using IllinoisChunker.
	 */	
	
	private Chunker chunker;
	
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);	
	}

	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {
		
		// Lazy loading
		if(this.chunker == null) {
			init();
		}
		
		for(Sentence sentence : JCasUtil.select(cas, Sentence.class)) {		
			LinkedVector illinoisWords = getIllinoisWords(cas, sentence);
			LinkedVector illinoisTokens = WordsToTokens.convert(illinoisWords);
			
			List<LBJ2.nlp.seg.Token> tokens = new ArrayList<>();		
			for(int i = 0, n = illinoisTokens.size(); i < n; i++) {
				LBJ2.nlp.seg.Token currentToken = (LBJ2.nlp.seg.Token) illinoisTokens.get(i);
				String tag = this.chunker.discreteValue(currentToken);
				// Assign the tag to the token label
				currentToken.label = tag;
				
				if(tag.startsWith("B-")) {
					extractChunk(tokens, cas);
					tokens.add(currentToken);
				} else if(tag.startsWith("I-")) {
					tokens.add(currentToken);
				} else {
					extractChunk(tokens, cas);
				}
			}

			extractChunk(tokens, cas);
		}
	}

	private LinkedVector getIllinoisWords(JCas cas, Sentence sentence) {
		LinkedVector illinoisWords = new LinkedVector();	
		for(Token token : JCasUtil.selectCovered(cas, Token.class, sentence)) {
			// Build a word with the Token data
			LBJ2.nlp.Word word = new LBJ2.nlp.Word(token.getCoveredText(),
					token.getPos().getPosValue(), token.getBegin(), token.getEnd());
			// Add the word to the word list
			illinoisWords.add(word);
		}
		return illinoisWords;
	}

	private void extractChunk(List<LBJ2.nlp.seg.Token> tokens, JCas cas) {
		if(!tokens.isEmpty()) {
			int begin = tokens.get(0).start;
			int end = tokens.get(tokens.size() - 1).end;
			
			Chunk chunk = new Chunk(cas);
			chunk.setBegin(begin);
			chunk.setChunkValue(tokens.get(0).label.substring(2));
			chunk.setEnd(end);
			chunk.addToIndexes();
			
			tokens.clear();
		}
	}
	
	private void init() {
		this.chunker = new Chunker();
	}

}
