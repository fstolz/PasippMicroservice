package de.fzi.pna.prolog.server;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import de.fzi.pna.prolog.simulator.PasippPathException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// mvn -q clean compile exec:java
// mvn -q clean compile exec:java -Dexec.args="-pasipp 'C:\Users\fabian\Documents\KIT\masterarbeit\Code\PASIPP\Diskette PASIPP 1991\SOURCES' -uri 'http://localhost:8080/prologService/'"
// mvn install:install-file -Dfile=C:\Users\fabian\eclipse-workspace\prologService.jar -DgroupId=de.fzi.pna -DartifactId=de.fzi.pna.prologService -Dversion=0.0.1 -Dpackaging=jar
// mvn clean compile assembly:single
// java -jar prolog-service-0.0.1-SNAPSHOT-jar-with-dependencies.jar

/**
 * Main class.
 *
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    private static final String defaultBaseUri = "http://localhost:8080/prologService/";
    private static final String defaultPasippPath = "C:/PASIPP/pasippSources";
    private static final Boolean defaultYEd = false;
    private static String baseUri;
    private static String pasippPath;
    private static Boolean yEd;

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer(String pasippPath) {
    	Map<String, Object> propertiesMap = new HashMap<String, Object>();
        propertiesMap.put("pasippPath", pasippPath);
        propertiesMap.put("yEd", yEd);
    	
        // create a resource config that scans for JAX-RS resources and providers
        // in org.glassfish.jersey.archetypes.simple_service package
        final ResourceConfig rc = new ResourceConfig().packages("de.fzi.pna.prolog")
        		.packages("de.fzi.pna.prolog.multipart")
        		.register(MultiPartFeature.class)
        		.addProperties(propertiesMap);

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(baseUri), rc);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
    	pasippPath = defaultPasippPath;
    	baseUri = defaultBaseUri;
    	yEd = defaultYEd;
    	for (int i = 0; i < args.length; i++) {
    		String arg = args[i];
    		switch(arg.toLowerCase()) {
    		case "-pasipp":
    		case "-pasipppath":
    			if (i+1 < args.length) {
    				pasippPath = args[i+1];
    			}
    			break;
    		case "-baseurl":
    		case "-url":
    		case "-uri":
    		case "-baseuri":
    			if (i+1 < args.length) {
    				baseUri = args[i+1];
    			}
    			break;
    		case "-yed":
    			yEd = true;
    			break;
    		}
    	}
        System.out.println("PASIPP path set to " + pasippPath);
        System.out.println("Base URI set to " + baseUri);
        System.out.println("yEd labels switched " + (yEd ? "on" : "off"));

        try {
            checkPasippPath();
        } catch(PasippPathException e) {
            System.out.println("ERROR: Indicated path does not contain PASIPP sources (or default path does not contain PASIPP sources, if no path was given)");
            return;
        }

        final HttpServer server = startServer(pasippPath);
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", baseUri));
        System.in.read();
        server.shutdownNow();
    }
    
    protected static void handleError() {
    	
    }

    private static void checkPasippPath() throws PasippPathException {
        File pasippFolder = new File(pasippPath);
        String[] fileNamesArray = pasippFolder.list();
        if (fileNamesArray == null) throw new PasippPathException();
        List<String> fileNames = Arrays.asList(fileNamesArray);
        if (!fileNames.contains("PASIPP.ARI")) throw new PasippPathException();
        if (!fileNames.contains("BUILD.ARI")) throw new PasippPathException();
        if (!fileNames.contains("SIMULAT.ARI")) throw new PasippPathException();
        if (!fileNames.contains("HELP1.ARI")) throw new PasippPathException();
        if (!fileNames.contains("NETWORK.ARI")) throw new PasippPathException();
        if (!fileNames.contains("NET_ANA.ARI")) throw new PasippPathException();
        if (!fileNames.contains("STATIC.ARI")) throw new PasippPathException();
        if (!fileNames.contains("DYNAMIC.ARI")) throw new PasippPathException();
        if (!fileNames.contains("HELP.ARI")) throw new PasippPathException();
        if (!fileNames.contains("OPTIONS.ARI")) throw new PasippPathException();
        if (!fileNames.contains("OPERAT.ARI")) throw new PasippPathException();
        if (!fileNames.contains("TREE.ARI")) throw new PasippPathException();
        if (!fileNames.contains("INIT.ARI")) throw new PasippPathException();
        if (!fileNames.contains("SPECIAL.ARI")) throw new PasippPathException();
        if (!fileNames.contains("ANALYSE.ARI")) throw new PasippPathException();
    }
}

