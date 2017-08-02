package qa.qcri.qf.annotators;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

@TypeCapability(
		outputs = {
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence"
		})
public class WhitespaceTokenizer extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {
		String[] tokens = cas.getDocumentText().split(" ");
		int lengthAccumulator = 0;
		for(int i = 0; i < tokens.length; i++) {		
			int begin = lengthAccumulator;
			int end = begin + tokens[i].length();
			lengthAccumulator = end + 1;
			
			Token token = new Token(cas);
			token.setBegin(begin);
			token.setEnd(end);
			token.addToIndexes(cas);
		}
		
		Sentence sentence = new Sentence(cas);
		sentence.setBegin(0);
		sentence.setEnd(cas.getDocumentText().length());
		sentence.addToIndexes(cas);
	}

}
