package de.fzi.pna.prolog.simulator;
import java.io.File;
import java.io.IOException;

import fr.lip6.move.pnml.framework.utils.exception.ImportException;
import fr.lip6.move.pnml.framework.utils.exception.InvalidIDException;

public class Main {
	public static void main(String[] args) throws PrologProcessExceededRessourcesException {
		Simulator simulator = new Simulator("C:/PASIPP/pasippSources");
		Simulator.INCLUDE_YED_LABELS = false;
		try {
			File pnmlFile;
			pnmlFile = new File("decisions.pnml");
			pnmlFile = new File("simpleTree3.pnml");
			pnmlFile = new File("simpleTree1.pnml");
			pnmlFile = new File("simpleTree1Infinity.pnml");
			pnmlFile = new File("simpleTree0Infinity.pnml");
			
			Tree tree = simulator.getReachabilityTree(pnmlFile).getReachabilityTree();
//			System.out.println(newTree.toString());
			System.out.println(tree.toGraphML());
		} catch (ImportException | InvalidIDException | IllegalPetriNetTypeException | IOException e) {
			e.printStackTrace();
		}
	}
}
