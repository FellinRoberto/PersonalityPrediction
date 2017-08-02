package util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import qa.qcri.qf.fileutil.ReadFile;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * 
 * Reads from a file blocks of sequential lines having a common characteristic
 */
public class ChunkReader implements Iterable<List<String>> {

	private ReadFile in;

	private List<String> linesBuffer = new ArrayList<>();

	private String previousExtractedItem = null;

	private Function<String, String> groupingItemExtraction;

	/**
	 * Instantiates the reader with the specified extraction function. This
	 * function extracts a text feature from a line of text, which is used to
	 * select lines belonging to the same block.
	 * 
	 * @param path
	 *            the path of the file to read
	 * @param groupingItemExtraction
	 *            the extraction function
	 */
	public ChunkReader(String path,
			Function<String, String> groupingItemExtraction) {
		this.in = new ReadFile(path);
		this.groupingItemExtraction = groupingItemExtraction;
	}

	@Override
	public Iterator<List<String>> iterator() {
		Iterator<List<String>> iterator = new Iterator<List<String>>() {

			@Override
			public boolean hasNext() {
				if (linesBuffer.isEmpty() && !in.hasNextLine()) {
					return false;
				} else {
					return true;
				}
			}

			@Override
			public List<String> next() {

				List<String> returnList = new ArrayList<>();

				if (!linesBuffer.isEmpty() && !in.hasNextLine()) {
					returnList = Lists.newArrayList(linesBuffer);
					linesBuffer.clear();
					return returnList;
				}

				while (in.hasNextLine()) {
					String currentLine = in.nextLine().trim();

					if (currentLine.isEmpty())
						continue;

					String extractedItem = groupingItemExtraction
							.apply(currentLine);
					if (previousExtractedItem == null) {
						previousExtractedItem = extractedItem;
						linesBuffer.add(currentLine);
					} else {
						if (previousExtractedItem.equals(extractedItem)) {
							linesBuffer.add(currentLine);
						} else {
							returnList = Lists.newArrayList(linesBuffer);
							previousExtractedItem = extractedItem;
							linesBuffer.clear();
							linesBuffer.add(currentLine);

							return returnList;
						}
					}
				}

				if (linesBuffer.isEmpty()) {
					return new ArrayList<String>();
				} else {
					returnList = Lists.newArrayList(linesBuffer);
					linesBuffer.clear();
					return returnList;
				}

			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};

		return iterator;
	}

}
