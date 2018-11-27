package de.fzi.pna.prolog.simulator;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import fr.lip6.move.pnml.framework.hlapi.HLAPIRootClass;
import fr.lip6.move.pnml.framework.utils.PNMLUtils;
import fr.lip6.move.pnml.framework.utils.exception.ImportException;
import fr.lip6.move.pnml.framework.utils.exception.InvalidIDException;
import fr.lip6.move.pnml.ptnet.PTMarking;
import fr.lip6.move.pnml.ptnet.impl.PtnetFactoryImpl;
import fr.lip6.move.pnml.ptnet.Page;
import fr.lip6.move.pnml.ptnet.PetriNet;
import fr.lip6.move.pnml.ptnet.Place;
import fr.lip6.move.pnml.ptnet.PnObject;

public class Simulator {
	static boolean INCLUDE_YED_LABELS = false;
	private PrologWrapper arityProcess;
	private PnmlToPrologConverter converter;
	private String pasippFolder;
	
	private File netFile;
	private File markingFile;

	public Simulator(String pasippFolder) {
		this(pasippFolder, false);
	}

	public Simulator(String pasippFolder, boolean yEd) {
        // checkPasippPath(pasippFolder);
		this.converter = new PnmlToPrologConverter();
		this.pasippFolder = pasippFolder;
		
		INCLUDE_YED_LABELS = yEd;
	}

	private void close() throws IOException {
		arityProcess.close();
		converter.deleteFiles();
	}
	
	public ReachabilityTreeResult getReachabilityTree(String pnmlString) throws ImportException, InvalidIDException, IllegalPetriNetTypeException, IOException, PrologProcessExceededRessourcesException {
		return getReachabilityTree(getFileFromPnmlString(pnmlString));
	}
	public ReachabilityTreeResult getReachabilityTree(File pnmlFile) throws ImportException, InvalidIDException, IllegalPetriNetTypeException, IOException, PrologProcessExceededRessourcesException {
		return getReachabilityTree(fileToPetriNet(pnmlFile));
	}
	public ReachabilityTreeResult getReachabilityTree(PetriNet pnmlNet) throws IOException, PrologProcessExceededRessourcesException {
		try {
			CycleObject cycles = createAndLoadPrologFiles(pnmlNet);
			PrologNetMarking originalMarking = converter.getNetMarking();
			
			callPrologCommand("reachtree_menue(a).");
			callPrologCommand(markingFile.getName());
			
			String prologResponse = getPrologResponse();
			
			Tree tree = getTree(prologResponse);

			if (tree.hasNoEdges()) {
				if (!prologResponse.contains("Es gibt keine Kanten im Erreichbarkeitsbaum.")) {
					throw new PrologProcessExceededRessourcesException();
				}
			}
			
			try {
				tree.setOriginalMarking(originalMarking);
			} catch (Exception e) {
				System.out.println("original marking could not be set");
			}
			
			return new ReachabilityTreeResult(tree, cycles);
		} finally {
			close();
		}
	}

	public CycleObject hasCycles(String pnmlString) throws IOException, ImportException, InvalidIDException, IllegalPetriNetTypeException {
		return hasCycles(getFileFromPnmlString(pnmlString));
	}
	public CycleObject hasCycles(File pnmlFile) throws ImportException, InvalidIDException, IllegalPetriNetTypeException, IOException {
		return hasCycles(fileToPetriNet(pnmlFile));
	}
	public CycleObject hasCycles(PetriNet pnmlNet) throws IOException {
		try {
			return createAndLoadPrologFiles(pnmlNet);
		} finally {
			close();
		}
	}

	public FireableTransitionsResult getPossibleFiringTransitions(String pnmlString) throws IOException, ImportException, InvalidIDException, IllegalPetriNetTypeException {
		return getPossibleFiringTransitions(getFileFromPnmlString(pnmlString));
	}
	public FireableTransitionsResult getPossibleFiringTransitions(File pnmlFile) throws ImportException, InvalidIDException, IllegalPetriNetTypeException, IOException {
		return getPossibleFiringTransitions(fileToPetriNet(pnmlFile));
	}
	public FireableTransitionsResult getPossibleFiringTransitions(PetriNet pnmlNet) throws IOException {
		try {
			CycleObject cycles = createAndLoadPrologFiles(pnmlNet);
			callPrologCommand("simulation(vorwaerts,X).");
			String prologResponse = getPrologResponse();
			List<String> fireableTransitions = getFireableTransitions(prologResponse);
			return new FireableTransitionsResult(fireableTransitions, cycles);
		}
		finally {
			close();
		}
	}

