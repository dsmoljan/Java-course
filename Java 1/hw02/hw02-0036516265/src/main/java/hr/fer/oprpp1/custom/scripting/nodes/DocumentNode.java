package hr.fer.oprpp1.custom.scripting.nodes;

/**
 * Node representing an entire document.
 * @author dorian
 *
 */
public class DocumentNode extends Node{
	
	@Override
	public String toString() {
		String res = "";
		for (int i = 0; i < this.numberOfChildren(); i++) {
			Node node = this.getChild(i);
			res += node.toString(); //each node specifies its toString method
		}
		
		return res;
	}

	@Override
	//two document nodes are equal if all of their children are equal
	public boolean equals(Object obj) {
		if (!(obj instanceof DocumentNode)) {
			return false;
		}
		DocumentNode other = (DocumentNode)obj;
		if (other.numberOfChildren() != this.numberOfChildren()){
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
