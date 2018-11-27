package de.fzi.pna.prolog.simulator;

import fr.lip6.move.pnml.ptnet.PetriNet;

public class SimulationStepResult {
	private PetriNet resultingPnmlPetriNet;
	private CycleObject cycles;

	public SimulationStepResult(PetriNet resultingPNML, CycleObject cycles) {
		this.resultingPnmlPetriNet = resultingPNML;
		this.cycles = cycles;
	}

	public PetriNet getResultingPnmlPetriNet() {
		return resultingPnmlPetriNet;
	}

	public CycleObject getCycles() {
		return cycles;
	}

}
