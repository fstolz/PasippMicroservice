package de.fzi.pna.prolog.simulator;

public class ReachabilityTreeResult {
	private Tree reachabilityTree;
	private CycleObject cycles;
	
	public ReachabilityTreeResult(Tree tree, CycleObject cycles) {
		this.reachabilityTree = tree;
		this.cycles = cycles;
	}
	
	public Tree getReachabilityTree() {
		return reachabilityTree;
	}
	
	public CycleObject getCycles() {
		return cycles;
	}
}
