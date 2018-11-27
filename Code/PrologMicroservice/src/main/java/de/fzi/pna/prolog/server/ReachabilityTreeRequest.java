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
import de.fzi.pna.prolog.simulator.Tree;
import de.fzi.pna.prolog.simulator.ReachabilityTreeResult;
import de.fzi.pna.prolog.simulator.CycleObject;

/**
 * Root resource (exposed at "reachabilitytree" path)
 */
@Path("reachabilitytree")
public class ReachabilityTreeRequest extends SimulatorRequest {
	
    @Context
	Configuration config;
	
    @Override
    @POST
    @Produces(MediaType.APPLICATION_XML)
    // curl -F pnml="@./decisions.txt" -X POST http://localhost:8080/prologService/reachabilitytree
    public String requestMethod(final FormDataMultiPart multiPart) {
		System.out.println("received ReachabilityTree POST request");
		
		// create new Simulator
		Simulator simulator;
		simulator = getSimulator(config);
		
		FormDataBodyPart pnmlBodyPart = multiPart.getField("pnml");
		if (pnmlBodyPart == null)
			return getErrorString("missing PNML parameter");
		
		try {
			String pnmlString = getStringFromBodyPart(pnmlBodyPart);
			ReachabilityTreeResult result = simulator.getReachabilityTree(pnmlString);
			Tree tree = result.getReachabilityTree();
			return getReturnString(tree, result.getCycles());
		} catch (Exception e) {
			return getErrorString(e);
		}
    }
    
    protected String getReturnString(Tree tree, CycleObject cycles) {
    	return buildXMLString(tree, cycles);
    }

    private String buildXMLString(Tree tree, CycleObject cycles) {
		String xmlString = "";
		xmlString += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		xmlString += "\n<reachabilityTreeResult>";
		xmlString += "\n  <cycles>";
		xmlString += "\n    <hasForwardCycle>" + cycles.hasForwardCycle() + "</hasForwardCycle>";
		xmlString += "\n    <hasBackwardCycle>" + cycles.hasBackwardCycle() + "</hasBackwardCycle>";
		xmlString += "\n  </cycles>";
		xmlString += "\n  <Tree>";
		xmlString += "\n" + addIndents(tree.toGraphML(), 4);
		xmlString += "\n  </Tree>";
		xmlString += "\n</reachabilityTreeResult>";
		return xmlString;
	}
}
