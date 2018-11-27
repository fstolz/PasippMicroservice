package de.fzi.pna.prolog.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import fr.lip6.move.pnml.framework.utils.exception.InvalidIDException;
import fr.lip6.move.pnml.framework.utils.exception.ImportException;

import de.fzi.pna.prolog.simulator.IllegalPetriNetTypeException;
import de.fzi.pna.prolog.simulator.PrologProcessExceededRessourcesException;
import de.fzi.pna.prolog.simulator.Simulator;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class SimulatorRequest {
	
	public Simulator getSimulator(Configuration config) {
		String pasippPath = (String) config.getProperties().get("pasippPath");
		Boolean yEd = (Boolean) config.getProperties().get("yEd");
		Simulator simulator = new Simulator(pasippPath, yEd);
		return simulator;
	}
	
	abstract public String requestMethod(final FormDataMultiPart multiPart);
	
    protected String getErrorString(String message) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "\n<error>"
				+ "" + message
				+ "</error>";
    }
    
    String getErrorString (Exception e) {
    	try {
    		throw e;
    	} catch (InvalidIDException ex) {
			return getErrorString("Encountered Invalid ID exception; petri net type might be missing");
    		// return getErrorString("Invalid id for the document workspace (InvalidIDException");
    	} catch (ImportException ex) {
			return getErrorString("Could not convert PNML string / file to a PNML object (ImportException)");
		} catch (IllegalPetriNetTypeException ex) {
			return getErrorString("Wrong Petri Net type; must be a PNML Place/Transition net");
//		} catch (OMException ex) {
//			return getErrorString("XML / PNML parsing error");
		// } catch (FileNotFoundException ex) {
    	// 	return getErrorString("Missing File. Maybe the PASIPP directory was not specified correctly");
    	} catch (IOException ex) {
			return getErrorString("Server-Side IO error");
		} catch (PrologProcessExceededRessourcesException ex) {
    		return getErrorString("The Prolog process exceeded its available ressources");
    	} catch (Exception ex) {
    		return getErrorString("Unknown error (" + e.getClass() + ")");
    	}
    }
    
    protected String prettifyXML(String xmlString) {
    	return prettifyXML(xmlString, 0);
    }
    
    protected String prettifyXML(String xmlString, int indents) {
    	try {	// if "try" succeeds, XML will be prettier; if not, return existing XML-String
			xmlString = xmlFormat(xmlString);
			xmlString = xmlString.trim();
			xmlString = addIndents(xmlString, indents);
		} catch (Exception e) {}
    	return xmlString;
    }
    protected String addIndents(String string, int indents) {
		if (indents>0) {
			String indent = "";
			for (int i = 0; i < indents; i++) {
				indent += " ";
			}
			if(!string.startsWith("\n")) {
				string = indent + string;
			}
			string = string.replaceAll("\n", "\n" + indent);
		}
		return string;
    }
    
    private String xmlFormat(String unformattedXml) {
    	try {
        	Document document = parseXmlFile(unformattedXml);
        	
        	
        	Transformer transformer = TransformerFactory.newInstance().newTransformer();
        	transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        	transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        	transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        	//initialize StreamResult with File object to save to file
        	StreamResult result = new StreamResult(new StringWriter());
        	
        	DOMSource source = new DOMSource(document);
        	transformer.transform(source, result);
        	String xmlString = result.getWriter().toString();
        	// System.out.println(xmlString);
        	return xmlString;
    	} catch (Exception e) {
    		return unformattedXml;
    	}
    }
    
    private Document parseXmlFile(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	protected String getStringFromBodyPart(FormDataBodyPart formDataBodyPart) throws IOException {
		String returnString = "";
    	if (formDataBodyPart.getMediaType().toString().equals(MediaType.TEXT_PLAIN)) {
    		returnString = formDataBodyPart.getValue();
    	} else {
    		InputStream in = formDataBodyPart.getEntityAs(InputStream.class);
    		
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
            	returnString += (returnString.equals("") ? line : "\n"+line);
            }
            reader.close();
    	}
		return returnString;
	}
    
}
