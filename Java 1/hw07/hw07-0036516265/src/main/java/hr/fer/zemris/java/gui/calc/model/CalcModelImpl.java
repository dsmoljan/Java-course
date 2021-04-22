package hr.fer.zemris.java.gui.calc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleBinaryOperator;

import hr.fer.zemris.java.gui.calc.model.CalcModel;
import hr.fer.zemris.java.gui.calc.model.CalcValueListener;
import hr.fer.zemris.java.gui.calc.model.CalculatorInputException;

public class CalcModelImpl implements CalcModel{
	
	private boolean editable;
	private boolean negative;
	private String input;
	private Double value;
	private String frozenDisplay;
	private Double activeOperand;
	private DoubleBinaryOperator pendingOperation; //zasad nek bude string, al kasnije možda bolje staviti da bude enum
	private List<CalcValueListener> observers;
	
	public CalcModelImpl() {
		this.input = "0";
		this.value = null;
		this.frozenDisplay = null;
		this.editable = true;
		this.negative = false;
		this.activeOperand = null;
		this.pendingOperation = null;
		this.observers = new ArrayList<>();
	}

	//dodajemo "promatrače"
	//svaki put kad dođe do promjene vrijednosti value, ova klasa (subjekt) mora o tome obavijestiti sve promatrače
	//CalcValueListener je valjda interface koji modelira promatrače
	@Override
	public void addCalcValueListener(CalcValueListener l) {
		this.observers.add(l);
		
	}

	@Override
	public void removeCalcValueListener(CalcValueListener l) {
		this.observers.remove(l);
	}

	@Override
	public double getValue() {
		
		if (this.value == null) {
			return 0;
		}
		return this.value;
	}
	
	public void notifyAllObservers() {
		for (CalcValueListener l : this.observers) {
			l.valueChanged(this);
		}
	}

	@Override
	public void setValue(double value) {
		this.value = value;
		this.frozenDisplay = null; //dodano
		this.input = Double.toString(value);
		this.editable = false;
		this.notifyAllObservers();
	}

	@Override
	public boolean isEditable() {
		return this.editable;
	}

	@Override
	public void clear() {
		this.input = "0";
		this.editable = true;
		this.value = null;
		this.notifyAllObservers();
		
	}

	@Override
	public void clearAll() {
		this.editable = true;
		this.activeOperand = null;
		this.pendingOperation = null;
		this.frozenDisplay = null;
		this.clear();

		
	}

	@Override
	public void swapSign() throws CalculatorInputException {
		if (this.isEditable() == false) {
			throw new CalculatorInputException("Calculator currenlty isn't editable!");
		}
		
		if (this.value != null) {
			this.value = this.value*-1;
			this.notifyAllObservers();
		}

		
		if (this.negative) {
			this.negative = false;
			this.input = this.input.replace("-", "");
		}else {
			this.negative = true;
			this.input = "-" + this.input;
		}
		
		this.notifyAllObservers();
	}

	@Override
	public void insertDecimalPoint() throws CalculatorInputException {
		
		if (this.value == null) {
			throw new CalculatorInputException("Please input a number first, then insert a decimal point.");
		}
		if (this.isEditable() == false) {
			throw new CalculatorInputException("Calculator currently isn't editable!");
		}
		
		if (this.input.contains(".")) {
			throw new CalculatorInputException("Decimal point has already been inserted!");
		}
		
		this.input+= "."; //eventualno dodaj notifyAllObservers i za promjenu input?
		
	}

	@Override
	public void insertDigit(int digit) throws CalculatorInputException, IllegalArgumentException {
		if (this.isEditable()== false) {
			throw new CalculatorInputException("Calculator currently isn't editable!");
		}
		
		String tmpInput = this.input;
		Double tmpDouble = 0.0;
		
		if (digit == 0 && this.input.contains(".") == false && this.input.equals("0") == false) {
			return;
		}
		
		if (this.input.equals("0")) {
			tmpInput = Integer.toString(digit);
		}else {
			tmpInput += Integer.toString(digit);
		}
		
		try {
			tmpDouble  = Double.parseDouble(tmpInput);
		}catch(NumberFormatException | NullPointerException e) {
			throw new CalculatorInputException("Cannot parse input number!");
		}
		
		this.frozenDisplay = null;
		
		if (tmpDouble.equals(Double.POSITIVE_INFINITY) | tmpDouble.equals(Double.NEGATIVE_INFINITY) | tmpDouble.equals(Double.NaN)) {
			throw new CalculatorInputException("Error - too many digits!");
		}
		
		this.input = tmpInput;
		this.value = tmpDouble;
		this.notifyAllObservers();
	}

	@Override
	public boolean isActiveOperandSet() {
		return this.activeOperand != null;
	}

	@Override
	public double getActiveOperand() throws IllegalStateException {
		
		if (this.activeOperand == null) {
			throw new IllegalStateException("Operand is not set!");
		}
		
		return this.activeOperand;
	}

	@Override
	public void setActiveOperand(double activeOperand) {
		this.activeOperand = activeOperand;
		
	}

	@Override
	public void clearActiveOperand() {
		this.activeOperand = null;
		
	}

	@Override
	public DoubleBinaryOperator getPendingBinaryOperation() {
		return this.pendingOperation;
	}

	@Override
	public void setPendingBinaryOperation(DoubleBinaryOperator op) {
		this.pendingOperation = op;
		
	}
	
	@Override
	public void freezeValue(String value) {
		this.frozenDisplay = this.input;
		this.input = "";
		
	}

	@Override
	public boolean hasFrozenValue() {
		return this.frozenDisplay != null;
	}
	
	
	@Override
	public String toString() {
		
		if (this.frozenDisplay != null) {
			return this.frozenDisplay;
		}else {
			return this.input;
		}
		

	}


	

}
