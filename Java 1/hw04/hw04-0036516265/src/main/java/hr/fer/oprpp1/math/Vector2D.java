package hr.fer.oprpp1.math;


/**
 * Class modeling a 2D vector
 * @author dorian
 *
 */
public class Vector2D {
	
	private double x;
	private double y;
	
	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Returns the x value
	 * @return
	 */
	public double getX() {
		return this.x;
	}
	
	/**
	 * Returns the y value
	 * @return
	 */
	public double getY() {
		return this.y;
	}
	
	/**
	 * Adds another vector to the current one
	 * @param offset
	 */
	public void add(Vector2D offset) {
		this.x += offset.getX();
		this.y += offset.getY();
	}
	
	/**
	 * Adds the given vector to the current one, and returns the result. The original vector is unchanged
	 * @param offset
	 * @return new vector - the result of the addition of the current vector and the given one
	 */
	public Vector2D added(Vector2D offset) {
		return new Vector2D(this.x + offset.getX(), this.y + offset.getY());
	}
	
	/**
	 * Rotates the current vector by a given angle
	 * @param angle
	 */
	public void rotate(double angle) {
		double oldX = this.x;
		this.x = this.x * Math.cos(angle) - this.y * Math.sin(angle);
		this.y = oldX * Math.sin(angle) + this.y * Math.cos(angle);
	}
	
	/**
	 * Rotates the current vector by a given angle and returns the result as a new vector. The original vector remains unchanged
	 * @param angle
	 * @return new vector - the result of the rotation
	 */
	public Vector2D rotated(double angle) {
		return new Vector2D(this.x * Math.cos(angle) - this.y * Math.sin(angle),this.x * Math.sin(angle) + this.y * Math.cos(angle));    
	}
	
	/**
	 * Scales the current vector by a given scalar. i.e if the vector was (5,1) and we scale it by 5, it becomes (25, 5)
	 * @param scaler
	 */
	public void scale(double scaler) {
		this.x = this.x * scaler;
		this.y = this.y * scaler;
	}
	
	/**
	 * Scales the current vector by a given scalar and returns the result as a new vector. The original vector remains unchanegd i.e if the vector was (5,1) and we scale it by 5, it becomes (25, 5)
	 * @param scaler
	 * @return new vector - the result of the scaler
	 */
	public Vector2D scaled(double scaler) {
		return new Vector2D(this.x * scaler, this.y * scaler);
	}
	
	/**
	 * Returns a copy of the current vector
	 * @return
	 */
	public Vector2D copy() {
		return new Vector2D(this.x, this.y);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Vector2D))
			return false;
		Vector2D other = (Vector2D) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "(x=" + x + ", y=" + y + ")";
	}

}
