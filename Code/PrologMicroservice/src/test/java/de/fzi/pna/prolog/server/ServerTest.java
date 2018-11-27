package de.fzi.pna.prolog.server;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import junit.framework.Assert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

public class ServerTest extends JerseyTest {
	private static final String pasippPath = "C:/PASIPP/pasippSources";
	private static final Boolean yEd = false;
	private static String pnmlString;

	private static final String resourcePath = "src/test/resources/";
	private static final String pnmlPath = resourcePath + "rome.pnml";
	
	@BeforeClass
	public static void setupTest() {
		StringBuilder sb = new StringBuilder();

		try (BufferedReader br = new BufferedReader(new FileReader(pnmlPath))) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				if (sb.length() > 0) {
					sb.append("\n");
				}
				sb.append(sCurrentLine);
			}
			pnmlString = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    @Override
    protected Application configure() {
    	Map<String, Object> propertiesMap = new HashMap<String, Object>();
        propertiesMap.put("pasippPath", pasippPath);
        propertiesMap.put("yEd", yEd);
    	
        // create a resource config that scans for JAX-RS resources and providers
        // in org.glassfish.jersey.archetypes.simple_service package
        final ResourceConfig rc = new ResourceConfig().packages("de.fzi.pna.prolog")
        		.packages("de.fzi.pna.prolog.multipart")
        		.register(MultiPartFeature.class)
        		.addProperties(propertiesMap);
        
        return rc;
    }
    
    @Override
    protected void configureClient(ClientConfig config) {
        config.register(MultiPartFeature.class);
    }
    
    @Test
    public void testExceedResources() {
    	WebTarget target;
    	target = target("reachabilitytree");
    	
    	MultiPart multiPart = new MultiPart();
    	multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
    	
    	FileDataBodyPart pnmlBodyPart = new FileDataBodyPart("pnml", new File(resourcePath + "tooManyDecisions.pnml"));
    	multiPart.bodyPart(pnmlBodyPart);
    	
    	Builder requestBuilder = target.request(MediaType.APPLICATION_XML);
    	Entity<MultiPart> e = Entity.entity(multiPart, multiPart.getMediaType());
    	String str = requestBuilder.post(e, String.class);
    	
    	System.out.println(str);
    	Assert.assertTrue(str.contains("<error>The Prolog process exceeded its available ressources</error>"));
    }
    
    @Test
    public void testUnsupportedOrNoPetriNetType() {
    	WebTarget target;
    	target = target("hascycles");
    	
    	MultiPart multiPart = new MultiPart();
    	multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
    	
    	FileDataBodyPart pnmlBodyPart = new FileDataBodyPart("pnml", new File(resourcePath + "ptNetNoType.pnml"));
    	multiPart.bodyPart(pnmlBodyPart);
    	
    	Builder requestBuilder = target.request(MediaType.APPLICATION_XML);
    	Entity<MultiPart> e = Entity.entity(multiPart, multiPart.getMediaType());
    	String str = requestBuilder.post(e, String.class);
    	
    	System.out.println(str);
    	Assert.assertTrue(str.contains("<error>Unexpected exception 'InvalidIDException'</error>"));
    	
    	
    	MultiPart multiPart2 = new MultiPart();
    	multiPart2.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
    	
    	pnmlBodyPart = new FileDataBodyPart("pnml", new File(resourcePath + "ptNetUndefinedType.pnml"));
    	multiPart2.bodyPart(pnmlBodyPart);
    	
    	requestBuilder = target.request(MediaType.APPLICATION_XML);
    	e = Entity.entity(multiPart2, multiPart2.getMediaType());
    	str = requestBuilder.post(e, String.class);
    	
    	System.out.println(str);
    	Assert.assertTrue(str.contains("<error>Could not convert PNML string / file to a PNML object (ImportException)</error>"));
    	
    	
    	MultiPart multiPart3 = new MultiPart();
    	multiPart3.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
    	
    	pnmlBodyPart = new FileDataBodyPart("pnml", new File(resourcePath + "ptNetUnsupportedType.pnml"));
    	multiPart3.bodyPart(pnmlBodyPart);
    	
    	requestBuilder = target.request(MediaType.APPLICATION_XML);
    	e = Entity.entity(multiPart3, multiPart3.getMediaType());
    	str = requestBuilder.post(e, String.class);
    	
    	System.out.println(str);
    	Assert.assertTrue(str.contains("<error>Wrong Petri Net type; must be a PNML Place/Transition net</error>"));
    }
    
    @Test
    public void testInvalidPnml() {
    	WebTarget target;
    	target = target("hascycles");
    	
    	MultiPart multiPart = new MultiPart();
    	multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
    	
    	String invalidPnmlString = pnmlString.substring(1);
    	FormDataBodyPart pnmlBodyPart = new FormDataBodyPart("pnml", invalidPnmlString);
    	multiPart.bodyPart(pnmlBodyPart);
    	
    	Builder requestBuilder = target.request(MediaType.APPLICATION_XML);
    	Entity<MultiPart> e = Entity.entity(multiPart, multiPart.getMediaType());
    	String str = requestBuilder.post(e, String.class);
    	
    	System.out.println(str);
    	Assert.assertTrue(str.contains("<error>Could not convert PNML string / file to a PNML object (ImportException)</error>"));
    }
    
