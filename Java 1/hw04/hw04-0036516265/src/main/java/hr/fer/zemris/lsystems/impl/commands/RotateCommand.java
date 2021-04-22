package hr.fer.zemris.lsystems.impl.commands;

import hr.fer.oprpp1.math.Vector2D;
import hr.fer.zemris.lsystems.Painter;
import hr.fer.zemris.lsystems.impl.Command;
import hr.fer.zemris.lsystems.impl.Context;

/**
 * Prima kut u stupnjevima kroz <b>konstruktor</b>, u trenutnom stanju modificira vektor
 * @author dorian
 *
 */
public class RotateCommand implements Command{
	
	private double angle;
	
	public RotateCommand(double angle) {
		this.angle = angle;
	}

	@Override
	public void execute(Context ctx, Painter painter) {
		ctx.getCurrentState().getAngle().rotate(Math.toRadians(angle));
		//System.out.println("Rotiram kornjaču za " + Double.toString(Math.toDegrees(angle)));
	}

}
