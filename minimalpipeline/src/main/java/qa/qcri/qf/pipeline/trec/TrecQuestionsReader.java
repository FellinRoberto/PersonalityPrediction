package qa.qcri.qf.pipeline.trec;

import java.util.Iterator;
import java.util.List;

import qa.qcri.qf.fileutil.ReadFile;
import qa.qcri.qf.pipeline.readers.AnalyzableReader;
import qa.qcri.qf.pipeline.retrieval.Analyzable;
import qa.qcri.qf.pipeline.retrieval.SimpleContent;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class TrecQuestionsReader implements AnalyzableReader {

	private String contentPath;
	
	private ReadFile in;

	public TrecQuestionsReader(String contentPath) {
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
				
				List<String> fields = Lists.newArrayList(Splitter.on(" ").limit(2).split(line));
				
				String id = fields.get(0);
				String content = fields.get(1);

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
		return new TrecQuestionsReader(this.contentPath);
	}

	@Override
	public String getContentPath() {
		return this.contentPath;
	}

}
