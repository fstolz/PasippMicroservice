package de.fzi.pna.prolog.simulator;

public class IllegalPetriNetTypeException extends Exception {
    // standard serialVersionUID
	private static final long serialVersionUID = 1L;

	public IllegalPetriNetTypeException() {}

    public IllegalPetriNetTypeException(String message)
    {
       super(message);
    }
}
