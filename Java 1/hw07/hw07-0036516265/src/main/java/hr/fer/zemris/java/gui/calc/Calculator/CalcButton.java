package hr.fer.zemris.java.gui.calc.Calculator;

import java.awt.Color;

import javax.swing.JButton;

public class CalcButton extends JButton{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CalcButton(String name) {
		super(name);
		this.setFont(this.getFont().deriveFont(15f));
		this.setBackground(Color.LIGHT_GRAY);
	}
	

}
