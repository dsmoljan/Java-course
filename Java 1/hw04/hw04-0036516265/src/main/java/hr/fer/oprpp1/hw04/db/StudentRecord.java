package hr.fer.oprpp1.hw04.db;

/**
 * Class modeling a student record. A student's record is made of JMBAG, first name, last name and final grade.
 * JMBAGs must not be identical. Final grade must be between 1 and 5
 * @author dorian
 *
 */
public class StudentRecord {
	
	private String JMBAG;
	private String lastName;
	private String firstName;
	private int finalGrade;
	
	/**
	 * 
	 * @param JMBAG
	 * @param lastName
	 * @param firstName
	 * @param finalGrade
	 */
	public StudentRecord(String JMBAG, String lastName, String firstName, int finalGrade) {
		super();
//		if (JMBAG == null) {
//			throw new IllegalArgumentException("JMBAG can't be null!");
//		}
		this.JMBAG = JMBAG;
		this.lastName = lastName;
		this.firstName = firstName;
		this.finalGrade = finalGrade;
	}

	public String getJMBAG() {
		return JMBAG;
	}

	public void setJMBAG(String jMBAG) {
		JMBAG = jMBAG;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public int getFinalGrade() {
		return finalGrade;
	}

	public void setFinalGrade(int finalGrade) {
		this.finalGrade = finalGrade;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((JMBAG == null) ? 0 : JMBAG.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof StudentRecord))
			return false;
		StudentRecord other = (StudentRecord) obj;
		if (JMBAG == null) {
			if (other.JMBAG != null)
				return false;
		} else if (!JMBAG.equals(other.JMBAG))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[JMBAG=" + JMBAG + ", lastName=" + lastName + ", firstName=" + firstName + ", finalGrade="
				+ finalGrade + "]";
	}
	
	
	
	
	
	
}
