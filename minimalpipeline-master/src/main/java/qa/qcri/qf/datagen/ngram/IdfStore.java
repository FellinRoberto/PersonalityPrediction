package qa.qcri.qf.datagen.ngram;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import qa.qcri.qf.fileutil.ReadFile;
import qa.qcri.qf.fileutil.WriteFile;

/**
 * Implements the IdfModel.
 *
 */
public class IdfStore implements IdfModel {

		
	private Map<String, Double> str2idfMap = new HashMap<String, Double>();
		
	IdfStore(Map<String, Double> str2idfMap) {
		if (str2idfMap == null) {
			throw new NullPointerException("str2idfMap is null");
		}
		
		this.str2idfMap = str2idfMap;
	}
		
	public IdfStore(String modelFile) {
		if (modelFile == null) {
			throw new NullPointerException("modelFile is null");
		}
		
		str2idfMap = readModelFile(modelFile);			
	}
		
	@Override
	public double getIdf(String str) {
		if (str == null) {
			throw new NullPointerException();
		}
		
		return str2idfMap.get(str);
	}
		
	private Map<String, Double> readModelFile(String modelFile) {
		assert modelFile != null;
			
		Map<String, Double> str2idfMap = new HashMap<>();
		ReadFile file = new ReadFile(modelFile);
		for (String line = file.nextLine(); line != null; line = file.nextLine()) {
			String[] linesplit = line.trim().split("\t");
			String str = linesplit[0];
			double df = Double.parseDouble(linesplit[1]);
			str2idfMap.put(str, df);
		}	
		file.close();
		return str2idfMap;		
	}
		
		
	public void saveModel(String modelFile) {
		if (modelFile == null) { 
			throw new NullPointerException("modelFile is null");
		}
		
		WriteFile file = new WriteFile(modelFile);
		for (String str : str2idfMap.keySet()) {
			double idf = str2idfMap.get(str);
			file.writeLn(str + "\t" + idf);
		}
		file.close();
	}

	@Override
	public Set<String> keySet() {
		return str2idfMap.keySet();
	}
	
}
