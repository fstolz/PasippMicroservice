package de.fzi.pna.prolog.server;

import javax.ws.rs.Path;

import de.fzi.pna.prolog.simulator.CycleObject;
import fr.lip6.move.pnml.ptnet.PetriNetDoc;

/**
 * Root resource (exposed at "simulatesteppnml" path)
 */
@Path("simulatesteppnml")
public class SimulateStepRequestPnml extends SimulateStepRequest {
	@Override
	protected String getReturnString(PetriNetDoc pnmlDoc, CycleObject cycles) {
		return prettifyXML(pnmlDoc.toPNML());
	}

}
