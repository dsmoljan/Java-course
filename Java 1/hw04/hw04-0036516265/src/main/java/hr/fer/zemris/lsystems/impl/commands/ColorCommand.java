package hr.fer.zemris.lsystems.impl.commands;

import java.awt.Color;

import hr.fer.zemris.lsystems.Painter;
import hr.fer.zemris.lsystems.impl.Command;
import hr.fer.zemris.lsystems.impl.Context;

/**
 * Kroz <b>konstruktor</b> prima boju; u trenutno stanje kornjače zapisuje predanu boju
 * @author dorian
 *
 */
public class ColorCommand implements Command{

	private Color color;
	
	public ColorCommand(Color color) {
		this.color = color;
	}
	
	@Override
	public void execute(Context ctx, Painter painter) {
		//System.out.println("Setting color to " + color.toString());
		ctx.getCurrentState().setColor(color);
	}

}
