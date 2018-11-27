package de.fzi.pna.prolog.simulator;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.Assert;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import de.fzi.pna.prolog.simulator.CycleObject;
import de.fzi.pna.prolog.simulator.FireableTransitionsResult;
import de.fzi.pna.prolog.simulator.IllegalPetriNetTypeException;
import de.fzi.pna.prolog.simulator.PrologNetMarking;
import de.fzi.pna.prolog.simulator.PrologProcessExceededRessourcesException;
import de.fzi.pna.prolog.simulator.ReachabilityTreeResult;
import de.fzi.pna.prolog.simulator.Simulator;
import de.fzi.pna.prolog.simulator.Tree;
import fr.lip6.move.pnml.framework.utils.exception.ImportException;
import fr.lip6.move.pnml.framework.utils.exception.InvalidIDException;
import fr.lip6.move.pnml.ptnet.Page;
import fr.lip6.move.pnml.ptnet.PetriNet;
import fr.lip6.move.pnml.ptnet.Place;
import fr.lip6.move.pnml.ptnet.PnObject;

class SimulatorTest {
	private String pasippPath = "C:/PASIPP/pasippSources";

//	@Test
//	void testGetPossibleFirings() throws ImportException, InvalidIDException {
//		Simulator simulator = new Simulator(pasippPath);
////		String[] result = simulator.getPossibleFiringMarkings(new File("src/test/resources/piscine.pnml"));
//		List<String> result = simulator.getPossibleFiringMarkings(new File("src/test/resources/philosophers.pnml"));
//		Assert.assertNotNull(result);
//		List<String> expected = Arrays.asList(new String[]{"cId200-i943123747","cId180-i943123747","cId188-i943123747","cId185-i943123747","cId192-i943123747","cId204-i943123747"});
//		Assert.assertTrue(result.containsAll(expected));
//		Assert.assertTrue(result.size() == expected.size());
//	}
	
/*	@Test
	void testManyTrees() throws ImportException, InvalidIDException, IllegalPetriNetTypeException, IOException, PrologProcessExceededRessourcesException {
		Simulator simulator = new Simulator(pasippPath);
		ReachabilityTreeResult[] treeResults = new ReachabilityTreeResult[50];
		
		for (int i = 0; i < treeResults.length; i++) {
			treeResults[i] = simulator.getReachabilityTree(new File("src/test/resources/rome.pnml"));
			System.out.println("i = " + i);
			if (i > 0) {
				Assert.assertEquals(treeResults[i-1].getReachabilityTree(), treeResults[i].getReachabilityTree());
			}
		}
	}*/
	
