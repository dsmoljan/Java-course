package hr.fer.zemris.lsystems.impl.commands;

import hr.fer.zemris.lsystems.Painter;
import hr.fer.zemris.lsystems.impl.Command;
import hr.fer.zemris.lsystems.impl.Context;

/**
 * Kroz *konstruktor* prima factor, u trenutnom stanju ažurira efektivnu
 * duljinu pomaka skaliranjem s danim faktorom.
 * @author dorian
 *
 */
public class ScaleCommand implements Command{
	
	private double factor;
	
	public ScaleCommand(double factor) {
		this.factor = factor;
	}

	@Override
	public void execute(Context ctx, Painter painter) {
		double oldShiftLength = ctx.getCurrentState().getShiftLength();
		ctx.getCurrentState().setShiftLength(factor * oldShiftLength);
		System.out.println("scaling by factor " + Double.toString(factor));
		System.out.println("Old length was " + Double.toString(oldShiftLength));
		System.out.println("New length is " + Double.toString(ctx.getCurrentState().getShiftLength()));
		
	}
	
	

}
