package qa.qcri.qf.pipeline.trec;

import java.util.Iterator;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import qa.qcri.qf.fileutil.ReadFile;
import qa.qcri.qf.pipeline.readers.AnalyzableReader;
import qa.qcri.qf.pipeline.retrieval.Analyzable;
import qa.qcri.qf.pipeline.retrieval.SimpleContent;

public class TrecCandidatesReader implements AnalyzableReader {

	private String contentPath;
	
	private ReadFile in;

	public TrecCandidatesReader(String contentPath) {
		this.contentPath = contentPath;
		this.in = new ReadFile(this.contentPath);
	}

	@Override
	public Iterator<Analyzable> iterator() {
		Iterator<Analyzable> iterator = new Iterator<Analyzable>() {

			@Override
			public boolean hasNext() {
				if (!in.hasNextLine()) {
					in.close();
					return false;
				}
				return in.hasNextLine();
			}

			@Override
			public Analyzable next() {
				String line = in.nextLine().trim();
				
				List<String> fields = Lists.newArrayList(Splitter.on(" ").limit(6).split(line));
				
				String id = fields.get(1);
				String content = fields.get(5);

				return new SimpleContent(id, content);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};

		return iterator;
	}

	@Override
	public AnalyzableReader newReader() {
		return new TrecCandidatesReader(this.contentPath);
	}

	@Override
	public String getContentPath() {
		return this.contentPath;
	}

}
