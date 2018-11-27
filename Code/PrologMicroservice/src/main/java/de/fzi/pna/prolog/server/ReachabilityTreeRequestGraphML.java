package de.fzi.pna.prolog.server;

import javax.ws.rs.Path;

import de.fzi.pna.prolog.simulator.Tree;
import de.fzi.pna.prolog.simulator.CycleObject;

/**
 * Root resource (exposed at "reachabilitytreegraphml" path)
 */
@Path("reachabilitytreegraphml")
public class ReachabilityTreeRequestGraphML extends ReachabilityTreeRequest {

	@Override
    protected String getReturnString(Tree tree, CycleObject cycles) {
		return tree.toGraphML();
	}
}
