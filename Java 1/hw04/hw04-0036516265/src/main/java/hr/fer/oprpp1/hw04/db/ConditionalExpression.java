package hr.fer.oprpp1.hw04.db;

/**
 * Class which models a conditional expression. A conditional expression consists of a field value getter, a string literal and a comparison operator
 * @author dorian
 *
 */
public class ConditionalExpression {
	
	private IFieldValueGetter fieldGetter;
	private String string;
	private IComparisonOperator comparisonOperator;
	
	/**
	 * 
	 * @param fieldValueGetter
	 * @param string
	 * @param comparisonOperator
	 */
	public ConditionalExpression(IFieldValueGetter fieldGetter, String string, IComparisonOperator comparisonOperator) {
		this.fieldGetter = fieldGetter;
		this.string = string;
		this.comparisonOperator = comparisonOperator;
	}

	public IFieldValueGetter getFieldGetter() {
		return fieldGetter;
	}

	public String getStringLiteral() {
		return string;
	}

	public IComparisonOperator getComparisonOperator() {
		return comparisonOperator;
	}
	
	

}
