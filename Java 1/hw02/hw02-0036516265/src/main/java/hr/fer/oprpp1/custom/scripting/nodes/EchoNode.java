package hr.fer.oprpp1.custom.scripting.nodes;

import hr.fer.oprpp1.custom.scripting.elems.Element;

/**
 * Node representing a command which generates some textual output dynamically.
 * @author dorian
 *
 */
public class EchoNode extends Node{
	
	private Element[] elements;
	
	public EchoNode(Element[] elements) {
		this.elements = elements;
	}
	
	public Element[] getElements() {
		return this.elements;
	}
	
	@Override
	public String toString() {
		String res = "{$= ";
		for (Element e : this.elements) {
			res += " " + e.asText();
		}
		res += "$}";
		return res;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EchoNode)) {
			return false;
		}
		EchoNode other = (EchoNode)obj;
		
		if (other.numberOfChildren() != this.numberOfChildren()) {
			return false;
		}
		
		for (int i = 0; i < this.numberOfChildren(); i++) {
			if (!(this.getChild(i).equals(other.getChild(i)))) {
				return false;
			}
		}
		
		
		return true;
	}

}
