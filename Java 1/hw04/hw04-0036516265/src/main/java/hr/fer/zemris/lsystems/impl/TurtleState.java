package hr.fer.zemris.lsystems.impl;

import java.awt.Color;

import hr.fer.oprpp1.math.Vector2D;

public class TurtleState {
	
	public Vector2D getPosition() {
		return position;
	}



	public void setPosition(Vector2D position) {
		this.position = position;
	}



	public Vector2D getAngle() {
		return angle;
	}



	public void setAngle(Vector2D angle) {
		this.angle = angle;
	}



	public Color getColor() {
		return color;
	}



	public void setColor(Color color) {
		this.color = color;
	}



	public double getShiftLength() {
		return shiftLength;
	}



	public void setShiftLength(double shiftLength) {
		this.shiftLength = shiftLength;
	}



	private Vector2D position; //radij vektor koji čuva poziciju kornjače
	private Vector2D angle; //jedinične duljine, čuva smjer kornjače
	private Color color;
	private double shiftLength; //duljina pomaka
	
	
	
	public TurtleState(Vector2D position, double angle, Color color, double shiftLength) {
		super();
		this.position = position;
		this.color = color;
		this.angle = new Vector2D(1,0);
		this.angle.rotate(Math.toRadians(angle));
		
		this.shiftLength = shiftLength;
	}



	public TurtleState copy() {
		return new TurtleState(this.position, Math.toDegrees(Math.atan(this.angle.getY() / this.angle.getX())), this.color, this.shiftLength);
	}
	
	
	
	

}
