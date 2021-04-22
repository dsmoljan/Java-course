package hr.fer.zemris.java.custom.scripting.exec;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import hr.fer.zemris.java.custom.collections.ObjectStack;
import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantDouble;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantInteger;
import hr.fer.zemris.java.custom.scripting.elems.ElementFunction;
import hr.fer.zemris.java.custom.scripting.elems.ElementOperator;
import hr.fer.zemris.java.custom.scripting.elems.ElementString;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;
import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.INodeVisitor;
import hr.fer.zemris.java.custom.scripting.nodes.Node;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Executes the document given its parsed tree
 * @author Dorian
 *
 */
public class SmartScriptEngine {
	
	private DocumentNode documentNode;
	private RequestContext requestContext;
	private ObjectMultistack multistack = new ObjectMultistack();
	
	private INodeVisitor visitor = new INodeVisitor() {

		@Override
		public void visitTextNode(TextNode node) {
			try {
				requestContext.write(node.getText());
			} catch (IOException e) {
				System.out.println("Greška prilikom slanja teksta na izlaz!");
			}	
		}

		@Override
		public void visitForLoopNode(ForLoopNode node) {
			ElementVariable var = node.getVariable();
			//na stog stavljamo varijablu i inicijaliziramo ju sa početnom vrijendnošću
			multistack.push(var.getName(), new ValueWrapper(node.getStartExpression().asText()));
			
			//sve dok je vrijednost petlje manja ili jednaka krajnoj vrijednosti petlje
			//prođi kroz svu djecu od ForLoop i pozovi accept nad svima
			while (multistack.peek(var.getName()).numCompare(node.getEndExpression().asText()) <= 0) {
				for (int i = 0; i < node.numberOfChildren(); i++) {
					Node n = node.getChild(i);
					n.accept(this);
				}
				
				//nakon što je jedna iteracija završila, povećavamo vrijednost varijable na stogu
				ValueWrapper currentValue = multistack.pop(var.getName());
				currentValue.add(node.getStepExpression().asText());
				multistack.push(var.getName(), currentValue);
			}
			
			//jednom kad zavri iteriranje, mičemo varijablu sa stoga
			multistack.pop(var.getName());
			
		}

		@Override
		public void visitEchoNode(EchoNode node) {
			
			//u ovom stogu će biti samo elementi tipa ValueWrapper
			
			Stack<ValueWrapper> tmpStack = new Stack<>();
			
			for (Element e : node.getElements()) {
				
				//ako se radi o konstanti (double, integer ili string), njenu vrijednost pushamo na stog
				if (e.getClass() == ElementConstantDouble.class) {
					tmpStack.push(new ValueWrapper(((ElementConstantDouble)e).getValue()));
					continue;
				}
				
				if (e.getClass() == ElementConstantInteger.class) {
					tmpStack.push(new ValueWrapper(((ElementConstantInteger)e).getValue()));
					continue;
				}
				
				if (e.getClass() == ElementString.class) {
					tmpStack.push(new ValueWrapper(((ElementString)e).getValue()));
					continue;
				}
				
				//ako se radi o varijabli, na glavnom stogu pronađemo najnoviju varijablu sa tim imenom
				//i njenu vrijednost dodamo na tmpStack
				if (e.getClass() == ElementVariable.class) {
					tmpStack.push(multistack.peek(((ElementVariable)e).getName()));
					continue;
				}
				
				//ako se radi o operatoru, skidamo dvije varijable sa stoga, provodimo operaciju
				//te rezultat vraćamo na stog
				//možeš probati zamijeniti ovo e.getClass sa e instance of ElementOperator
				if (e.getClass() == ElementOperator.class) {
					String operation = ((ElementOperator)e).getSymbol();
					
					ValueWrapper firstOperand = tmpStack.pop();
					ValueWrapper secondOperand = tmpStack.pop();
					
					switch(operation)
					{
						case "+":
							firstOperand.add(secondOperand.getValue());
							break;
						case "-":
							firstOperand.subtract(secondOperand.getValue());
							break;
						case "*":
							firstOperand.multiply(secondOperand.getValue());
							break;
						case "/":
							firstOperand.divide(secondOperand.getValue());
							break;
						default:
							throw new IllegalArgumentException("Unsupported operand!");
					}
					
					tmpStack.push(firstOperand);
					continue;
				}
				
				//ako se radi o funkciji, skidamo odgovarajući broj argumenata sa stoga i izvršavamo funkciju
				if (e.getClass() == ElementFunction.class) {
					String fun = ((ElementFunction)e).getName();
					
					switch(fun)
					{
						case "sin":
							Double x = Double.valueOf(tmpStack.pop().getValue().toString());
							double r = Math.sin(x);
							tmpStack.push(new ValueWrapper(r));
							break;
						case "decfmt":
							DecimalFormat format = new DecimalFormat((String) tmpStack.pop().getValue());
							Object o = tmpStack.pop().getValue();
							tmpStack.push(new ValueWrapper(decfmt(o, format)));
							break;
						case "dup":
							ValueWrapper value = tmpStack.pop();
							tmpStack.push(value);
							tmpStack.push(value);
							break;
						case "swap":
							ValueWrapper a = tmpStack.pop();
							ValueWrapper b = tmpStack.pop();
							tmpStack.push(a);
							tmpStack.push(b);
							break;
						case "setMimeType":
							String s = (String)tmpStack.pop().getValue();
							System.out.println("Postavljam mimeType na: " + s);
							requestContext.setMimeType(s);
							break;
						case "paramGet":
							ValueWrapper dv = tmpStack.pop();
							String name = (String)tmpStack.pop().getValue();
							ValueWrapper valueMappedForName = new ValueWrapper(requestContext.getParameter(name));
							tmpStack.push(valueMappedForName.getValue() == null ? dv : (valueMappedForName));
							break;
						case "pparamGet":
							dv = tmpStack.pop();
							name = (String)tmpStack.pop().getValue();
							valueMappedForName = new ValueWrapper(requestContext.getPersistentParameter(name));
							tmpStack.push(valueMappedForName.getValue() == null ? dv : valueMappedForName);
							break;
						case "pparamSet":
							name = (String)tmpStack.pop().getValue();
							valueMappedForName = tmpStack.pop();
							requestContext.setPersistentParameter(name, valueMappedForName.getValue().toString());
							break;
						case "pparamDel:":
							name = (String)tmpStack.pop().getValue();
							requestContext.removePersistentParameter(name);
							break;
						case "tparamGet":
							dv = tmpStack.pop();
							name = (String)tmpStack.pop().getValue();
							valueMappedForName = new ValueWrapper(requestContext.getTemporaryParameter(name));
							tmpStack.push(valueMappedForName.getValue() == null ? dv : (valueMappedForName));
							break;
						case "tparamSet":
							name = (String)tmpStack.pop().getValue();
							valueMappedForName = tmpStack.pop();
							requestContext.setTemporaryParameter(name, valueMappedForName.getValue().toString());
							break;
						case "tParamDel":
							name = (String)tmpStack.pop().getValue();
							requestContext.removeTemporaryParameter(name);
							break;
					}
					
				}
			}
			
			ArrayList<ValueWrapper> tmpList = new ArrayList<>();
			//ovo je samo da možemo pročitati stog od dna prema vrhu
			while (tmpStack.size() != 0) {
				tmpList.add(tmpStack.pop());

			}
			
			for (int i = tmpList.size() - 1; i >= 0; i--) {
				try {
					//pitanje je je li ovo ok; može li doći nešto što je npr. byte[]?
					//mislim da ne bi trebalo
					requestContext.write(tmpList.get(i).getValue().toString());
				} catch (IOException e1) {
					System.out.println("Greška prilikom pisanja u izlazni tok!");
				}
			}
		}

		@Override
		public void visitDocumentNode(DocumentNode node) {
			for (int i = 0; i < node.numberOfChildren(); i++) {
				Node n = node.getChild(i);
				n.accept(this);
			}
			
		}
		
	};
	
	public SmartScriptEngine(DocumentNode documentNode, RequestContext requestContext) {
		this.documentNode = documentNode;
		this.requestContext = requestContext;
	}
	
	public void execute() {
		documentNode.accept(visitor);
	}
	
	private String decfmt(Object o, DecimalFormat format){
		String result = "";
		
		if (o.getClass() == Integer.class){
			result = format.format((Integer)o);
		}else if (o.getClass() == Double.class) {
			result = format.format((Double)o);
		}else if(o.getClass() == String.class){
			result = format.format((String)o);
		}else {
			throw new IllegalArgumentException("Decimal format supports only integer, double and string!");
		}
		
		return result;
	}

}
