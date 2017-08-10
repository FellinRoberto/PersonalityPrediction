package qa.qcri.qf.features.representation;

import org.apache.uima.jcas.JCas;

import util.Pair;

public class CustomRepresentation implements Representation {

	private Pair<String, String> representation;
	
	private String name;
	
	public CustomRepresentation(String a, String b, String name) {
		this.representation = new Pair<>(a, b);
		this.name = name;
	}
	
	@Override
	public Pair<String, String> getRepresentation(JCas aCas, JCas bCas) {
		return this.representation;
	}

	@Override
	public String getName() {
		return this.name;
	}

}