	@Test
	void testHorusPnml() throws ImportException, InvalidIDException, IllegalPetriNetTypeException, IOException  {
		Simulator simulator = new Simulator(pasippPath);
		
		File folder = new File("src/test/resources/horus");
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			File curFile = listOfFiles[i];
			if (curFile.isFile()) {
				// TODO 
				String path = curFile.getAbsolutePath();
				if (!path.contains("3.4 - 3.19 Modellierungsmuster für Ablaufmodelle") && !path.contains("4.38 - L1 - DemandPlan2Replenishment") && !path.contains("4.38 - L2 - MinMaxBeschaffung")) {	// "echter" Double-Wert
					if (!path.contains("4.10 Unternehmensarchitektur Modellierung") && !path.contains("4.2 Strategie- und Architetkturphase") && !path.contains("@4.3 Kontextanalyse")) {		// Und-Zeichen
						System.out.println(curFile.getAbsolutePath());
						simulator.hasCycles(curFile);
					}
				}
			}
		}

//		CycleObject cycles;
//		cycles = simulator.hasCycles(new File("src/test/resources/Vertriebsprozess.pnml"));
//		cycles = simulator.hasCycles(new File("src/test/resources/AblaufModell3.pnml"));
	}
	
	@Test
	void testFunctionalityWithHorusPnml() throws ImportException, InvalidIDException, IllegalPetriNetTypeException, IOException, PrologProcessExceededRessourcesException {
		Simulator simulator = new Simulator(pasippPath);
		
		File folder = new File("src/test/resources/horus");
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			File curFile = listOfFiles[i];
			if (curFile.isFile()) {
				// TODO 
				System.out.println(curFile.getAbsolutePath());
				simulator.hasCycles(curFile);
				simulator.getPossibleFiringTransitions(curFile);
				simulator.getReachabilityTree(curFile);
				simulator.simulateStep(curFile, null);
			}
		}
	}
	
	@Test
	void testReachabilityTreeHorus() throws ImportException, InvalidIDException, IllegalPetriNetTypeException, IOException, PrologProcessExceededRessourcesException {
		Simulator simulator = new Simulator(pasippPath, true);
		File file = new File("src/test/resources/horus/AblaufModell.pnml");
		
		ReachabilityTreeResult treeResult = simulator.getReachabilityTree(file);
		System.out.println(treeResult.getReachabilityTree().toGraphML());
	}
	
	@Test
	void testGetTree() throws ImportException, InvalidIDException, IllegalPetriNetTypeException, IOException, PrologProcessExceededRessourcesException {
		Simulator simulator = new Simulator(pasippPath);
		
		Assertions.assertThrows(PrologProcessExceededRessourcesException.class, () -> simulator.getReachabilityTree(new File("src/test/resources/tooManyDecisions.pnml")));
		
		ReachabilityTreeResult treeResult = simulator.getReachabilityTree(new File("src/test/resources/rome.pnml"));
		Assert.assertFalse(treeResult.getReachabilityTree().toString().contains("infinity"));
		
		treeResult = simulator.getReachabilityTree(new File("src/test/resources/cycle.pnml"));
		Assert.assertFalse(treeResult.getReachabilityTree().toString().contains("infinity"));
/*		Tree expected = new Tree();
		expected.addEdge(new PrologNetMarking().addPlaceMarking("place2", 2), new PrologNetMarking().addPlaceMarking("place1", 1).addPlaceMarking("place2", 1));
		expected.addEdge(new PrologNetMarking().addPlaceMarking("place1", 2), new PrologNetMarking().addPlaceMarking("place1", 1).addPlaceMarking("place2", 1));
		expected.addEdge(new PrologNetMarking().addPlaceMarking("place1", 1).addPlaceMarking("place2", 1), new PrologNetMarking().addPlaceMarking("place2", 2));
		expected.addEdge(new PrologNetMarking().addPlaceMarking("place1", 1).addPlaceMarking("place2", 1), new PrologNetMarking().addPlaceMarking("place1", 2));
		expected.setOriginalMarking(new PrologNetMarking().addPlaceMarking("place1", 1).addPlaceMarking("place2", 1));
		Assert.assertEquals(expected, treeResult.getReachabilityTree());*/
		
		treeResult = simulator.getReachabilityTree(new File("src/test/resources/cycleAdd.pnml"));
		Assert.assertTrue(treeResult.getReachabilityTree().toString().contains("infinity"));
		
		treeResult = simulator.getReachabilityTree(new File("src/test/resources/infinity.pnml"));
		Assert.assertTrue(treeResult.getReachabilityTree().toString().contains("infinity"));
		Tree expected = new Tree();
		expected.addEdge(new PrologNetMarking().addPlaceMarking("place1", 1), new PrologNetMarking().addPlaceMarking("place1", -1));	// -1 stands for infinity
		expected.addEdge(new PrologNetMarking().addPlaceMarking("place1", -1), new PrologNetMarking().addPlaceMarking("place1", -1));	// -1 stands for infinity
		expected.setOriginalMarking(new PrologNetMarking().addPlaceMarking("place1", 1));
		/**/
		treeResult = simulator.getReachabilityTree(new File("src/test/resources/cycleRemove.pnml"));
		Assert.assertFalse(treeResult.getReachabilityTree().toString().contains("infinity"));
		expected = new Tree();
		expected.addEdge(new PrologNetMarking().addPlaceMarking("place1", 1), new PrologNetMarking().addPlaceMarking("place2", 1));
		expected.addEdge(new PrologNetMarking().addPlaceMarking("place2", 2), new PrologNetMarking().addPlaceMarking("place1", 1));
		expected.addEdge(new PrologNetMarking().addPlaceMarking("place1", 1).addPlaceMarking("place2", 1), new PrologNetMarking().addPlaceMarking("place2", 2));
		expected.setOriginalMarking(new PrologNetMarking().addPlaceMarking("place1", 1).addPlaceMarking("place2", 1));
		Tree actual = treeResult.getReachabilityTree();
		
		Assert.assertEquals(expected.getEdges(), actual.getEdges());
		Assert.assertEquals(expected.getNodeCount(), actual.getNodeCount());
		Assert.assertEquals(expected.getNodes(), actual.getNodes());
		Assert.assertEquals(expected, treeResult.getReachabilityTree());
	}
	
	@Test
	void testInvalidXML() throws IOException, ImportException, InvalidIDException, IllegalPetriNetTypeException {
		Simulator simulator = new Simulator(pasippPath);
		Assertions.assertThrows(ImportException.class, () -> simulator.getPossibleFiringTransitions("invalidXML"));
		
		File file = new File("src/test/resources/pnmlFile.pnml");
		file.createNewFile();
		PrintWriter printWriter = new PrintWriter(file);
		printWriter.write("invalidXML");
		printWriter.close();
		Assertions.assertThrows(ImportException.class, () -> simulator.getPossibleFiringTransitions(file));
	}
	
	@Test
	void testPnmlString() throws IOException, ImportException, InvalidIDException, IllegalPetriNetTypeException {
		Simulator simulator = new Simulator(pasippPath);
		String pnmlString = "<pnml xmlns=\"http://www.pnml.org/version-2009/grammar/pnml\">\r\n" + 
				"  <net id=\"net0\" type=\"http://www.pnml.org/version-2009/grammar/ptnet\">\r\n" + 
				"    <page id=\"page0\">\r\n" + 
				"      <place id=\"place1\">\r\n" + 
				"        <initialMarking><text>1</text></initialMarking>\r\n" + 
				"      </place>\r\n" + 
				"      <transition id=\"transitionA\"/>\r\n" + 
				"      <arc id=\"arc1\" source=\"place1\" target=\"transitionA\"/>\r\n" + 
				"      <arc id=\"arc2\" source=\"transitionA\" target=\"place1\"/>\r\n" + 
				"    </page>\r\n" + 
				"  </net>\r\n" + 
				"</pnml>";
		FireableTransitionsResult result = simulator.getPossibleFiringTransitions(pnmlString);

		Assert.assertTrue(result.getCycles().hasBackwardCycle());
		Assert.assertTrue(result.getCycles().hasForwardCycle());
		List<String> transitions = result.getFireableTransitions();
		List<String> expected = Arrays.asList(new String[]{"transitionA"});
		Assert.assertTrue(transitions.containsAll(expected));
		Assert.assertTrue(transitions.size() == expected.size());
	}
	
	@Test
	void testGetPossibleFirings2() throws ImportException, InvalidIDException, IllegalPetriNetTypeException, IOException {
		Simulator simulator = new Simulator(pasippPath);
		List<String> result = simulator.getPossibleFiringTransitions(new File("src/test/resources/piscine.pnml")).getFireableTransitions();
		Assert.assertNotNull(result);
		List<String> expected = Arrays.asList(new String[]{"cId-77830227927229401959"});
		Assert.assertTrue(result.containsAll(expected));
		Assert.assertTrue(result.size() == expected.size());
	}
	
	@Test
	void testGetPossibleFiringsSimpleNet() throws ImportException, InvalidIDException, IllegalPetriNetTypeException, IOException {
		Simulator simulator = new Simulator(pasippPath);
		List<String> result = simulator.getPossibleFiringTransitions(new File("src/test/resources/simpleNet.pnml")).getFireableTransitions();
		
		Assert.assertNotNull(result);
		List<String> expected = Arrays.asList(new String[]{"transitionA"});
		Assert.assertTrue(result.containsAll(expected));
		Assert.assertTrue(result.size() == expected.size());
	}
	
	@Test
	void testSimulateFiring() throws ImportException, InvalidIDException, IllegalPetriNetTypeException, IOException {
		Simulator simulator = new Simulator(pasippPath);
		PetriNet petriNet = simulator.simulateStep(new File("src/test/resources/piscine.pnml"), "").getResultingPnmlPetriNet();
		
		List<String> fireableMarkings = simulator.getPossibleFiringTransitions(petriNet).getFireableTransitions();
		Assert.assertNotNull(fireableMarkings);
		List<String> expected = Arrays.asList(new String[]{"cId-778302279272294019510"});
		Assert.assertTrue(fireableMarkings.containsAll(expected));
		Assert.assertTrue(fireableMarkings.size() == expected.size());
	}
	
	@Test
	void testMultipleSimulationSteps() throws ImportException, InvalidIDException, IllegalPetriNetTypeException, IOException {
		Simulator simulator = new Simulator(pasippPath);
		List<String> fireableTransitions = simulator.getPossibleFiringTransitions(new File("src/test/resources/piscine.pnml")).getFireableTransitions();
		
		Assert.assertNotNull(fireableTransitions);
		List<String> expected = Arrays.asList(new String[]{"cId-77830227927229401959"});
		Assert.assertTrue(fireableTransitions.containsAll(expected));
		Assert.assertTrue(fireableTransitions.size() == expected.size());
		
		// fire cId-...1959
		PetriNet petriNet = simulator.simulateStep(new File("src/test/resources/piscine.pnml"), "").getResultingPnmlPetriNet();
		fireableTransitions = simulator.getPossibleFiringTransitions(petriNet).getFireableTransitions();
		
		Assert.assertNotNull(fireableTransitions);
		expected = Arrays.asList(new String[]{"cId-778302279272294019510"});
		Assert.assertTrue(fireableTransitions.containsAll(expected));
		Assert.assertEquals(expected.size(), fireableTransitions.size());
		
		// fire cId-...9510
		petriNet = simulator.simulateStep(petriNet, "cId-778302279272294019510").getResultingPnmlPetriNet();
		fireableTransitions = simulator.getPossibleFiringTransitions(petriNet).getFireableTransitions();
		
		Assert.assertNotNull(fireableTransitions);
		expected = Arrays.asList(new String[]{"cId-778302279272294019511"});
		Assert.assertTrue(fireableTransitions.containsAll(expected));
		Assert.assertTrue(fireableTransitions.size() == expected.size());

		// fire cId-...9511
		petriNet = simulator.simulateStep(petriNet, "").getResultingPnmlPetriNet();
		fireableTransitions = simulator.getPossibleFiringTransitions(petriNet).getFireableTransitions();
		
		Assert.assertNotNull(fireableTransitions);
		expected = Arrays.asList(new String[]{"cId-778302279272294019512","cId-77830227927229401959"});
		Assert.assertTrue(fireableTransitions.containsAll(expected));
		Assert.assertTrue(fireableTransitions.size() == expected.size());
	}
	
	@Test
	void testManyPossibleFiringTransitions() throws ImportException, InvalidIDException, IllegalPetriNetTypeException, IOException {
		Simulator simulator = new Simulator(pasippPath);
		List<String> fireableTransitions = simulator.getPossibleFiringTransitions(new File("src/test/resources/manyFireableTransitions.pnml")).getFireableTransitions();
		Assert.assertEquals(15, fireableTransitions.size());
		for (int i = 1; i < 16; i++) {
			Assert.assertTrue(fireableTransitions.contains("transition" + i));
		}
	}
	
	@Test
	void testCycle() throws ImportException, InvalidIDException, IllegalPetriNetTypeException, IOException {
		Simulator simulator = new Simulator(pasippPath);
		FireableTransitionsResult result = simulator.getPossibleFiringTransitions(new File("src/test/resources/cycle.pnml"));
		List<String> fireableTransitions = result.getFireableTransitions();
		
		Assert.assertNotNull(fireableTransitions);
		List<String> expected = Arrays.asList(new String[]{"transitionA","transitionB"});
		Assert.assertTrue(fireableTransitions.containsAll(expected));
		Assert.assertTrue(fireableTransitions.size() == expected.size());
		Assert.assertTrue(result.getCycles().hasBackwardCycle());
		Assert.assertTrue(result.getCycles().hasForwardCycle());
		
		result = simulator.getPossibleFiringTransitions(new File("src/test/resources/simpleNet.pnml"));
		fireableTransitions = result.getFireableTransitions();
		
		Assert.assertNotNull(fireableTransitions);
		expected = Arrays.asList(new String[]{"transitionA"});
		Assert.assertTrue(fireableTransitions.containsAll(expected));
		Assert.assertTrue(fireableTransitions.size() == expected.size());
		Assert.assertFalse(result.getCycles().hasBackwardCycle());
		Assert.assertFalse(result.getCycles().hasForwardCycle());
		
		CycleObject cycles = simulator.hasCycles(new File("src/test/resources/cycle.pnml"));
		Assert.assertTrue(cycles.hasBackwardCycle());
		Assert.assertTrue(cycles.hasForwardCycle());
		
		cycles = simulator.hasCycles(new File("src/test/resources/simpleNet.pnml"));
		Assert.assertFalse(cycles.hasBackwardCycle());
		Assert.assertFalse(cycles.hasForwardCycle());

		cycles = simulator.hasCycles(new File("src/test/resources/cycleAdd.pnml"));
		Assert.assertTrue(cycles.hasBackwardCycle());
		Assert.assertTrue(cycles.hasForwardCycle());
		
		cycles = simulator.hasCycles(new File("src/test/resources/cycleRemove.pnml"));
		Assert.assertTrue(cycles.hasBackwardCycle());
		Assert.assertTrue(cycles.hasForwardCycle());
	}
	
	@Test
	void testManyMarkings() throws ImportException, InvalidIDException, IllegalPetriNetTypeException, IOException {
		Simulator simulator = new Simulator(pasippPath);
		PetriNet petriNet = simulator.simulateStep(new File("src/test/resources/manyMarkings.pnml"), "").getResultingPnmlPetriNet();
		int i = 0;
		Page page = petriNet.getPages().get(0);
		List<PnObject> pnmlObjects = page.getObjects();
		Long expected = 1L;
		for(PnObject pnmlObject : pnmlObjects) {
			if (pnmlObject instanceof Place) {
				Place place = (Place) pnmlObject;
				Assert.assertNotNull(place.getInitialMarking());
				Assert.assertNotNull(place.getInitialMarking().getText());
				Assert.assertEquals(expected, place.getInitialMarking().getText());
				i++;
			}
		}
		Assert.assertEquals(15, i);
	}
	/*
	@Test
	void testSpecialCharacters() throws ImportException, InvalidIDException, IllegalPetriNetTypeException, IOException {
		Simulator simulator = new Simulator(pasippPath);
		PetriNet petriNet = simulator.simulateStep(new File("src/test/resources/specialCharacters.pnml"), "").getResultingPnmlPetriNet();
		Page page = petriNet.getPages().get(0);
		List<PnObject> pnmlObjects = page.getObjects();
		List<String> ids = new LinkedList<String>();
		for(PnObject pnmlObject : pnmlObjects) {
			if (pnmlObject instanceof Place) {
				Place place = (Place) pnmlObject;
				System.out.println("id = " + place.getId());
				ids.add(place.getId());
			}
		}
		Assert.assertEquals(9, ids.size());
		Assert.assertTrue(ids.contains("place1"));
		Assert.assertTrue(ids.contains("place-2"));
		Assert.assertTrue(ids.contains("place_3"));
		Assert.assertTrue(ids.contains("_place4"));
		
		System.out.println(ids);
		
		Assert.assertTrue(ids.contains("������"));
		Assert.assertTrue(ids.contains("������"));
	}*/
	
	@Test
	void testInvalidId() {
		ExpectedException.none().expect(ImportException.class);
		Simulator simulator = new Simulator(pasippPath);
		Assertions.assertThrows(ImportException.class, () -> simulator.simulateStep(new File("src/test/resources/invalidID.pnml"), ""));
	}
	
	@Test
	void testEmptyPage() throws ImportException, InvalidIDException, IllegalPetriNetTypeException, IOException {
		Simulator simulator = new Simulator(pasippPath);
		File pnmlFile = new File("src/test/resources/emptyPage.pnml");
		List<String> transitionIds = simulator.getPossibleFiringTransitions(pnmlFile).getFireableTransitions();
		PetriNet petriNet = simulator.simulateStep(pnmlFile, "").getResultingPnmlPetriNet();

		Assert.assertTrue(transitionIds.size() == 0);
		Assert.assertTrue(petriNet.getPages().size() == 1);
		Assert.assertTrue(petriNet.getPages().get(0).getObjects().size() == 0);
	}
	
	@Test
	void testNoPage() throws ImportException, InvalidIDException {
		Simulator simulator = new Simulator(pasippPath);
		File pnmlFile = new File("src/test/resources/noPage.pnml");
		Assertions.assertThrows(ImportException.class, () -> simulator.getPossibleFiringTransitions(pnmlFile));
		Assertions.assertThrows(ImportException.class, () -> simulator.simulateStep(pnmlFile, ""));
	}
	
	@Test
	void testNoNet() throws ImportException, InvalidIDException {
		Simulator simulator = new Simulator(pasippPath);
		File pnmlFile = new File("src/test/resources/noNet.pnml");
		Assertions.assertThrows(ImportException.class, () -> simulator.getPossibleFiringTransitions(pnmlFile));
		Assertions.assertThrows(ImportException.class, () -> simulator.simulateStep(pnmlFile, ""));
	}
	
	@Test
	void testNoContent() throws ImportException, InvalidIDException {
		Simulator simulator = new Simulator(pasippPath);
		File pnmlFile = new File("src/test/resources/noContent.pnml");
		Assertions.assertThrows(ImportException.class, () -> simulator.getPossibleFiringTransitions(pnmlFile));
		Assertions.assertThrows(ImportException.class, () -> simulator.simulateStep(pnmlFile, ""));
	}

}
