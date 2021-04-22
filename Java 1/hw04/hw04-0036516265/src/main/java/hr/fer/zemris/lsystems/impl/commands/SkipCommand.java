package hr.fer.zemris.lsystems.impl.commands;

import java.awt.Color;

import hr.fer.oprpp1.math.Vector2D;
import hr.fer.zemris.lsystems.Painter;
import hr.fer.zemris.lsystems.impl.Command;
import hr.fer.zemris.lsystems.impl.Context;

/**
 * Kao i DrawCommand, samo što ne povlači liniju
 * @author dorian
 *
 */
public class SkipCommand implements Command{
	
	private int step;
	
	public SkipCommand(int step) {
		this.step = step;
	}

	@Override
	public void execute(Context ctx, Painter painter) {
		Color curColor = ctx.getCurrentState().getColor();
		ctx.getCurrentState().setColor(Color.WHITE);
		
		DrawCommand draw = new DrawCommand(step);
		
		ctx.getCurrentState().setColor(curColor);
		
	}
	
	

}
