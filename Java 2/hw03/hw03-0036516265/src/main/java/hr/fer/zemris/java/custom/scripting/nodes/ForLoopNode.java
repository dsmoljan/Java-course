package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;

/**
 * A node representing a single for-loop expression.
 * @author dorian
 *
 */
public class ForLoopNode extends Node{
	
	private ElementVariable variable; //1st parameter in for expression
	private Element startExpression;  //2nd parameter in for expression
	private Element endExpression;	  //3rd parameter in for expression
	private Element stepExpression = null; //4th parameter in for expression, OPTIONAL, CAN BE LEFT OUT
	
	public ForLoopNode(ElementVariable variable, Element startExpression, Element endExpression, Element stepExpression){ 
		this.variable = variable;
		this.startExpression = startExpression;
		this.endExpression = endExpression;
		this.stepExpression = stepExpression;
		
	}
	
	//for može imati svoju djecu, pa treba to imati na umu
	// i također mora sama sebe zatvoriti jer ne pamtimo eksciplitno end tagove
	@Override
	public String toString() {
		String res = "{$ FOR ";
		
		res += this.variable.asText() + " " + this.startExpression.asText() + " " + this.endExpression.asText() + " " + this.stepExpression.asText();
		res += "$}";
		
		for (int i = 0; i < this.numberOfChildren(); i++) {
			Node node = this.getChild(i);
			res += node.toString(); //each node specifies its toString method
		}
		res += "{$END$}";
		return res;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ForLoopNode)) {
			return false;
		}
		
		ForLoopNode other = (ForLoopNode)obj;
		
		return (this.variable.equals(other.getVariable())  && this.startExpression.equals(other.getStartExpression()) &&
				this.endExpression.equals(other.getEndExpression()) && this.stepExpression.equals(other.getStepExpression()));
	}
	
	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visitForLoopNode(this);
	}

	public ElementVariable getVariable() {
		return variable;
	}

	public Element getStartExpression() {
		return startExpression;
	}

	public Element getEndExpression() {
		return endExpression;
	}

	public Element getStepExpression() {
		return stepExpression;
	}
	


}
