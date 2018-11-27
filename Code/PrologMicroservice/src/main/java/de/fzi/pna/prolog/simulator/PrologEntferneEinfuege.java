package de.fzi.pna.prolog.simulator;

public abstract class PrologEntferneEinfuege {
	String type;			// "entferne" oder "einfuege"
	private String id;
	private int count;
	
	public PrologEntferneEinfuege(String id, String type) {
		this(id, type, 1);
	}
	private PrologEntferneEinfuege(String id, String type, int count) {
		this.type = type;
		this.id = id;
		this.count = count;
	}
	
	public void increaseCount() {
		count++;
	}
	
	public void decreaseCount() {
		count--;
	}

	public int getCount() {
		return count;
	}
	
	public String getId() {
		return id;
	}
	
	public String toString() {
		return type + "(" + count + "," + "'" + id + "'" + ")";
	}
}
