package hr.fer.zemris.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ComplexPolynomTest {
	
	@Test
	public void testGivenExample() {
		
		System.out.println("First example:");
		ComplexRootedPolynomial crp = new ComplexRootedPolynomial(
				new Complex(2,0), Complex.ONE, Complex.ONE_NEG, Complex.IM, Complex.IM_NEG);
		
		ComplexPolynomial cp = crp.toComplexPolynom();
		System.out.println(crp);
		System.out.println(cp);
		System.out.println(cp.derive());
		
		System.out.println("Second example:");
		
		 crp = new ComplexRootedPolynomial(
				Complex.ONE,Complex.ONE, Complex.ONE_NEG, Complex.IM, Complex.IM_NEG);
		 
		cp = crp.toComplexPolynom();
		System.out.println(crp);
		System.out.println(cp);
		System.out.println(cp.derive());
			
		
		
		}
	}


