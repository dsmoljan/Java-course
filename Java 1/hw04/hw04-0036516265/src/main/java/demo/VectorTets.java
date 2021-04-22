package demo;

import hr.fer.oprpp1.math.Vector2D;

public class VectorTets {
	
	public static void main(String[] args) {
		Vector2D vector = new Vector2D(1,0);
		vector.rotate(Math.toRadians(210));
		
		System.out.println(vector);
	
		printAngle(vector);
	}
	
	public static void printAngle(Vector2D vector) {
		System.out.println(Math.toDegrees(Math.atan(vector.getY()/vector.getX())));
	}
			

}


