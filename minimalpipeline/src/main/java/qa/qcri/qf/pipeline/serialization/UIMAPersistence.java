package qa.qcri.qf.pipeline.serialization;

import org.apache.uima.jcas.JCas;

public interface UIMAPersistence {

	public void serialize(JCas cas, String casId);

	public void deserialize(JCas cas, String casId);

	public boolean isAlreadySerialized(String casId);

}
