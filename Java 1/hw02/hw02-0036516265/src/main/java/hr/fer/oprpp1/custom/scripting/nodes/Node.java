package hr.fer.oprpp1.custom.scripting.nodes;

import hr.fer.oprpp1.custom.collections.ArrayIndexedCollection;

/**
 * A bae class for all graph nodes.
 * @author dorian
 *
 */
public class Node {
	
	private ArrayIndexedCollection childNodes = null;
	
	public Node() {
	}
	
	/**
	 * Adds given child to internally managed collection of children.
	 */
	public void addChild(Node child) {
		if (childNodes == null) {
			childNodes = new ArrayIndexedCollection();
		}
		childNodes.add(child);
	}
	
	public Node getChild(int index) {
		return (Node) this.childNodes.get(index);
	}
	
	/**
	 * Returns the number of direct children.
	 */
	public int numberOfChildren() {
		if (this.childNodes == null) {
			return 0;
		} else {
			return childNodes.size();
		}
	}

}
