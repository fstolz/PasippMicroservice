package de.fzi.pna.prolog.simulator;

public class CycleObject {
	private boolean forwardCycle = false;
	private boolean backwardCycle = false;
	
	public boolean hasForwardCycle() {
		return forwardCycle;
	}
	public void setForwardCycle(boolean forwardCycle) {
		this.forwardCycle = forwardCycle;
	}
	public boolean hasBackwardCycle() {
		return backwardCycle;
	}
	public void setBackwardCycle(boolean backwardCycle) {
		this.backwardCycle = backwardCycle;
	}
}
