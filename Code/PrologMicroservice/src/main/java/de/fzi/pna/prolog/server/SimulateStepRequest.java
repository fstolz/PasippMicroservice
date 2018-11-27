package de.fzi.pna.prolog.server;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import de.fzi.pna.prolog.simulator.SimulationStepResult;
import de.fzi.pna.prolog.simulator.Simulator;
import de.fzi.pna.prolog.simulator.CycleObject;
import fr.lip6.move.pnml.ptnet.PetriNet;
import fr.lip6.move.pnml.ptnet.PetriNetDoc;


/**
 * Root resource (exposed at "simulatestep" path)
 */
@Path("simulatestep")
public class SimulateStepRequest extends SimulatorRequest {
	
    @Context
	Configuration config;
	
    @Override
    @POST
    @Produces(MediaType.APPLICATION_XML)
    // curl -F "pnml=@./philo.txt" -F "id=cId199-i943123747" -X POST http://localhost:8080/prologService/simulatestep
    public String requestMethod(final FormDataMultiPart multiPart) {
		System.out.println("received SimulateStep POST request");

		// create new Simulator
		Simulator simulator;
		simulator = getSimulator(config);

		FormDataBodyPart pnmlBodyPart = multiPart.getField("pnml");
		FormDataBodyPart idBodyPart = multiPart.getField("id");
		if (pnmlBodyPart == null)
			return getErrorString("missing PNML parameter");
		
		// try to write the PNML String to pnmlFile.pnml
		// File file = new File("pnmlFile.pnml");
		try {
			String transitionId = null;
			if (idBodyPart != null) {
				transitionId = idBodyPart.getValue();
			}
			String pnmlString = getStringFromBodyPart(pnmlBodyPart);
		
			// simulate a step; get the resulting PNML petri net document
			SimulationStepResult result = simulator.simulateStep(pnmlString, transitionId);
			PetriNet pnmlNet = result.getResultingPnmlPetriNet();
			PetriNetDoc pnmlDoc = pnmlNet.getContainerPetriNetDoc();
			
			return getReturnString(pnmlDoc, result.getCycles());
		} catch (Exception e) {
			return getErrorString(e);
		}
    }
    
    protected String getReturnString(PetriNetDoc pnmlDoc, CycleObject cycles) {
    	return buildXMLString(pnmlDoc, cycles);
    }
    
	private String buildXMLString(PetriNetDoc pnmlDoc, CycleObject cycles) {
		String xmlString = "";
		xmlString += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		xmlString += "\n<simulateStepResult>";
		xmlString += "\n  <cycles>";
		xmlString += "\n    <hasForwardCycle>" + cycles.hasForwardCycle() + "</hasForwardCycle>";
		xmlString += "\n    <hasBackwardCycle>" + cycles.hasBackwardCycle() + "</hasBackwardCycle>";
		xmlString += "\n  </cycles>";
		xmlString += "\n  <resultingPetriNet>";
		String pnmlOutputString = pnmlDoc.toPNML();
		pnmlOutputString = "\n" + prettifyXML(pnmlOutputString, 4);
		xmlString += pnmlOutputString;
		xmlString += "\n  </resultingPetriNet>";
		xmlString += "\n</simulateStepResult>";
		return xmlString;
	}
}
