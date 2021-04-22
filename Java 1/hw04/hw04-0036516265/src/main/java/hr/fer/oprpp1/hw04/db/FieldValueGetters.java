package hr.fer.oprpp1.hw04.db;

/**
 * Contains concrete implementations of student entry value getter strategy. Each implementation takes a StudentEntry, and returns the appropriate value
 * @author dorian
 *
 */
public class FieldValueGetters {
	
	/**
	 * Takes in a student entry, returns first name value of the entry
	 */
	public static IFieldValueGetter FIRST_NAME = ((r) -> r.getFirstName());
	
	/**
	 * Takes in a student entry, returns last name value of the entry
	 */
	public static IFieldValueGetter LAST_NAME = ((r) -> r.getLastName());
	
	/**
	 * Takes in a student entry, returns jmbag value of the entry
	 */
	public static IFieldValueGetter JMBAG = ((r) -> r.getJMBAG());

}
