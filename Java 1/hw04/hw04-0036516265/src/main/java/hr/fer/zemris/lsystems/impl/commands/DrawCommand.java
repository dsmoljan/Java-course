package hr.fer.zemris.lsystems.impl.commands;

import hr.fer.oprpp1.math.Vector2D;
import hr.fer.zemris.lsystems.Painter;
import hr.fer.zemris.lsystems.impl.Command;
import hr.fer.zemris.lsystems.impl.Context;

/**
 * Kroz <b>konstruktor</b> prima step, računa gdje kornjača mora otići, povlači liniju zadanom bojom
 * od trenutne pozicije kornjače do izračunate i pamti u trenutnom stanju novu poziciju
 * @author dorian
 *
 */
public class DrawCommand implements Command{
	
	/**
	 * Broj jediničnih koraka koje kornjača mora prijeći; duljina za koju se mora pomaknuti
	 * Skaliramo ga sa shift length da dobijemo ukupnu dužinu
	 */
	private int step;
	
	public DrawCommand(int step) {
		this.step = step;
	}

	@Override
	public void execute(Context ctx, Painter painter) {
		double curX = ctx.getCurrentState().getPosition().getX();
		double curY = ctx.getCurrentState().getPosition().getY();
		
		double angle = calcAngle(ctx.getCurrentState().getAngle()); //-> ovdje ti je greška
																													//jer iako ti dobro rotiraš vektor, jednom kada ga rotiraš na ovaj način, problem, jer ti ovakvam način vraća samo raspone od [0,90] te [-90,0] stupnjeva
																													//pa to prepravi
		
		// shift length je ispravno postavljen
		double length = this.step * ctx.getCurrentState().getShiftLength();
		
		Vector2D newPos = new Vector2D(length,0); //prvo napravimo vektor sa traženom duljinom na x-osi
		
		//System.out.println("Angle is " + Double.toString(Math.toDegrees(angle)));
		newPos.rotate(angle);					  //zatim ga zarotiramo za potreban kut, tj. kut u kojem kornjača trenutno gleda
		
		System.out.println("Vektor rotacije je " + ctx.getCurrentState().getAngle());
		System.out.println("Smjer kornjače je " + Math.toDegrees(angle));
		
		newPos = ctx.getCurrentState().getPosition().added(newPos);  //dodajemo ga trenutnoj poziciji da dobijemo sljedeću točku
		
		System.out.println("Povlačim liniju od točke (" + Double.toString(curX) + "," + Double.toString(curY) + 
				") do točke (" + Double.toString(newPos.getX()) + "," + Double.toString(newPos.getY()) + ")");
		
		painter.drawLine(curX, curY, newPos.getX(), newPos.getY(), ctx.getCurrentState().getColor(), 1);
		//painter.drawLine(curX, curY, 0.95, 0.4, ctx.getCurrentState().getColor(), 1); -> ovo bi trebala biti prva draw naredba
		ctx.getCurrentState().setPosition(newPos);
		// TODO ctx.getCurrentState().setShiftLength(ctx.getCurrentState().getShiftLength();
	}
	
	/**
	 * Pomoćna metoda, služi za ispravno računanje kuta vektora
	 * @param vector
	 * @return
	 */
	private double calcAngle(Vector2D vector) {
		double angle = Math.abs(Math.atan(vector.getY()/vector.getX()));
		if (angle < 0) {
			System.out.println("DA DA DA");
		}
		
		if (vector.getX() < 0 && vector.getY() >= 0) { //drugi kvadrant, rezultatu treba dodati 90°, tj. pi/2
			angle = Math.PI - angle;
		} else if (vector.getX() <= 0 && vector.getY() < 0) { //treći kvadrant, rezultatu treba dodati 180°, tj. pi
			angle = Math.PI + angle;
		} else if (vector.getX() > 0 && vector.getY() < 0) { //četvrti kvadrant, rezultatu treba dodati 240°, tj. 3/2 pi
			System.out.println("Prijašnji kut je "  + Math.toDegrees(angle));
			angle = Math.PI*2 - angle;
		} //inače, prvi kvadrant, ne dodaje se ništa, rezultat je ok
		
		return angle;
		
	}

}
