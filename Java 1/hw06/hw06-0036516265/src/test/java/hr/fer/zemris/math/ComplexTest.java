package hr.fer.zemris.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ComplexTest {

	
	
	@Test
	public void angleAndMagnitudeTest() {
		Complex number = new Complex(3,4);
		Assertions.assertEquals(5.0, number.module());
		Assertions.assertEquals(0.9272952180016122,number.getAngle());
		
		number = new Complex(-3,4);
		Assertions.assertEquals(5.0, number.module());
		Assertions.assertEquals(2.214297435588181,number.getAngle());
		
		number = new Complex(-3,4);
		Assertions.assertEquals(5.0, number.module());
		Assertions.assertEquals(2.214297435588181,number.getAngle());
		
		number = new Complex(3,-4);
		Assertions.assertEquals(5.0, number.module());
		Assertions.assertEquals(5.355890089177974,number.getAngle());
		
		number = new Complex(-3,-4);
		Assertions.assertEquals(5.0, number.module());
		Assertions.assertEquals(4.068887871591405,number.getAngle());
		
		number = new Complex(1, 1);
		Assertions.assertEquals(1.4142135623730951, number.module());
		Assertions.assertEquals(0.7853981633974483,number.getAngle());
		
		number = new Complex(1,-1);
		Assertions.assertEquals(1.4142135623730951, number.module());
		Assertions.assertEquals(5.497787143782138,number.getAngle());
		
		number = new Complex(-1,1);
		Assertions.assertEquals(1.4142135623730951, number.module());
		Assertions.assertEquals(2.356194490192345,number.getAngle());
		
		number = new Complex(-1,-1);
		Assertions.assertEquals(1.4142135623730951, number.module());
		Assertions.assertEquals(3.9269908169872414,number.getAngle());
		
		number = new Complex(1,0);
		Assertions.assertEquals(1.0, number.module());
		Assertions.assertEquals(0.0,number.getAngle());
		
		number = new Complex(0,1);
		Assertions.assertEquals(1.0, number.module());
		Assertions.assertEquals(1.5707963267948966,number.getAngle());


		//System.out.println(number.module());
		//System.out.println(number.getAngle());
		
	}
	
	@Test
	public void testOperations() {
		
		//addition test
		
		Complex n1 = new Complex(3.4578, -4.258);
		Complex n2 = new Complex(-14.83789, 2.20818);
		Complex res = n1.add(n2);
		
		Assertions.assertEquals(new Complex(-11.38009,-2.04982), res);
		
		n1 = new Complex(1,0);
		n2 = new Complex(0,1);
		res = n1.add(n2);
		
		Assertions.assertEquals(new Complex(1.0,1.0), res);
		
		//subtraction
		n1 = new Complex(3.4578, -4.258);
		n2 = new Complex(-14.83789, 2.20818);
		res = n1.sub(n2);
		
		Assertions.assertEquals(new Complex(18.29569,-6.46618), res);
		
		n1 = new Complex(0.0, 0.0);
		n2 = new Complex(1.0,1.0);
		res = n1.sub(n2);
		
		Assertions.assertEquals(new Complex(-1.0,-1.0), res);
		
		//multiplytiplication
		n1 = new Complex(3.4578, -4.258);
		n2 = new Complex(-14.83789, 2.20818);
		res = n1.multiply(n2);
		
		Assertions.assertEquals(new Complex(-41.904025602000004,70.815180424), res);
		
		//division
		n1 = new Complex(3.4578, -4.258);
		n2 = new Complex(-14.83789, 2.20818);
		res = n1.divide(n2);
		
		Assertions.assertEquals(new Complex(-0.2697704668010293,0.24682069018029537), res);

		
		n1 = new Complex(3.4578, -4.258);
		n2 = new Complex(0, 2);
		res = n1.divide(n2);
		
		Assertions.assertEquals(new Complex(-2.129,-1.7289), res);
		
		//power
		n1 = new Complex(3.4578, -4.258);
		res=n1.power(3);
		Assertions.assertEquals(new Complex(-146.73281892904794,-75.53086733816016), res);
		
		
		//root
		n1 = new Complex(3.4578, -4.258);
		Assertions.assertEquals(new Complex(-2.1145870488612113, 1.0068159649169095), n1.root(2).get(0));
		Assertions.assertEquals(new Complex(2.114587048861211, -1.0068159649169097), n1.root(2).get(1));

		//System.out.println(n1.root(2)[0]);
		//System.out.println(n1.root(2)[1]);
		
		//System.out.println(res);

	}
	
}
