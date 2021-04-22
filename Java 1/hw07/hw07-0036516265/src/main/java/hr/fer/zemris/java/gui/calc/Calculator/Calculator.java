package hr.fer.zemris.java.gui.calc.Calculator;

import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import hr.fer.zemris.java.gui.calc.model.CalcModel;
import hr.fer.zemris.java.gui.calc.model.CalcModelImpl;
import hr.fer.zemris.java.gui.calc.model.CalcValueListener;
import hr.fer.zemris.java.gui.layouts.CalcLayout;
import hr.fer.zemris.java.gui.layouts.RCPosition;

public class Calculator extends JFrame implements CalcValueListener{
	
	private class NumberActionListener implements ActionListener{
		
		private int number;
		private JLabel label;
		
		public NumberActionListener(int number, JLabel label) {
			this.number = number;
			this.label = label;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			//System.out.println("Inserting new number...");
			model.insertDigit(number);
			label.setText(model.toString());
		}

	}
	
	private class BinaryOperationActionListener implements ActionListener{
		private DoubleBinaryOperator operation;
		private JLabel label;
		
		public BinaryOperationActionListener(DoubleBinaryOperator operation) {
			this.operation = operation;
		}
		
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			if (model.getPendingBinaryOperation() != null) {
				double result = model.getPendingBinaryOperation().applyAsDouble(model.getActiveOperand(), model.getValue());
				model.setValue(result);
				model.clearActiveOperand();
				model.setPendingBinaryOperation(null);
			}

			
			model.setActiveOperand(model.getValue());
			model.setPendingBinaryOperation(operation);
			model.freezeValue(model.toString());
			model.clear(); 
		}
		
	}
	
	private class UnaryOperationActionListener implements ActionListener{
		private DoubleUnaryOperator operation;
		double tmp = 0;
		
		public UnaryOperationActionListener(DoubleUnaryOperator operation) {
			this.operation = operation;
		}
		
		 @Override
		 public void actionPerformed(ActionEvent e) {
			 tmp = operation.applyAsDouble(model.getValue());
			 model.setValue(tmp);
			 //model.clear();
			}
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CalcModel model;
	private JLabel input;
	private Stack<Double> stack;
	
	public Calculator() {
		super();
		this.model = new CalcModelImpl();
		stack = new Stack<>();
		model.addCalcValueListener(this); //sad svaki put kad se desi neka promjena u value calculator modela, calculator model poziva metodu valuechanged calculatora
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Java Calculator v1.0");
		setLocation(20, 20);
		//setSize(500, 500);
		initGUI();
	}

	private void initGUI() {
		Container cp = getContentPane();
		cp.setLayout(new CalcLayout(3));
		
		System.out.println("U initu sam, vrijednost modela je " + model.toString());
		// da jedini problem je što ga ne repainta, sve ostalo je ok
		input = new JLabel(model.toString(), SwingConstants.RIGHT);
		input.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		input.setFont(input.getFont().deriveFont(30f));
		input.setBackground(Color.YELLOW);
		input.setOpaque(true);
		cp.add(input, new RCPosition(1, 1));
		
		//dodavanje "općenitih" buttona
		
		CalcButton equalsBtn = new CalcButton("=");
		equalsBtn.addActionListener(l ->{
			double result = model.getPendingBinaryOperation().applyAsDouble(model.getActiveOperand(), model.getValue());
			model.setValue(result);
			model.clearActiveOperand();
			model.setPendingBinaryOperation(null);
		});
		cp.add(equalsBtn, new RCPosition(1, 6));
		
		CalcButton clearBtn = new CalcButton("clr");
		clearBtn.addActionListener(l -> {
			model.clear();
			input.setText(model.toString());
		});
		cp.add(clearBtn, new RCPosition(1, 7));
		
		CalcButton resetBtn = new CalcButton("rst");
		resetBtn.addActionListener(l -> {
			model.clearAll();
			input.setText(model.toString());
		});
		cp.add(resetBtn, new RCPosition(2,7));
		
		CalcButton pushBtn = new CalcButton("push");
		pushBtn.addActionListener(l ->{
			stack.push(model.getValue());
		});
		cp.add(pushBtn, new RCPosition(3, 7));
		
		CalcButton popBtn = new CalcButton("pop");
		popBtn.addActionListener(l ->{
			double tmp = model.getValue();
			try {
				tmp = stack.pop();
			}catch(EmptyStackException e) {
				System.out.println("Cannot pop, stack is empty");
			}
			//model.set
			model.setValue(tmp);
			
			
		});
		cp.add(popBtn, new RCPosition(4, 7));
		
		//dodavanje number buttona
		int number = 9;
		for (int j = 2; j <= 5; j++) {
			for (int i = 5; i >= 3; i--) {
				if (j == 5 && (i == 5 || i == 4)) { //dodavanje 0
					continue;
				}
				CalcNumberButton numberBtn = new CalcNumberButton(number);
				//numberBtn.addActionListener(new NumberActionListener(number, input));
				numberBtn.addActionListener(l ->{
					model.insertDigit(Integer.parseInt(numberBtn.getText()));
					input.setText(model.toString());
				});
				cp.add(numberBtn, new RCPosition(j, i));
				
				//https://stackoverflow.com/questions/11037622/pass-variables-to-actionlistener-in-java
				//mislim da je ovo tehnički strategija
				
				number--;
			}
			

		}
	
		//jer DoubleBinaryOperator je ustvari functional interface
		//i mi ovdje radimo novi primjerak te klase i kažemo ok, za operaciju stavi a/b
		//operacija dijeljenja
		CalcButton divideBtn = new CalcButton("/");
		divideBtn.addActionListener(new BinaryOperationActionListener((a,b) -> a/b));
		cp.add(divideBtn, new RCPosition(2,6));
		
		//operacija množenja
		CalcButton multiplyBtn = new CalcButton("*");
		multiplyBtn.addActionListener(new BinaryOperationActionListener((a,b) -> a*b));
		cp.add(multiplyBtn, new RCPosition(3, 6));
		
		//operacija oduzimanj
		CalcButton subtractBtn = new CalcButton("-");
		subtractBtn.addActionListener(new BinaryOperationActionListener((a,b) -> a - b));
		cp.add(subtractBtn, new RCPosition(4, 6));
		
		//operacija zbrajanja
		CalcButton addBtn = new CalcButton("+");
		addBtn.addActionListener(new BinaryOperationActionListener((a,b) -> a + b));
		cp.add(addBtn, new RCPosition(5,6));
		
		//dodavanje točke
		CalcButton decPointBtn = new CalcButton(".");
		decPointBtn.addActionListener(l ->{
			model.insertDecimalPoint();
		});
		cp.add(decPointBtn, new RCPosition(5, 5));
		
		//promjena predznaka
		CalcButton signBtn = new CalcButton("+/-");
		signBtn.addActionListener(l ->{
			model.swapSign();
		});
		cp.add(signBtn, new RCPosition(5, 4));
		
		//kada ćeš dodavati listenere za invertabilne operacije, moraš provjetiti je li označen checkbox za invertiranje i na osnovu
		//toga dodati odgovarajuće listenere
		//i da, najbolje je svaki taj gumb kojem moraš promijeniti tekst dodati u zasebnu listu
		//i onda samo dodaš action listener na checkbox u kojem kažeš kada se klikne gumb, prođi kroz listu i izmijeni im stanje
		
		List<CalcInvertableOperatorButton> buttonList = new ArrayList<>();
		
		// 1/x
		CalcButton recipricalValueBtn = new CalcButton("1/x");
		recipricalValueBtn.addActionListener(new UnaryOperationActionListener((a) -> 1/a));
		cp.add(recipricalValueBtn, new RCPosition(2, 1));
		
		//sinus
		CalcInvertableOperatorButton sinusBtn = new CalcInvertableOperatorButton("sin", "arcsin",(a) -> Math.sin(a), (a) -> Math.asin(a));
		sinusBtn.addActionListener(new UnaryOperationActionListener(sinusBtn.getCurrentOperation()));
		buttonList.add(sinusBtn);
		cp.add(sinusBtn, new RCPosition(2, 2));
		
		//logaritam
		CalcInvertableOperatorButton logBtn = new CalcInvertableOperatorButton("log", "10^x", (a) -> Math.log10(a), (a) -> Math.pow(10,a));
		logBtn.addActionListener(new UnaryOperationActionListener(logBtn.getCurrentOperation()));
		buttonList.add(logBtn);
		cp.add(logBtn, new RCPosition(3, 1));
		
		//kosinus
		CalcInvertableOperatorButton cosinusBtn = new CalcInvertableOperatorButton("cos", "arccos", (a) -> Math.cos(a), (a) -> Math.acos(a));
		cosinusBtn.addActionListener(new UnaryOperationActionListener(cosinusBtn.getCurrentOperation()));
		buttonList.add(cosinusBtn);
		cp.add(cosinusBtn, new RCPosition(3,2));
		
		//ln
		CalcInvertableOperatorButton lnBtn = new CalcInvertableOperatorButton("ln", "e^x", (a) -> Math.log(a), (a) -> Math.pow(Math.E, a));
		lnBtn.addActionListener(new UnaryOperationActionListener(lnBtn.getCurrentOperation()));
		buttonList.add(lnBtn);
		cp.add(lnBtn, new RCPosition(4, 1));
		
		//tan
		CalcInvertableOperatorButton tanBtn = new CalcInvertableOperatorButton("tan", "arctan", (a) -> Math.tan(a), (a) -> Math.atan(a));
		tanBtn.addActionListener(new UnaryOperationActionListener(tanBtn.getCurrentOperation()));
		buttonList.add(tanBtn);
		cp.add(tanBtn, new RCPosition(4, 2));
		
		//x^n -> nije invertable btn jer nije unarna operacija, a raditi novu klasu samo zbog jedne operacije mi nije imalo smisla
		CalcButton xToNBtn = new CalcButton("x^n");
		xToNBtn.addActionListener(new BinaryOperationActionListener((x,n) -> Math.pow(x, n)));
		cp.add(xToNBtn, new RCPosition(5, 1));
		
		//arctan
		CalcInvertableOperatorButton ctgBtn = new CalcInvertableOperatorButton("tg", "arctg", (a) -> 1.0/Math.tan(a), (a) -> Math.PI/2 - Math.atan(a));
		ctgBtn.addActionListener(new UnaryOperationActionListener(ctgBtn.getCurrentOperation()));
		buttonList.add(ctgBtn);
		cp.add(ctgBtn, new RCPosition(5, 2));
		
		
		//okej mogao bi ukloniti sve action listenere
		//i onda dodati nove!
		
		
		
		
		//dodavanje checkboxa za invertiranje operacija
		JCheckBox invertedCheckbox = new JCheckBox("Inverted");
		invertedCheckbox.addActionListener(l ->{
			for (CalcInvertableOperatorButton btn : buttonList) {
				for (ActionListener listener : btn.getActionListeners()) {
					btn.removeActionListener(listener);
				}
				String newOperationName = btn.switchOperation();
				btn.setText(newOperationName);
				btn.addActionListener(new UnaryOperationActionListener(btn.getCurrentOperation()));
			}
			
			//malo varanja :)
			for (ActionListener listener : xToNBtn.getActionListeners()) {
				xToNBtn.removeActionListener(listener);
			}
			if (xToNBtn.getText().equals("x^n")) {
				xToNBtn.addActionListener(new BinaryOperationActionListener((x,n) -> Math.pow(x, (1.0/n))));
				xToNBtn.setText("x^(1/n)");
			}else {
				xToNBtn.addActionListener(new BinaryOperationActionListener((x,n) -> Math.pow(x, n)));
				xToNBtn.setText("x^n");
			}
			this.pack(); //da prilagodi veličinu prozora
		});
		cp.add(invertedCheckbox, new RCPosition(5, 7));
		
		
		
		//CalcInvertableOperatorButton 
		
		
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Calculator calculator = new Calculator();
				calculator.pack();
				calculator.setVisible(true);
			}
		});
	}
	
	@Override
	public void valueChanged(CalcModel model) {
		//this.removeAll();
		//this.initGUI(); //svaki put kad se promijeni nešto u calculator modelu, obavijesti GUI da se mora ponovno nacrtati (jer je recimo došlo do unosa novog broja)
		//this.repaint();
		input.setText(model.toString());
		System.out.println("ponovo sam pozvao init GUI, vrijednost u modelu je " + this.model.toString());
	};
	
	
}
