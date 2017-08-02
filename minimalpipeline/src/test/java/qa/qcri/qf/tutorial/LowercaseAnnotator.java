package qa.qcri.qf.tutorial;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import qa.qcri.qf.type.Lowercase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

@TypeCapability(
		inputs = {
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence" },
		outputs = {
				"qa.qcri.qf.type.Lowercase"
		})
public class LowercaseAnnotator  extends JCasAnnotator_ImplBase {
	
	public static final String PARAM_LANGUAGE = "language";
	@ConfigurationParameter(name=PARAM_LANGUAGE, mandatory=true,
			description="The language of the document to be processed")
	private String language;

	@Override
	public void process(JCas cas) throws AnalysisEngineProcessException {
		
		System.out.println("LowercaseAnnotator is running!");
		
		for(Token token : JCasUtil.select(cas, Token.class)) {
			
			String tokenText = token.getCoveredText();
			
			if(tokenText.toLowerCase().equals(tokenText)) {
				Lowercase lc = new Lowercase(cas);
				lc.setBegin(token.getBegin());
				lc.setEnd(token.getEnd());
				lc.setUppercaseVersion(token.getCoveredText().toUpperCase());
				lc.addToIndexes(cas);
			}
		}
	}
	
}
