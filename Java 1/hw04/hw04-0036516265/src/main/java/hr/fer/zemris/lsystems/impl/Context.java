package hr.fer.zemris.lsystems.impl;

import hr.fer.oprpp1.custom.collections.ObjectStack;

/**
 * Razred koji omogućuje izvođenje postupka prikazivanja fraktala. Sadrži stog sa stanjima kornjače
 * @author dorian
 *
 */
public class Context {
	private ObjectStack<TurtleState> stack; //aktivno stanje je ono na vrhu stoga
	
	public Context() {
		this.stack = new ObjectStack<>();
	}
	
	/**
	 * Vraća stanje s vrha stoga bez uklanjanja
	 * @return
	 */
	public TurtleState getCurrentState() {
		return stack.peek();
	}
	
	/**
	 * Dodaje predano stanje na vrh stoga
	 * @param state
	 */
	public void pushState(TurtleState state) {
		stack.push(state);
	}
	
	/**
	 * Briše jedno stanje s vrha stoga
	 */
	public void popState() {
		stack.pop();
	}
}