	public SimulationStepResult simulateStep(String pnmlString, String transitionId) throws ImportException, InvalidIDException, IOException, IllegalPetriNetTypeException {
		return simulateStep(getFileFromPnmlString(pnmlString), transitionId);
	}
	public SimulationStepResult simulateStep(File pnmlFile, String transitionId) throws ImportException, InvalidIDException, IllegalPetriNetTypeException, IOException {
		return simulateStep(fileToPetriNet(pnmlFile), transitionId);
	}
	public SimulationStepResult simulateStep(PetriNet pnmlNet, String transitionId) throws IOException {
		try {
			CycleObject cycles = createAndLoadPrologFiles(pnmlNet);
			callPrologCommand("simulation(vorwaerts,X).");
			if (transitionId == null || transitionId.equals("")) {
				callPrologCommand("a");
				callPrologCommand("");
				callPrologCommand("");
				callPrologCommand("");
			} else {
				callPrologCommand("b");
				callPrologCommand("");
				callPrologCommand("['" + transitionId + "'].");
				callPrologCommand("");
				callPrologCommand("");
				callPrologCommand("");
			}

			Map<String, Integer> markings = getAllMarkings();

			PetriNet resultingPNML = updatePNML(markings);
			return new SimulationStepResult(resultingPNML, cycles);	
		}
		finally {
			close();
		}
	}

	private PetriNet updatePNML(Map<String,Integer> markings) {
		PetriNet petriNet = converter.getPnmlNet();
		List<Page> pnmlPages = petriNet.getPages();
		Page pnmlPage = pnmlPages.get(0);
		List<PnObject> pnmlObjects = pnmlPage.getObjects();

		PtnetFactoryImpl factory = new PtnetFactoryImpl();

		for (PnObject object : pnmlObjects) {
			if (object instanceof Place) {
				Place place = (Place)object;
				Integer newMarking = markings.get(place.getId());
				if (newMarking == null) {
					if (place.getInitialMarking() != null) {
						place.getInitialMarking().setText(0L);
					}
				} else {
					if (place.getInitialMarking() != null) {
						place.getInitialMarking().setText(newMarking.longValue());
					} else {
						PTMarking marking = factory.createPTMarking();
						marking.setText(newMarking.longValue());
						place.setInitialMarking(marking);
					}
				}
			}
		}
		return petriNet;
	}

	private CycleObject createAndLoadPrologFiles(PetriNet pnmlNet) throws IOException {
		converter.convert(pnmlNet, pasippFolder);
		netFile = converter.getNetFile();
		markingFile = converter.getMarkingFile();

		initProlog();

		String prologResponse;
		String lastLine;

		getPrologResponse();

		callPrologCommand("lade('" + netFile.getName() + "','das Netz').");
		prologResponse = getPrologResponse();
		lastLine = prologResponse.substring(prologResponse.lastIndexOf("\n"));
		CycleObject cycles = new CycleObject();
		if (lastLine.contains("<j>")) {
			cycles = checkCycles(prologResponse, cycles);
			callPrologCommand("");
			prologResponse = getPrologResponse();
			lastLine = prologResponse.substring(prologResponse.lastIndexOf("\n"));
			if (lastLine.contains("<j>")) {
				cycles = checkCycles(prologResponse, cycles);
				callPrologCommand("");
			}
		}
		callPrologCommand("");
		callPrologCommand("");
		callPrologCommand("lade('" + markingFile.getName() + "','die Startmarkierung').");
		// hier werden Warnungen ausgegeben wenn Kapazit�tsbeschr�nkungen verletzt werden.
		// da das Programm aber keine Kapazit�tsbeschr�nkungen erzeugt, kann dies
		// ignoriert werden.
		callPrologCommand("");

		return cycles;
	}

	private CycleObject checkCycles(String prologResponse, CycleObject cycles) {
		if (prologResponse.contains("Vorw")) {
			cycles.setForwardCycle(true);
		}
		if (prologResponse.contains("ckw")) {
			cycles.setBackwardCycle(true);
		}
		return cycles;
	}

	private File getFileFromPnmlString(String pnmlString) throws IOException {
		try {
			File file = new File("pnmlFile.pnml");
			file.createNewFile();
			PrintWriter printWriter = new PrintWriter(file);
			printWriter.write(pnmlString);
			printWriter.close();
			return file;
		} catch (Exception e) {
			System.out.println("ERROR");
			System.out.println("could not write to file pnmlFile.pnml");
		}
		return null;
	}

	private Map<String, Integer> getAllMarkings() {
		Map<String, Integer> markings = new HashMap<String, Integer>();
		String prologResponse = getPrologResponse();
		callPrologCommand("static_menue_p(a).");
		prologResponse = getPrologResponse();
		String[] responseLines = prologResponse.split("\\r?\\n");
		for (int i = 2; i < responseLines.length - 2; i++) {
			String line = responseLines[i];
			String[] marking = line.split("  /  ", 2);
			markings.put(marking[1], Integer.parseInt(marking[0]));
		}

		return markings;
	}

