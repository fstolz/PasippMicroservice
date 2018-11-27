package de.fzi.pna.prolog.simulator;
import java.util.TreeMap;

public class PrologNetMarking {
	private boolean isOriginalMarking;

	private TreeMap<String, Long> placeMarkings;
	
	public PrologNetMarking() {
		placeMarkings = new TreeMap<String, Long>();
	}
	
	public PrologNetMarking addPlaceMarking(String id, long count) {
		if (count != 0) {
			Long existingValue = placeMarkings.get(id);
			if (existingValue == null) {
				placeMarkings.put(id, count);
			} else {
				placeMarkings.put(id, existingValue + count);
			}
		}
		return this;
	}
	
	public String toString() {
		String ret = "";
		for (String id : placeMarkings.keySet()) {
			if (ret != "") {
				ret += "\n";
			}
			
			long count = placeMarkings.get(id);
			ret += "marke(" + count + "," + "'" + id + "'" + ").";
		}
		// Original runnable PASIPP.exe needs a kind of EOF line
		// not needed when using Arity/Prolog32 Interpreter + PASIPP source files
		// therefore only in comments
		// ret += "\nx";
		return ret;
	}
	
	public String toXMLString() {
		String xmlString = "";
		for (String id : placeMarkings.keySet()) {
			if (!xmlString.equals("")) {
				xmlString += "\n";
			}
			long count = placeMarkings.get(id);
			String countString = count >= 0 ? "" + count : "infinity";
			xmlString += "      <Marking><Place>" + id + "</Place><Count>" + countString + "</Count></Marking>";
		}
		return xmlString;
	}
	
	public boolean isOriginalMarking() {
		return isOriginalMarking;
	}

	public void setOriginalMarking(boolean isOriginalMarking) {
		this.isOriginalMarking = isOriginalMarking;
	}

	public String toGraphML(String markingId) {
		int count = 1;
		
		String returnString = "";
		if (isOriginalMarking) {
			returnString += " <data key=\"originalMarking\">true</data>";
		}
		returnString += "\r\n            <graph id=\"" + markingId + ":\" edgedefault=\"directed\">";
		
		for (String placeId : placeMarkings.keySet()) {
			Long placeCountLong = placeMarkings.get(placeId);
			String placeCount = placeCountLong >= 0 ? "" + placeCountLong : "infinity";

			returnString += "\r\n                <node id=\"" + markingId + "::" + count + "\">";
			if (Simulator.INCLUDE_YED_LABELS) {
				returnString += "\r\n                    <data key=\"d0\">\r\n" + 
						"                        <y:ShapeNode>\r\n" + 
						"                            <y:NodeLabel>" + "[" + placeId + "," + placeCount + "]" + "</y:NodeLabel>\r\n" + 
						"                        </y:ShapeNode>\r\n" + 
						"                    </data>";
			}
			returnString += "\r\n                    <data key=\"placeId\">" + placeId + "</data> <data key=\"count\">" + placeCount + "</data>";
			returnString += "\r\n                </node>";
			count++;
		}
		returnString += "\r\n            </graph>";
		
		return returnString;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isOriginalMarking ? 1231 : 1237);
		result = prime * result + ((placeMarkings == null) ? 0 : placeMarkings.hashCode());
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
		PrologNetMarking other = (PrologNetMarking) obj;
		if (isOriginalMarking != other.isOriginalMarking) {
			return false;
		}
		if (placeMarkings == null) {
			if (other.placeMarkings != null) {
				return false;
			}
		} else if (!placeMarkings.equals(other.placeMarkings)) {
			return false;
		}
		return true;
	}
}
