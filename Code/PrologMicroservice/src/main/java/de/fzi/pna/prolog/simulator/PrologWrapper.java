package de.fzi.pna.prolog.simulator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class PrologWrapper {
	private Process arityProcess;
	private InputStream fromArity;
	private OutputStream toArity;
	private PrintWriter toArityWriter;
	
	public PrologWrapper(String pasippFolder) throws IOException {
		ProcessBuilder builder = new ProcessBuilder("api32");
		builder.directory(new File(pasippFolder));
//		try {
			arityProcess = builder.start();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		fromArity = arityProcess.getInputStream();
		toArity = arityProcess.getOutputStream();
		toArityWriter = new PrintWriter(toArity, true);
	}
	
	public void close() throws IOException {
		if (arityProcess != null) {
			if (arityProcess.isAlive()) {
				arityProcess.destroy();
			}
		}
	}
	
	public void callPrologCommand(String command) {
		toArityWriter.println(command);
	}
	
	public String getPrologResponse() {
		try {
			Thread.sleep(100);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		try {
			byte[] buffer = new byte[4000];
			int outCount = fromArity.available();
			String returnString = "";
			while(outCount > 0) {
				int n = fromArity.read(buffer, 0, Math.min(outCount, buffer.length));
				String next = new String(buffer, 0, n);
				returnString += next;
				outCount = fromArity.available();
			}
			return returnString;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
