package hr.fer.zemris.java.gui.calc.Calculator;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

public class CalcInvertableOperatorButton extends CalcButton{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DoubleUnaryOperator regularOperation;
	private DoubleUnaryOperator invertedOperation;
	String regularOperationName;
	String invertedOperationName;
	boolean regular;

	public CalcInvertableOperatorButton(String regularOperationName, String invertedOperationName, DoubleUnaryOperator regularOperation, DoubleUnaryOperator invertedOperation) {
		super(regularOperationName);
		this.regularOperationName = regularOperationName;
		this.invertedOperationName = invertedOperationName;
		this.regularOperation = regularOperation;
		this.invertedOperation = invertedOperation;
		this.regular = true;
	}
	
	public DoubleUnaryOperator getCurrentOperation() {
		if (regular == true) {
			return this.regularOperation;
		}else {
			return this.invertedOperation;
		}
	}
	
	/**
	 * Vraća naziv nove metode te mijenja operaciju
	 * @return
	 */
	public String switchOperation() {
		if (regular == true) {
			regular = false;
			return this.invertedOperationName;
		}else {
			regular = true;
			return this.regularOperationName;
		}
	}

}
