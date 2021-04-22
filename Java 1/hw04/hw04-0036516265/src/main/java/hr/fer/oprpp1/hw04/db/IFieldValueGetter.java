package hr.fer.oprpp1.hw04.db;

/**
 * Interface defining a strategy responsible for obatining a requested field value from given student record
 * @author dorian
 *
 */
public interface IFieldValueGetter {
	public String get(StudentRecord record);

}
