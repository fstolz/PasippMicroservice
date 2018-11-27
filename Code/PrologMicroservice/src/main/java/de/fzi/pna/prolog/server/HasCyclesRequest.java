package de.fzi.pna.prolog.server;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import de.fzi.pna.prolog.simulator.Simulator;
import de.fzi.pna.prolog.simulator.CycleObject;

/**
 * Root resource (exposed at "hascycles" path)
 */
@Path("hascycles")
public class HasCyclesRequest extends SimulatorRequest {
	
    @Context
	Configuration config;
	
    @Override
    @POST
    @Produces(MediaType.APPLICATION_XML)
//    @Consumes("application/x-www-form-urlencoded")
//    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    // curl -X POST -F pnml="@./piscine.txt" http://localhost:8080/prologService/hascycles
    public String requestMethod(final FormDataMultiPart multiPart) {
    	System.out.println("received HasCycles POST request");
    	
    	// create new Simulator
		Simulator simulator;
		simulator = getSimulator(config);

    	FormDataBodyPart pnmlBodyPart = multiPart.getField("pnml");
		if (pnmlBodyPart == null)
			return getErrorString("missing PNML parameter");
    	
    	// get the transitions that could possibly fire, write them to a String, and return them
		try {
			String pnmlString = getStringFromBodyPart(pnmlBodyPart);
			CycleObject cycles = simulator.hasCycles(pnmlString);
			
			return getReturnString(cycles);
		} catch (Exception e) {
	    	e.printStackTrace();
			return getErrorString(e);
		}
    }
    
    private String getReturnString(CycleObject cycles) {
    	return buildXMLString(cycles);
    }

	private String buildXMLString(CycleObject cycles) {
		String xmlString = "";
		xmlString += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		xmlString += "\n<hasCyclesResult>";
		xmlString += "\n  <hasForwardCycle>" + cycles.hasForwardCycle() + "</hasForwardCycle>";
		xmlString += "\n  <hasBackwardCycle>" + cycles.hasBackwardCycle() + "</hasBackwardCycle>";
		xmlString += "\n</hasCyclesResult>";
		return xmlString;
	}
}
