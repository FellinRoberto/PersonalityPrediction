package qa.qcri.qf.pipeline.readers;

import java.util.Iterator;

import qa.qcri.qf.fileutil.ReadFile;
import qa.qcri.qf.pipeline.retrieval.Analyzable;
import qa.qcri.qf.pipeline.retrieval.SimpleContent;

public class SampleFileReader implements AnalyzableReader {

	private String contentPath;
	
	private ReadFile in;

	public SampleFileReader(String contentPath) {
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
			public SimpleContent next() {
				String line = in.nextLine().trim();
				String[] fields = line.split("\t");
				String id = fields[0];
				String content = fields[1];

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
		return new SampleFileReader(this.contentPath);
	}

	@Override
	public String getContentPath() {
		return this.contentPath;
	}

}
