package hr.fer.zemris.java.gui.calc.Calculator;

public class CalcNumberButton extends CalcButton{
	
	private int number;
	
	//strategija bi se možda mogla iskoristiti da odredimo što koji gumb radi
	//recimo da za svaki number button dodamo action listenera koji upisuje tekst u jlabel itd.
	
	public CalcNumberButton(int number) {
		super(Integer.toString(number));
		this.setFont(this.getFont().deriveFont(30f));
		this.number = number;
	}
	
	

}
