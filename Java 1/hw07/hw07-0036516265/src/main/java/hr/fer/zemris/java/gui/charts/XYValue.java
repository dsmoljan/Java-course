package hr.fer.zemris.java.gui.charts;

/**
 * Razred modelira jedan uređeni par (x,y). Read only
 * @author Dorian
 *
 */
public class XYValue {
	
	private int x;
	private int y;
	
	public XYValue(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	
	

}
