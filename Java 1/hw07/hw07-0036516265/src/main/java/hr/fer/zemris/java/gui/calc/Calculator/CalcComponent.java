package hr.fer.zemris.java.gui.calc.Calculator;

import java.awt.Color;
import java.awt.Component;

public class CalcComponent extends Component{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CalcComponent() {
		super();
		this.setFont(this.getFont().deriveFont(30f));
		this.setBackground(Color.BLUE);
		//this.setOpaque(true);
	}

}
