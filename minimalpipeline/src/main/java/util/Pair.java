package util;

public class Pair<K, V> {
	
	private K a;
	private V b;
	
	public Pair(K a, V b) {
		this.a = a;
		this.b = b;
	}
	
	public K getA() {
		return this.a;
	}
	
	public V getB() {
		return this.b;
	}
	
	public void setA(K a) {
        this.a = a;
    }
    
    public void setB(V b) {
        this.b = b;
    }
}
