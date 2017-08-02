package qa.qcri.qf.fileutil;

import java.util.Map;

import org.maltparser.core.helper.HashMap;

/**
 * 
 * Centralizes files creation and writing
 */
public class FileManager {

	private Map<String, WriteFile> files;

	public FileManager() {
		this.files = new HashMap<>();
	}

	/**
	 * Creates a file and associates it at the given path
	 * 
	 * @param path
	 */
	public void create(String path) {
		WriteFile out = new WriteFile(path);
		this.files.put(path, out);
	}

	/**
	 * Writes a line without \n in the file associated to the given path. If the
	 * file is not referenced inside the file manager, it is created
	 * 
	 * @param path
	 * @param content
	 */
	public void write(String path, String content) {
		if (!this.files.containsKey(path)) {
			this.create(path);
		}
		this.files.get(path).write(content);
	}

	/**
	 * Writes a line with \n in the file associated to the given path. If the
	 * file is not referenced inside the file manager, it is created
	 * 
	 * @param path
	 * @param content
	 */
	public void writeLn(String path, String content) {
		if (!this.files.containsKey(path)) {
			this.create(path);
		}
		this.files.get(path).writeLn(content);
	}

	/**
	 * Closes the file associated to the given path
	 * 
	 * @param path
	 */
	public void close(String path) {
		if (this.files.containsKey(path)) {
			this.files.get(path).close();
			this.files.remove(path);
		}
	}

	/**
	 * Closes all the referenced files
	 */
	public void closeFiles() {
		for (WriteFile out : this.files.values()) {
			out.close();
		}
		this.files.clear();
	}
}
