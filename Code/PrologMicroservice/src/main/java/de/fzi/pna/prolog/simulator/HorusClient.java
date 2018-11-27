package de.fzi.pna.prolog.simulator;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

public class HorusClient {

	public String convertHorusToPnml(File horusFile) {
		Client client = ClientBuilder.newClient();
		client.register(MultiPartFeature.class);
		
    	WebTarget target;
    	target = client.target("http://localhost:8090/api/petrinet/transformHorus");
    	
    	MultiPart multiPart = new MultiPart();
    	multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
    	
    	FileDataBodyPart pnmlBodyPart = new FileDataBodyPart("pnmlFile", horusFile);
    	multiPart.bodyPart(pnmlBodyPart);
    	
    	Builder requestBuilder = target.request();
    	Entity<MultiPart> e = Entity.entity(multiPart, multiPart.getMediaType());
//    	Response response = requestBuilder.post(e);
    	String pnmlString = requestBuilder.post(e, String.class);
    	
		pnmlString = removeErrors(pnmlString);
    	
		return pnmlString;
	}

	private String removeErrors(String pnmlString) {
    	pnmlString = pnmlString.substring(pnmlString.indexOf('\n')+1);
    	pnmlString = pnmlString.substring(pnmlString.indexOf('\n')+1);
    	pnmlString = pnmlString.substring(pnmlString.indexOf('\n')+1);
    	pnmlString = "<pnml xmlns=\"http://www.pnml.org/version-2009/grammar/pnml\">\n" + pnmlString;
    	
		// width has to be >0
		pnmlString = pnmlString.replace("width=\"0.0\"", "width=\"1\"");
		
		// Horus has a graphics element without x/y information that defines background color
		// PNML framework does not support graphics elements without x/y information
		pnmlString = pnmlString.replaceFirst(".*<graphics>.*\n.*<position/>.*\n.*\n.*</graphics>\n", "");
		
		// pnmlcoremodel does not support attribute "initialMarking" that is set by HORUS / by PetriAnalyzer
		pnmlString = pnmlString.replace("http://www.pnml.org/version-2009/grammar/pnmlcoremodel", "http://www.pnml.org/version-2009/grammar/ptnet");
		
		// id may not consist only of digits
//		pnmlString = pnmlString.replace("net id=\"0\"", "net id=\"net0\"");
		
		String regex;
		Pattern p;
		Matcher m;
		
		regex = "<net id=\"\\d+\"";
		p = Pattern.compile(regex);  // insert your pattern here
		m = p.matcher(pnmlString);
		while (m.find()) {
			int position = m.start() + 9;
			pnmlString = pnmlString.substring(0, position) + "net" + pnmlString.substring(position);
			m.reset();
			m = p.matcher(pnmlString);
		}

		regex = "x=\\\"\\d+\\.0";
		p = Pattern.compile(regex);  // insert your pattern here
		m = p.matcher(pnmlString);
		while (m.find()) {
			int position = m.end();
			pnmlString = pnmlString.substring(0, position - 2) + pnmlString.substring(position);
			m.reset();
			m = p.matcher(pnmlString);
		}
		
		regex = "y=\\\"\\d+\\.0";
		p = Pattern.compile(regex);  // insert your pattern here
		m = p.matcher(pnmlString);
		while (m.find()) {
			int position = m.end();
			pnmlString = pnmlString.substring(0, position - 2) + pnmlString.substring(position);
			m.reset();
			m = p.matcher(pnmlString);
		}
		
		regex = "width=\\\"\\d+\\.0";
		p = Pattern.compile(regex);  // insert your pattern here
		m = p.matcher(pnmlString);
		while (m.find()) {
			int position = m.end();
			pnmlString = pnmlString.substring(0, position - 2) + pnmlString.substring(position);
			m.reset();
			m = p.matcher(pnmlString);
		}
		
		return pnmlString;
	}

}
