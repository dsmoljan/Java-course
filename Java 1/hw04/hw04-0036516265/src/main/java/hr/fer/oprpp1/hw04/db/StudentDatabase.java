package hr.fer.oprpp1.hw04.db;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;

public class StudentDatabase {
	
	/**
	 * Internal list used to keep student records
	 */
	private ArrayList<StudentRecord> studentRecordList;
	
	/**
	 * Contains the index of the given student record in studentRecordList, uses JMBAG as a key. Used for fast retrieval from studentRecordList
	 */
	private HashMap<String, Integer> studentIndex;
	
	
	/**
	 * Gets a list of rows - database entries and creates an internal list of student records
	 * @param rows
	 */
	public StudentDatabase(String[] rows) {
		studentRecordList = new ArrayList<>();
		studentIndex = new HashMap<>();
		
		String[] tmpArray;
		for (int i = 0; i < rows.length; i++) {
//			while (rows[i].contains(" ")) {
//				rows[i].replace("  ", " ");
//			}
			
			tmpArray = rows[i].split("\t");
			
			String jmbag = tmpArray[0];
			String lastName = tmpArray[1];
			String firstName = tmpArray[2];
			int finalGrade = Integer.parseInt(tmpArray[3]);
			
			//check if jmbag is unique and if final grade is between 1 and 5
			
			if (studentIndex.containsKey(jmbag)) {
				throw new IllegalArgumentException("JMBAG " + jmbag + " is not unique!");
			}
			
			if (finalGrade < 1 && finalGrade > 5) {
				throw new IllegalArgumentException("Final grade for student with jmbag " + jmbag + " is not unique!");
			}
			
			int index = studentRecordList.size();
			
			studentRecordList.add(new StudentRecord(jmbag,lastName,firstName,finalGrade));
			studentIndex.put(jmbag, index);
		}
	}
	
	/**
	 * Method uses internal index to get requested student by jmbag in O(1). If the student doesn't exsist, returns null
	 * @param jmbag
	 * @return
	 */
	public StudentRecord forJMBAG(String jmbag) {
		if (studentIndex.containsKey(jmbag) == false) {
			return null;
		}
		return studentRecordList.get(studentIndex.get(jmbag));
		
	}
	
	/**
	 * Loops through all student records and returns those which satisfy the given filter.
	 * @param filter
	 * @return List<StudentRecord>
	 */
	public List<StudentRecord> filter(IFilter filter){
		List<StudentRecord> tmpList = new ArrayList<StudentRecord>();
		
		for (int i = 0; i < studentRecordList.size(); i++) {
			if (filter.accepts(studentRecordList.get(i))) {
				tmpList.add(studentRecordList.get(i));
			}
		}
		
		return tmpList;
	}

}
