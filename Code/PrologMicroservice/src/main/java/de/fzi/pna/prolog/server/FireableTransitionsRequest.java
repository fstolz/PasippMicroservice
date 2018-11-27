package de.fzi.pna.prolog.server;

import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import de.fzi.pna.prolog.simulator.Simulator;
import de.fzi.pna.prolog.simulator.FireableTransitionsResult;
import de.fzi.pna.prolog.simulator.CycleObject;


/**
 * Root resource (exposed at "fireabletransitions" path)
 */
@Path("fireabletransitions")
public class FireableTransitionsRequest extends SimulatorRequest {
	
    @Context
	Configuration config;
	
    @Override
    @POST
    @Produces(MediaType.APPLICATION_XML)
    // @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    // curl -F pnml="@./piscine.txt" -X POST http://localhost:8080/prologService/fireabletransitions
    public String requestMethod(final FormDataMultiPart multiPart) {
		System.out.println("received FireableTransitions POST request");
		
		// create new Simulator
		Simulator simulator;
		simulator = getSimulator(config);
		
		List<String> transitions;
		
		FormDataBodyPart pnmlBodyPart = multiPart.getField("pnml");
		if (pnmlBodyPart == null)
			return getErrorString("missing PNML parameter");
		
		// get the transitions that could possibly fire, write them to a String, and return them
		try {
			String pnmlString = getStringFromBodyPart(pnmlBodyPart);
			FireableTransitionsResult result = simulator.getPossibleFiringTransitions(pnmlString);
			transitions = result.getFireableTransitions();
			return getReturnString(transitions, result.getCycles());
		} catch (Exception e) {
			return getErrorString(e);
		}
    }
    
    private String getReturnString(List<String> transitions, CycleObject cycles) {
    	return buildXMLString(transitions, cycles);
    }

    private String buildXMLString(List<String> transitions, CycleObject cycles) {
		String xmlString = "";
		xmlString += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		xmlString += "\n<fireableTransitionsResult>";
		xmlString += "\n  <cycles>";
		xmlString += "\n    <hasForwardCycle>" + cycles.hasForwardCycle() + "</hasForwardCycle>";
		xmlString += "\n    <hasBackwardCycle>" + cycles.hasBackwardCycle() + "</hasBackwardCycle>";
		xmlString += "\n  </cycles>";
		xmlString += "\n  <fireableTransitions>";
		for (String marking : transitions) {
			xmlString += "\n    <id>" + marking + "</id>";
		}
		xmlString += "\n  </fireableTransitions>";
		xmlString += "\n</fireableTransitionsResult>";
		return xmlString;
	}
}
