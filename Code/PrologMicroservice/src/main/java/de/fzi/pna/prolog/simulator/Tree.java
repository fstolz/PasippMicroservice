package de.fzi.pna.prolog.simulator;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.bidimap.AbstractDualBidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

public class Tree {
	private int nodeCount = 0;
	
	private AbstractDualBidiMap<String,PrologNetMarking> nodes;
	private Set<Edge> edges;

//	private PrologNetMarking originalMarking;
	
	public Tree() {
		this.nodes = new DualHashBidiMap<String, PrologNetMarking>();
		this.edges = new HashSet<Edge>();
	}
	
	public String addNode(PrologNetMarking netMarking) {
		if (nodes.containsValue(netMarking)) {
			return nodes.getKey(netMarking);
		} else {
			nodeCount++;
			String id = "netMarking"+nodeCount;
			nodes.put(id, netMarking);
			return id;
		}
	}
	
	public void addEdge(PrologNetMarking source, PrologNetMarking target) {
		String sourceId = addNode(source);
		String targetId = addNode(target);
		
		edges.add(new Edge(sourceId, targetId));
	}
//	public void addEdge(String sourceId, String targetId) {
//		edges.add(new Edge(sourceId, targetId));
//	}
	
	public String toGraphML() {
		String returnString = "";
		returnString = "<graphml\r\n" +
				"    xmlns=\"http://graphml.graphdrawing.org/xmlns\"  \r\n" + 
				"    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + 
				"    xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns \r\n" + 
				"        http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\r\n" + 
				"\r\n";
		
		if(Simulator.INCLUDE_YED_LABELS) {
			returnString = "<graphml\r\n" + 
					"    xmlns=\"http://graphml.graphdrawing.org/xmlns\"\r\n" + 
					"    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + 
					"    xmlns:y=\"http://www.yworks.com/xml/graphml\"\r\n" + 
					"    xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns http://www.yworks.com/xml/schema/graphml/1.0/ygraphml.xsd\">" +
					"\r\n" +
					"\r\n" +
					"    <key for=\"node\" id=\"d0\" yfiles.type=\"nodegraphics\"/>\r\n";
		}
		
		returnString += "    <key id=\"originalMarking\" for=\"node\" attr.name=\"originalMarking\" attr.type=\"boolean\">" + 
		"<default>false</default>" + 
		"</key>\r\n" + 
		"    <key id=\"placeId\" for=\"node\" attr.name=\"placeId\" attr.type=\"string\"/>\r\n" + 
		"    <key id=\"count\" for=\"node\" attr.name=\"count\" attr.type=\"string\"/>\r\n" + 
		"\r\n" + 
		"    <graph id=\"G\" edgedefault=\"directed\">";
		
		for (String nodeId : nodes.keySet()) {
			PrologNetMarking marking = nodes.get(nodeId);
			
			returnString += "\r\n        <node id=\"" + nodeId + "\">";
			returnString += marking.toGraphML(nodeId);
			returnString += "\r\n        </node>";
		}
		
		returnString += "\r\n";
		
		for (Edge edge : edges) {
			returnString += "\r\n        <edge source=\"" + edge.getSource() + "\" target=\"" + edge.getTarget() + "\"/>";
		}
		
		returnString += "\r\n    </graph>\r\n" +
				"</graphml>";
		return returnString;
	}
	
	public String toString() {
		return toGraphML();
	}
	
/*	public String toString() {
		String returnString = "<Tree>";
		for (Edge edge : edges) {
			returnString += "\n  <Edge>";
			returnString += getMarkingString(nodes.get(edge.getSource()), "Parent");
			returnString += getMarkingString(nodes.get(edge.getTarget()), "Child");
			returnString += "\n  </Edge>";	
		}
		returnString += "\n</Tree>";
		return returnString;
	}

	private String getMarkingString(PrologNetMarking netMarking, String type) {
		String returnString = "";
		if (netMarking.isOriginalMarking())
			returnString += "\n    <" + type + " isOriginalMarking='true'>";
		else
			returnString += "\n    <" + type + ">";
		returnString += "\n" + netMarking.toXMLString();
		returnString += "\n    </" + type + ">";
		return returnString;
	}*/
	
	/*
	public String toString() {
		String returnString = "<Tree>";
		for (PrologNetMarking key : graph.keySet()) {
			returnString += "\n  <Edge>";
			returnString += getMarkingString(key, "Parent");
			Set<PrologNetMarking> children = graph.get(key);
			for (PrologNetMarking child : children) {
				returnString += getMarkingString(child, "Child");
			}
			returnString += "\n  </Edge>";
		}
		returnString += "\n</Tree>";
		return returnString;
	}
	 */
	
	public void setOriginalMarking(PrologNetMarking originalMarking) {
		nodes.get(nodes.getKey(originalMarking)).setOriginalMarking(true);
	}

	public boolean hasNoEdges() { 
		return edges.isEmpty();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((edges == null) ? 0 : edges.hashCode());
		result = prime * result + nodeCount;
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Tree other = (Tree) obj;
		if (edges == null) {
			if (other.edges != null) {
				return false;
			}
		} else if (!edges.equals(other.edges)) {
			return false;
		}
		if (nodeCount != other.nodeCount) {
			return false;
		}
		if (nodes == null) {
			if (other.nodes != null) {
				return false;
			}
		} else if (!nodes.equals(other.nodes)) {
			return false;
		}
		return true;
	}

	public int getNodeCount() {
		return nodeCount;
	}

	public void setNodeCount(int nodeCount) {
		this.nodeCount = nodeCount;
	}

	public AbstractDualBidiMap<String, PrologNetMarking> getNodes() {
		return nodes;
	}

	public void setNodes(AbstractDualBidiMap<String, PrologNetMarking> nodes) {
		this.nodes = nodes;
	}

	public Set<Edge> getEdges() {
		return edges;
	}

	public void setEdges(Set<Edge> edges) {
		this.edges = edges;
	}

}
