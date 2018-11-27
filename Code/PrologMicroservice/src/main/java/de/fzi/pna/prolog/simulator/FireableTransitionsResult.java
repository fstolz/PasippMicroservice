package de.fzi.pna.prolog.simulator;

import java.util.List;

public class FireableTransitionsResult {
	private List<String> fireableTransitions;
	private CycleObject cycles;

	public FireableTransitionsResult(List<String> fireableTransitions, CycleObject cycles) {
		this.fireableTransitions = fireableTransitions;
		this.cycles = cycles;
	}

	public List<String> getFireableTransitions() {
		return fireableTransitions;
	}

	public CycleObject getCycles() {
		return cycles;
	}

}
