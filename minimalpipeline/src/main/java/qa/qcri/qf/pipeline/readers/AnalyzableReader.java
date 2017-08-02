package qa.qcri.qf.pipeline.readers;

import qa.qcri.qf.pipeline.retrieval.Analyzable;

public interface AnalyzableReader extends Iterable<Analyzable> {
	
	public AnalyzableReader newReader();
	
	public String getContentPath();
	
}
