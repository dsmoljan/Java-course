package hr.fer.oprpp1.custom.collections.demo;
import java.util.Arrays;
import java.util.Scanner;

import hr.fer.oprpp1.custom.collections.ObjectStack;


/**
 * Command line application used to evaluate expressions written in postfix notation.
 * Supported operators are +, -, /, * and %
 * @author dorian
 *
 */
public class StackDemo {
		
	public static void main(String[] args) {
		String expression = args[0];
		//System.out.println(expression);
		String[] tmpArray = expression.split(" ");
		
		ObjectStack stack = new ObjectStack();
		
		for (String s : tmpArray) {
			s = s.trim();
			if ((int)s.charAt(0) >= 48 && (int)s.charAt(0) <= 57) { //number
				stack.push(Integer.parseInt(s));
			} else {
				int num1 = (int)stack.pop();
				int num2 = (int)stack.pop();
				switch (s)
				{
					case "+":
						stack.push(num2 + num1);
						break;
					case "-":
						stack.push(num2 - num1);
						break;
					case "/":
						if (num1 == 0) {
							throw new IllegalArgumentException("Division by 0 is not possible!");
						}
						stack.push((int)Math.floor(num2/num1));
						break;
					case "*":
						stack.push(num2 * num1);
						break;
					case "%":
						if (num1 == 0) {
							throw new IllegalArgumentException("Division by 0 is not possible!");
						}
						stack.push(num2%num1);
						break;
					default:
						throw new IllegalArgumentException("Operand " + s + " is not a supported operand");
				}
						
			}
		}
		
		if (stack.size() != 1) {
			System.out.println("Error!");
		} else {
			System.out.println(Integer.toString((int)stack.pop()));
		}
		
	}
	

}
