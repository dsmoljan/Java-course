package hr.fer.zemris.java.gui.calc.Calculator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Pomoćni slušač koji u konstruktoru može primiti jedan argument
 * @author Dorian
 *
 */
public class NumberActionListener implements ActionListener{
	
	private int number;
	
	public NumberActionListener(int number) {
		this.number = number;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
			
	}

}
