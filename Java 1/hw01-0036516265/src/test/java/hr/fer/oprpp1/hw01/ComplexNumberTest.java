package hr.fer.oprpp1.hw01;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ComplexNumberTest {
	
	@Test
	public void constructorTest(){
		ComplexNumber number = ComplexNumber.fromMagnitudeAndAngle(1.0,1.5707963267948966);
		Assertions.assertEquals(0.0, number.getReal());
		Assertions.assertEquals(1.0, number.getImaginary());
		
		number = ComplexNumber.fromMagnitudeAndAngle(5.0, 0.9272952180016122);
		Assertions.assertEquals(3.0, number.getReal());
		Assertions.assertEquals(4.0, number.getImaginary());
		
		Assertions.assertEquals("3.0 + 4.0i", number.toString());
		
		//System.out.println(number.toString());
	}
	
	
	
	//@Test
	public void parserTest() {
		System.out.println(ComplexNumber.parse("-2.71+3.15i"));
	}
	
	@Test
	public void angleAndMagnitudeTest() {
		ComplexNumber number = new ComplexNumber(3,4);
		Assertions.assertEquals(5.0, number.getMagnitude());
		Assertions.assertEquals(0.9272952180016122,number.getAngle());
		
		number = new ComplexNumber(-3,4);
		Assertions.assertEquals(5.0, number.getMagnitude());
		Assertions.assertEquals(2.214297435588181,number.getAngle());
		
		number = new ComplexNumber(-3,4);
		Assertions.assertEquals(5.0, number.getMagnitude());
		Assertions.assertEquals(2.214297435588181,number.getAngle());
		
		number = new ComplexNumber(3,-4);
		Assertions.assertEquals(5.0, number.getMagnitude());
		Assertions.assertEquals(5.355890089177974,number.getAngle());
		
		number = new ComplexNumber(-3,-4);
		Assertions.assertEquals(5.0, number.getMagnitude());
		Assertions.assertEquals(4.068887871591405,number.getAngle());
		
		number = new ComplexNumber(1, 1);
		Assertions.assertEquals(1.4142135623730951, number.getMagnitude());
		Assertions.assertEquals(0.7853981633974483,number.getAngle());
		
		number = new ComplexNumber(1,-1);
		Assertions.assertEquals(1.4142135623730951, number.getMagnitude());
		Assertions.assertEquals(5.497787143782138,number.getAngle());
		
		number = new ComplexNumber(-1,1);
		Assertions.assertEquals(1.4142135623730951, number.getMagnitude());
		Assertions.assertEquals(2.356194490192345,number.getAngle());
		
		number = new ComplexNumber(-1,-1);
		Assertions.assertEquals(1.4142135623730951, number.getMagnitude());
		Assertions.assertEquals(3.9269908169872414,number.getAngle());
		
		number = new ComplexNumber(1,0);
		Assertions.assertEquals(1.0, number.getMagnitude());
		Assertions.assertEquals(0.0,number.getAngle());
		
		number = new ComplexNumber(0,1);
		Assertions.assertEquals(1.0, number.getMagnitude());
		Assertions.assertEquals(1.5707963267948966,number.getAngle());


		//System.out.println(number.getMagnitude());
		//System.out.println(number.getAngle());
		
	}
	
	@Test
	public void testOperations() {
		
		//addition test
		
		ComplexNumber n1 = new ComplexNumber(3.4578, -4.258);
		ComplexNumber n2 = new ComplexNumber(-14.83789, 2.20818);
		ComplexNumber res = n1.add(n2);
		
		Assertions.assertEquals(new ComplexNumber(-11.38009,-2.04982), res);
		
		n1 = new ComplexNumber(1,0);
		n2 = new ComplexNumber(0,1);
		res = n1.add(n2);
		
		Assertions.assertEquals(new ComplexNumber(1.0,1.0), res);
		
		//subtraction
		n1 = new ComplexNumber(3.4578, -4.258);
		n2 = new ComplexNumber(-14.83789, 2.20818);
		res = n1.sub(n2);
		
		Assertions.assertEquals(new ComplexNumber(18.29569,-6.46618), res);
		
		n1 = new ComplexNumber(0.0, 0.0);
		n2 = new ComplexNumber(1.0,1.0);
		res = n1.sub(n2);
		
		Assertions.assertEquals(new ComplexNumber(-1.0,-1.0), res);
		
		//multiplication
		n1 = new ComplexNumber(3.4578, -4.258);
		n2 = new ComplexNumber(-14.83789, 2.20818);
		res = n1.mul(n2);
		
		Assertions.assertEquals(new ComplexNumber(-41.904025602000004,70.815180424), res);
		
		//division
		n1 = new ComplexNumber(3.4578, -4.258);
		n2 = new ComplexNumber(-14.83789, 2.20818);
		res = n1.div(n2);
		
		Assertions.assertEquals(new ComplexNumber(-0.2697704668010293,0.24682069018029537), res);

		
		n1 = new ComplexNumber(3.4578, -4.258);
		n2 = new ComplexNumber(0, 2);
		res = n1.div(n2);
		
		Assertions.assertEquals(new ComplexNumber(-2.129,-1.7289), res);
		
		//power
		n1 = new ComplexNumber(3.4578, -4.258);
		res=n1.power(3);
		Assertions.assertEquals(new ComplexNumber(-146.73281892904794,-75.53086733816016), res);
		
		
		//root
		n1 = new ComplexNumber(3.4578, -4.258);
		Assertions.assertEquals(new ComplexNumber(-2.1145870488612113, 1.0068159649169095), n1.root(2)[0]);
		Assertions.assertEquals(new ComplexNumber(2.114587048861211, -1.0068159649169097), n1.root(2)[1]);

		//System.out.println(n1.root(2)[0]);
		//System.out.println(n1.root(2)[1]);
		
		//System.out.println(res);

	}
	
	@Test
	public void providedTest() {
		ComplexNumber c1 = new ComplexNumber(2,3);
		ComplexNumber c2 = ComplexNumber.parse("2.5-3i");
		ComplexNumber c3 = c1.add(ComplexNumber.fromMagnitudeAndAngle(2, 1.57)).div(c2).power(3).root(2)[1];
		//System.out.println(c3);
		
		Assertions.assertEquals(new ComplexNumber(1.6181754193833346, -0.06878563085611523), c3);
	}

}