	private List<String> getFireableTransitions(String prologResponse) {
		String [] responseLines = prologResponse.split("\\r?\\n");
		List<String> ids = new LinkedList<String>();
		for (int i = responseLines.length - 1; i >= 0; i--) {
			String line = responseLines[i].trim();
			if (line.startsWith("[")) {
				line = line.substring(2, line.length() - 2);
				if (line.startsWith("'")) {
					line = line.substring(1, line.length() - 1);
				}
				ids.add(line);
			}
		}
		return ids;
	}
	private void initProlog() throws IOException {
		arityProcess = new PrologWrapper(pasippFolder);
		initPasipp();
	}
	private void initPasipp() {
		callPrologCommand("consult('pasipp.ari').");
		callPrologCommand("consult('build.ari').");
		callPrologCommand("consult('simulat.ari').");
		callPrologCommand("consult('help1.ari').");
		callPrologCommand("consult('network.ari').");
		callPrologCommand("consult('net_ana.ari').");
		callPrologCommand("consult('static.ari').");
		callPrologCommand("consult('dynamic.ari').");
		callPrologCommand("consult('help.ari').");
		callPrologCommand("consult('options.ari').");
		callPrologCommand("consult('operat.ari').");
		callPrologCommand("consult('tree.ari').");
		callPrologCommand("consult('init.ari').");
		callPrologCommand("consult('special.ari').");
		callPrologCommand("consult('analyse.ari').");
		callPrologCommand("init.");
		// System.out.println("init");
		// System.out.println("prologResponse:");
		// System.out.println("\"" + getPrologResponse() + "\"");
		getPrologResponse();
	}

	private void callPrologCommand(String command) {
		//		System.out.println(getPrologResponse());
		//		System.out.println("" + command);
		arityProcess.callPrologCommand(command);
		//		System.out.println(getPrologResponse());
	}
	private String getPrologResponse() {
		return arityProcess.getPrologResponse();
	}

	private PetriNet fileToPetriNet(File pnmlFile) throws ImportException, InvalidIDException, IllegalPetriNetTypeException {
		HLAPIRootClass rc = null;
		try {
			// Load the document. No fall back to any compatible type (false).
			// Fall back takes place between an unknown Petri Net type and the CoreModel.
			rc = PNMLUtils.importPnmlDocument(pnmlFile, true);
		} catch (ImportException e) {
			try {
				HorusClient client = new HorusClient();
				String pnmlString = client.convertHorusToPnml(pnmlFile);
				pnmlFile = getFileFromPnmlString(pnmlString);
				rc = PNMLUtils.importPnmlDocument(pnmlFile, true);
			} catch (IOException e1) {
				throw new ImportException();
			}
		}
		
		

		// Check that Petri Net Document type is a Place/Transition Net
		if (PNMLUtils.isPTNetDocument(rc)) {
			// process the Place/Transition Net document, get the right type first
			fr.lip6.move.pnml.ptnet.hlapi.PetriNetDocHLAPI ptDoc =
					(fr.lip6.move.pnml.ptnet.hlapi.PetriNetDocHLAPI)rc;

			List<fr.lip6.move.pnml.ptnet.PetriNet> petriNetList = ptDoc.getNets();
			return petriNetList.get(0);
		} else {
			throw new IllegalPetriNetTypeException("Petri Net must be a Place/Transition Petri Net");
		}
	}

	private Tree getTree(String prologResponse) {
		Tree tree = new Tree();
		String[] rows = prologResponse.split("\r\n");
		for (String row : rows) {
			if (row.startsWith("[")) {
				tree = addRelationshipToTree(row, tree);
			}
		}
		return tree;
	}
	
	private Tree addRelationshipToTree(String relationship, Tree tree) {
		PrologNetMarking parent = new PrologNetMarking();
		PrologNetMarking child = new PrologNetMarking();

		relationship = relationship.substring(1, relationship.length() - 1);
		
		boolean goOn = true;
		while (goOn) {
			relationship = relationship.trim();
			if (relationship.startsWith(",")) {
				relationship = relationship.substring(1);
				relationship = relationship.trim();
			}
			if (relationship.startsWith("[")) {
				String marking = relationship.substring(relationship.indexOf("[") + 1, relationship.indexOf("]")).trim();
				relationship = relationship.substring(relationship.indexOf("]") + 1);
				if (!marking.isEmpty()) {
					parent = addMarking(parent, marking);
				}
			} else {
				goOn = false;
			}
		}
		
		relationship = relationship.substring(1).trim().substring(1).trim().substring(1);
		
		goOn = true;
		while (goOn) {
			relationship = relationship.trim();
			if (relationship.startsWith(",")) {
				relationship = relationship.substring(1);
				relationship = relationship.trim();
			}
			if (relationship.startsWith("[")) {
				String marking = relationship.substring(relationship.indexOf("[") + 1, relationship.indexOf("]")).trim();
				relationship = relationship.substring(relationship.indexOf("]") + 1);
				if (!marking.isEmpty()) {
					child = addMarking(child, marking);
				}
			} else {
				goOn = false;
			}
		}
		
		tree.addEdge(parent, child);
		return tree;
	}
	
	private PrologNetMarking addMarking(PrologNetMarking netMarking, String markingString) {
		markingString = markingString.substring(1, markingString.length() - 1);
		int firstComma = markingString.indexOf(",");
		String count = markingString.substring(0,firstComma).trim();
		String id = markingString.substring(firstComma + 1).trim();
		
		if (count.equals("infinit")) {
			netMarking.addPlaceMarking(id, -1);
		} else {
			netMarking.addPlaceMarking(id, Long.parseLong(count));
		}
		
		return netMarking;
	}
}
