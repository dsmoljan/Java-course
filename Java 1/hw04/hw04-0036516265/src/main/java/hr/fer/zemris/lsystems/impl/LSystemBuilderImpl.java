package hr.fer.zemris.lsystems.impl;

import java.awt.Color;

import hr.fer.oprpp1.custom.collections.Dictionary;
import hr.fer.oprpp1.math.Vector2D;
import hr.fer.zemris.lsystems.LSystem;
import hr.fer.zemris.lsystems.LSystemBuilder;
import hr.fer.zemris.lsystems.Painter;
import hr.fer.zemris.lsystems.impl.commands.ColorCommand;
import hr.fer.zemris.lsystems.impl.commands.DrawCommand;
import hr.fer.zemris.lsystems.impl.commands.PopCommand;
import hr.fer.zemris.lsystems.impl.commands.PushCommand;
import hr.fer.zemris.lsystems.impl.commands.RotateCommand;
import hr.fer.zemris.lsystems.impl.commands.ScaleCommand;
import hr.fer.zemris.lsystems.impl.commands.SkipCommand;

public class LSystemBuilderImpl implements LSystemBuilder{
	
	private double unitLength = 0.1;
	private double unitLengthDegreeScaler = 1;
	private Vector2D origin = new Vector2D(0, 0);
	private double angle = 0;
	private String axiom = "";
	
	private Dictionary<String,String> dictProductions;
	private Dictionary<String, Command> dictCommands;
	
	public LSystemBuilderImpl() {
		dictProductions = new Dictionary<String, String>();
		dictCommands = new Dictionary<String, Command>();
	}
	
	
	//imamo i produkcije, npr.
	// F -> F+F--F+F -> ovo je 1. rječnik, ključ je slovo, vrijednost je pravilo kako se to slovo mijenja u metodi generate
	
	//kada pozovemo metou generate, primjenjuju se proudukcije, krenuvši iz početnog aksioma
	
	//jednom kad završimo sa metodom generate, imamo gotov niz znakova koje možemo početi prevoditi u naredbe
	
	//svaki znak ima svoju naredbu
	//npr. F -> draw 1, + -> rotate 60 itd. -< ovo je 2. rječnik
	
	
	
	/**
	 * Metoda vraća primjerak razreda koji implementira sučelje Lsystem
	 */
	@Override
	public LSystem build() {
		class LSystemImpl implements LSystem{

			@Override
			public void draw(int level, Painter painter) {
				Context ctx = new Context();
				ctx.pushState(new TurtleState(origin, angle, Color.BLACK, unitLength));
				String commands = this.generate(level);
				//painter.drawLine(0, 0, 1, 1, Color.BLACK, 1);
				
				ScaleCommand scale = new ScaleCommand(unitLengthDegreeScaler);

				
				for (int i = 0; i < level; i++) {
					scale.execute(ctx, painter);
				}
				
				for (int i = 0; i < commands.toCharArray().length; i++) {
					System.out.println("Izvršavam naredbu za zanak " + commands.charAt(i));
					if (dictCommands.get(String.valueOf(commands.charAt(i))) != null) {
						dictCommands.get(String.valueOf(commands.charAt(i))).execute(ctx, painter);
					}
					//System.out.println("Executing command...");
				}				


			}

			@Override
			public String generate(int levels) {
				System.out.println("Calling method generate for level " + Integer.toString(levels));
				String res = axiom;
				String tmp = "";
				
				for (int i = 0; i < levels; i++) {
					for (char c : res.toCharArray()) {
						if (dictProductions.get(String.valueOf(c)) != null) {
							tmp += dictProductions.get(String.valueOf(c));
						} else {
							tmp += String.valueOf(c);
						}
					}
					res = tmp;
					//System.out.println("Res is currently: " + res);
					tmp = "";
				}
				
				//System.out.println("Generated string is " );
				//System.out.println(res);
				
				return res;
			}
			
		}
		
		
		return new LSystemImpl();
	}