    @Test
    public void testNoPnml() {
    	WebTarget target;
    	target = target("simulatestep");
    	
    	MultiPart multiPart = new MultiPart();
    	multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
    	
    	FormDataBodyPart transitionBodyPart = new FormDataBodyPart("id", "transitionA");
    	multiPart.bodyPart(transitionBodyPart);
    	
    	Builder requestBuilder = target.request(MediaType.APPLICATION_XML);
    	Entity<MultiPart> e = Entity.entity(multiPart, multiPart.getMediaType());
    	String str = requestBuilder.post(e, String.class);
    	
    	System.out.println(str);
    	Assert.assertTrue(str.contains("<error>missing PNML parameter</error>"));
    	
    	
    	target = target("hascycles");
    	
    	requestBuilder = target.request(MediaType.APPLICATION_XML);
    	str = requestBuilder.post(e, String.class);
    	
    	System.out.println(str);
    	Assert.assertTrue(str.contains("<error>missing PNML parameter</error>"));
    }
    
    @Test
    @DisplayName("Test file upload")
    public void testFile() {
    	WebTarget target;
    	target = target("fireabletransitions");
    	
    	MultiPart multiPart = new MultiPart();
    	multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
    	
    	FileDataBodyPart pnmlBodyPart = new FileDataBodyPart("pnml", new File(pnmlPath));
    	multiPart.bodyPart(pnmlBodyPart);
    	
    	Builder requestBuilder = target.request(MediaType.APPLICATION_XML);
    	Entity<MultiPart> e = Entity.entity(multiPart, multiPart.getMediaType());
    	String str = requestBuilder.post(e, String.class);
    	
    	System.out.println(str);
    	Assert.assertFalse(str.contains("error"));
    }
    
    @Test
    @DisplayName("Test fireableTransitions functionality")
    public void fireabletransitionsTest() {
    	WebTarget target;
    	target = target("fireabletransitions");
    	
    	MultiPart multiPart = new MultiPart();
    	multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
    	
    	FormDataBodyPart pnmlBodyPart = new FormDataBodyPart("pnml", pnmlString);
    	multiPart.bodyPart(pnmlBodyPart);
    	
    	Builder requestBuilder = target.request(MediaType.APPLICATION_XML);
    	Entity<MultiPart> e = Entity.entity(multiPart, multiPart.getMediaType());
    	String str = requestBuilder.post(e, String.class);
    	
    	System.out.println(str);
    	Assert.assertFalse(str.contains("error"));
    }
    
    @Test
    @DisplayName("Test hasCycles functionality")
    public void hascyclesTest() {
    	WebTarget target;
    	target = target("hascycles");
    	
    	MultiPart multiPart = new MultiPart();
    	multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
    	
    	FormDataBodyPart pnmlBodyPart = new FormDataBodyPart("pnml", pnmlString);
    	multiPart.bodyPart(pnmlBodyPart);
    	
    	Builder requestBuilder = target.request(MediaType.APPLICATION_XML);
    	Entity<MultiPart> e = Entity.entity(multiPart, multiPart.getMediaType());
    	String str = requestBuilder.post(e, String.class);
    	
    	System.out.println(str);
    	Assert.assertFalse(str.contains("error"));
    }
    
    @Test
    @DisplayName("Test simulateStep functionality")
    public void simulatestepTest() {
    	WebTarget target;
    	target = target("simulatestep");
    	
    	MultiPart multiPart = new MultiPart();
    	multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
    	
    	FormDataBodyPart pnmlBodyPart = new FormDataBodyPart("pnml", pnmlString);
    	multiPart.bodyPart(pnmlBodyPart);
    	
    	FormDataBodyPart transitionBodyPart = new FormDataBodyPart("id", "transitionA");
    	multiPart.bodyPart(transitionBodyPart);
    	
    	Builder requestBuilder = target.request(MediaType.APPLICATION_XML);
    	Entity<MultiPart> e = Entity.entity(multiPart, multiPart.getMediaType());
    	String str = requestBuilder.post(e, String.class);
    	
    	System.out.println(str);
    	Assert.assertFalse(str.contains("error"));
    }
    
    @Test
    @DisplayName("Test simulateStep functionality when no Transition ID is given")
    public void simulatestepTestNoTransitionId() {
    	WebTarget target;
    	target = target("simulatestep");
    	
    	MultiPart multiPart = new MultiPart();
    	multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
    	
    	FormDataBodyPart pnmlBodyPart = new FormDataBodyPart("pnml", pnmlString);
    	multiPart.bodyPart(pnmlBodyPart);
    	
    	Builder requestBuilder = target.request(MediaType.APPLICATION_XML);
    	Entity<MultiPart> e = Entity.entity(multiPart, multiPart.getMediaType());
    	String str = requestBuilder.post(e, String.class);
    	
    	System.out.println(str);
    	Assert.assertFalse(str.contains("error"));
    }
    
    @Test
    @DisplayName("Test reachabilityTree functionality")
    public void reachabilitytreeTest() {
    	WebTarget target;
    	target = target("reachabilitytree");
    	
    	MultiPart multiPart = new MultiPart();
    	multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
    	
    	FormDataBodyPart pnmlBodyPart = new FormDataBodyPart("pnml", pnmlString);
    	multiPart.bodyPart(pnmlBodyPart);
    	
    	Builder requestBuilder = target.request(MediaType.APPLICATION_XML);
    	Entity<MultiPart> e = Entity.entity(multiPart, multiPart.getMediaType());
    	String str = requestBuilder.post(e, String.class);
    	
    	System.out.println(str);
    	Assert.assertFalse(str.contains("error"));
    }

}
