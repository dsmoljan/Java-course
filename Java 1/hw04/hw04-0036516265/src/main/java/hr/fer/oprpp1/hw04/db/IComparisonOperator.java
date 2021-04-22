package hr.fer.oprpp1.hw04.db;

/**
 * Interface modeling a string comparison strategy
 * @author dorian
 *
 */
public interface IComparisonOperator {
	public boolean satisfied(String value1, String value2);

}