	@Override
	public LSystemBuilder configureFromText(String[] array) {
		String tmp = "";
		String[] tmpArray;
		String[] initArray;
		for (int i = 0; i < array.length; i++) {
			tmp = array[i].trim();
			
			while (tmp.contains("  ")) {
				tmp = tmp.replace("  ", " ");
			}
			
			initArray = tmp.split(" ");
			tmpArray = new String[initArray.length];
			
			int j = 0;
			while (j < initArray.length) {
				
//				if (initArray[j].contains("/")) {
//					String[] tmpDashCheck = initArray[j].split("/");
//					if (tmpDashCheck.length == 1) {
//						tmpArray[j - 1] = Double.toString(Double.parseDouble(tmpArray[j-1]) + Double.parseDouble(tmpDashCheck[0]));
//					} else {
//						tmpArray[j - 1] = Double.toString(Double.parseDouble(tmpArray[j-1]) + Double.parseDouble(tmpArray[j + ]));
//					}
//				}
				
				if (j < (initArray.length - 2) && initArray[j+1].equals("/")) {
					tmpArray[j] =Double.toString(Double.parseDouble(initArray[j]) / Double.parseDouble(initArray[j+2]));
					j += 2;
				}else {
					tmpArray[j] = initArray[j];
				}
				
				j++;
			}
			
			switch(tmpArray[0])
			{
			case "origin":
				this.setOrigin(Double.parseDouble(tmpArray[1]), Double.parseDouble(tmpArray[2]));
				break;
			case "angle":
				this.setAngle(Double.parseDouble(tmpArray[1]));
				break;
			case "unitLength":
				this.setUnitLength(Double.parseDouble(tmpArray[1]));
				break;
			case "unitLengthDegreeScaler":
				String fullString = "";
				String prviArg = "";
				String drugiArg = "";
				boolean passedDash = false;
				
				if (tmpArray.length > 2 && tmpArray[2] == null) {
					this.setUnitLengthDegreeScaler(Double.parseDouble(tmpArray[1]));
					break;
				}
				
				for (int k = 1; k < tmpArray.length; k++) {
					if (tmpArray[k] != null) {
						tmpArray[k].replace(" ", ""); //mičem posljednje preostale praznine, pa je jedini mogući slučaj 1.0/2.0
						fullString += tmpArray[k];
					}

				}
				
				for (int l = 0; l < fullString.toCharArray().length; l++) {
					if ((fullString.charAt(l) != '/')) {
						if (passedDash == false) {
							prviArg += fullString.charAt(l);
						} else {
							drugiArg += fullString.charAt(l);
						}
					} else {
						passedDash = true;
					}
				}

				this.setUnitLengthDegreeScaler(Double.parseDouble(prviArg)/Double.parseDouble(drugiArg));
				System.out.println("Postavljam scaler na" + Double.toString(Double.parseDouble(prviArg)/Double.parseDouble(drugiArg)));
				break;
			case "command":
				if (tmpArray.length <= 3) {
					this.registerCommand(tmpArray[1].charAt(0), tmpArray[2]);
				}else {
					this.registerCommand(tmpArray[1].charAt(0), tmpArray[2] + " " + tmpArray[3]);
				}
				break;
			case "axiom":
				this.setAxiom(tmpArray[1]);
				break;
			case "production":
				this.registerProduction(tmpArray[1].charAt(0), tmpArray[2]);
				break;
			default:
				break;
			}
			
		}
		return this;
	}

	@Override
	public LSystemBuilder registerCommand(char key, String command) {
		Command newCommand = null;
		command = command.trim();
		String commandString = "";
		String commandArg = "";
		
		while (command.contains("  ")) {
			command.replace("  ", " ");
		}
		
		String[] array = command.split(" ");
		
		commandString = array[0];
		if (array.length > 1) {
			commandArg = array[1];
		}
	
		switch(commandString)
		{
			case "draw":
				newCommand = new DrawCommand(Integer.parseInt(commandArg));
				break;
			case "skip":
				newCommand = new SkipCommand(Integer.parseInt(commandArg));
				break;
			case "scale":
				newCommand = new ScaleCommand(Double.parseDouble(commandArg));
				break;
			case "rotate":
				newCommand = new RotateCommand(Double.parseDouble(commandArg));
				break;
			case "push":
				newCommand = new PushCommand();
				break;
			case "pop":
				newCommand = new PopCommand();
				break;
			case "color":
				System.out.println("Setting new color");
				Integer r = Integer.parseInt(commandArg.substring(0, 2),16);
				Integer g = Integer.parseInt(commandArg.substring(2, 4),16);
				Integer b = Integer.parseInt(commandArg.substring(4, 6),16);
				
				newCommand = new ColorCommand(new Color(r,g,b));
				break;
		}
		
		String commandKey = String.valueOf(key);
		this.dictCommands.put(commandKey, newCommand);
		return this;
	}

	@Override
	public LSystemBuilder registerProduction(char key, String production) {
		String productionKey = String.valueOf(key);
		this.dictProductions.put(productionKey, production.trim());
		return this;
	}

	@Override
	public LSystemBuilder setAngle(double angle) {
		this.angle = angle;
		return this;
	}

	@Override
	public LSystemBuilder setAxiom(String axiom) {
		this.axiom = axiom;
		return this;
	}

	@Override
	public LSystemBuilder setOrigin(double x, double y) {
		this.origin = new Vector2D(x,y);
		return this;
	}

	@Override
	public LSystemBuilder setUnitLength(double length) {
		this.unitLength = length;
		return this;
	}

	@Override
	public LSystemBuilder setUnitLengthDegreeScaler(double scale) {
		this.unitLengthDegreeScaler = scale;
		return this;
	}

}
