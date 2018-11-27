package de.fzi.pna.prolog.simulator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;

import fr.lip6.move.pnml.ptnet.PetriNet;
import fr.lip6.move.pnml.ptnet.Place;
import fr.lip6.move.pnml.ptnet.PnObject;
import fr.lip6.move.pnml.ptnet.Transition;
import fr.lip6.move.pnml.ptnet.Arc;
import fr.lip6.move.pnml.ptnet.Page;

public class PnmlToPrologConverter {
	
	private PetriNet pnmlNet;
	private File netFile;
	private File markingFile;
	
	private PrologFires fires;
	public File getNetFile() {
		return netFile;
	}

	public File getMarkingFile() {
		return markingFile;
	}

	public PrologFires getFires() {
		return fires;
	}

	public PrologNetMarking getNetMarking() {
		return netMarking;
	}

	private PrologNetMarking netMarking;

	public void convert(PetriNet petriNet, String pasippFolder) {
		this.pnmlNet = petriNet;
		
		List<Page> pnmlPages = petriNet.getPages();
		Page pnmlPage = pnmlPages.get(0);
		List<PnObject> pnmlObjects = pnmlPage.getObjects();
		
		fires = new PrologFires();
		netMarking = new PrologNetMarking();
		
		for (PnObject object : pnmlObjects) {
			if (object instanceof Transition) {
				Transition transition = (Transition)object;
				List<Arc> inArcs = transition.getInArcs();
				List<Arc> outArcs = transition.getOutArcs();
				
				PrologFire fireRule = new PrologFire(transition.getId());
				
				for (Arc inArc : inArcs) {
					fireRule.addEntferneRegel(inArc.getSource().getId());
				}
				for(Arc outArc : outArcs) {
					fireRule.addEinfuegeRegel(outArc.getTarget().getId());
				}
				fires.addFire(fireRule);
			} else if (object instanceof Place) {
				Place place = (Place)object;
				if (place.getInitialMarking() != null) {
					if (place.getInitialMarking().getText() != null) {
						netMarking.addPlaceMarking(place.getId(), place.getInitialMarking().getText());
					}
				}
			}
		}
		
		Long time = System.nanoTime();
		netFile = new File(pasippFolder + "/plNet" + time + ".net");
		markingFile = new File(pasippFolder + "/plMark" + time + ".dat");
		try {
			netFile.createNewFile();
		} catch (Exception e) {
			System.out.println("ERROR");
			System.out.println("could not write to file " + pasippFolder + "/plNet" + time + ".net");
		}
		try {
			markingFile.createNewFile();
		} catch (Exception e) {
			System.out.println("ERROR");
			System.out.println("could not write to file " + pasippFolder + "/plMark" + time + ".dat");
		}
		try {
			PrintWriter out = new PrintWriter(netFile);
			out.print(fires.toString());
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("ERROR");
			System.out.println("could not write to file " + pasippFolder + "/plNet" + time + ".net");
		}
		
		try {
			PrintWriter out = new PrintWriter(markingFile);
			out.print(netMarking.toString());
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("ERROR");
			System.out.println("could not write to file " + pasippFolder + "/plMark" + time + ".dat");
		}
		
//		return new File[] {netFile, markingFile};
	}
	
	public void deleteFiles() throws IOException {
		if (netFile != null)
			Files.deleteIfExists(netFile.toPath());
		if (markingFile != null)
			Files.deleteIfExists(markingFile.toPath());
	}

	public PetriNet getPnmlNet() {
		return pnmlNet;
	}
	
}
