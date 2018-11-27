package de.fzi.pna.prolog.simulator;
import java.util.HashMap;
import java.util.Map;

public class PrologFire {
	private Map<String, PrologEntferne> entferneMap;
	private Map<String, PrologEinfuege> einfuegeMap;
	private String id;
	
	public PrologFire(String id) {
		this.id = id;
		entferneMap = new HashMap<String, PrologEntferne>();
		einfuegeMap = new HashMap<String, PrologEinfuege>();
	}
	
	public void addEntferneRegel(String id) {
		if (entferneMap.containsKey(id)) {
			entferneMap.get(id).increaseCount();
		} else {
			entferneMap.put(id, new PrologEntferne(id));
		}
	}
	
	public void addEinfuegeRegel(String id) {
		if (einfuegeMap.containsKey(id)) {
			einfuegeMap.get(id).increaseCount();
		} else {
			einfuegeMap.put(id, new PrologEinfuege(id));
		}
	}
	
	public String toString() {
		String ret = "fire([" + "'" + id + "'" + "]) :-";
		
		for (PrologEntferne entf : entferneMap.values()) {
			ret += "\n\t" + entf.toString() + ",";
		}
		
		for (PrologEinfuege einf : einfuegeMap.values()) {
			ret += "\n\t" + einf.toString() + ",";
		}
		
		if (ret.endsWith(",")) {
			ret = ret.substring(0, ret.length() - 1);
		}
		
		ret += ".";
		return ret;
	}

	public boolean isEmpty() {
		return (entferneMap.isEmpty() && einfuegeMap.isEmpty());
	}
}
