package hr.fer.oprpp1.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Vector2DTest {
	
	@Test
	public void constructorAndCopyTest() {
		
		Vector2D vector = new Vector2D(1.5,2.235);
		
		Assertions.assertEquals(1.5, vector.getX());
		Assertions.assertEquals(2.235, vector.getY());
		
		Vector2D newVector = vector.copy();
		
		Assertions.assertEquals(1.5, newVector.getX());
		Assertions.assertEquals(2.235, newVector.getY());
	}
	
	@Test
	public void addTest() {
		Vector2D vector1 = new Vector2D(1.5,2.235);
		Vector2D vector2 = new Vector2D(3.7,1.9876);
		
		Assertions.assertEquals(new Vector2D(5.2,4.2226), vector1.added(vector2));
		
		vector1.add(vector2);
		
		Assertions.assertEquals(new Vector2D(5.2,4.2226), vector1);
	}
	
	@Test
	public void rotateTest() {
		Vector2D vector1 = new Vector2D(1.5,2.5);
		
		Assertions.assertEquals(new Vector2D(-0.7071067811865472, 2.82842712474619), vector1.rotated(Math.toRadians(45)));
		vector1.rotate(Math.toRadians(45));
		Assertions.assertEquals(new Vector2D(-0.7071067811865472, 2.82842712474619), vector1);
	}
	
	@Test
	public void scaleTest() {
		Vector2D vector1 = new Vector2D(5,-7);
		
		Assertions.assertEquals(new Vector2D(25, -35), vector1.scaled(5));
		
		vector1.scale(5);
		Assertions.assertEquals(new Vector2D(25, -35), vector1);

		
		
	}

}
