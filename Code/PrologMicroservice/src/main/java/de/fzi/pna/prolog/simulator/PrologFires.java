package de.fzi.pna.prolog.simulator;
import java.util.HashSet;
import java.util.Set;

public class PrologFires {
	private Set<PrologFire> fires;
	
	public PrologFires() {
		fires = new HashSet<PrologFire>();
	}
	
	public void addFire(PrologFire fire) {
		fires.add(fire);
	}
	
	public String toString() {
		String ret = "";
		for (PrologFire fire : fires) {
			if (!fire.isEmpty()) {
				if (ret != "") {
					ret += "\n";
				}
				ret += fire.toString();
			}
		}
		// Original runnable PASIPP.exe needs a kind of EOF line
		// not needed when using Arity/Prolog32 Interpreter + PASIPP source files
		// therefore only in comments
		// ret += "\nx";
		return ret;
	}
}
