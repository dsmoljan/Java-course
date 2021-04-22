package hr.fer.oprpp1.hw04.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class QueryMain {
	
	public static String[] getDatabaseRecords() throws IOException{
		try(InputStream is = QueryMain.class.getClassLoader().getResourceAsStream("database/" + "database.txt")) {
			if(is==null) throw new RuntimeException("Datoteka extra/" + "database.txt " + " + je nedostupna.");
			byte[] data = is.readAllBytes();
			String text = new String(data, StandardCharsets.UTF_8);
			return text.split("\n");
		} catch(IOException ex) {
			throw new RuntimeException("Greška pri čitanju datoteke.", ex);
		}
	}

	public static void main(String[] args) throws IOException {		
	    Scanner scanner = new Scanner(System.in);
	    while (true) {
	    	int maxFirstNameSize = 0;
	    	int maxLastNameSize = 0;
	    	int maxJmbagSize = 0; //in case we have larger jmbages 
	    	List<StudentRecord> list = null;
	    	
	    	
	    	System.out.println("Unesite upit: ");
	    	
	    	try {
		    	list = QueryMain.getRecordsFromDB(scanner.nextLine());
	    	} catch(NullPointerException | IllegalArgumentException | IndexOutOfBoundsException e) {
	    		System.err.println("Error while parsing expression! Check your syntax!");
	    		continue;
	    	}
	    	
	    	/**
	    	 * Pronađi najveće prezime, ime, itd.
	    	 */
	    	for (StudentRecord record : list) {
	    		if (record.getFirstName().length() > maxFirstNameSize) maxFirstNameSize = record.getFirstName().length();
	    		if (record.getLastName().length() > maxLastNameSize) maxLastNameSize = record.getLastName().length();
	    		if (record.getJMBAG().length() > maxJmbagSize) maxJmbagSize = record.getJMBAG().length();
	    	}
	    	
	    	int fullTableSize = maxFirstNameSize + maxLastNameSize + maxJmbagSize + 8; // 8 because there are 8 empty spaces in the table
	    	
	    	//System.out.println("Records selected: " + list.size());
	    	int polje[] = {maxJmbagSize, maxLastNameSize, maxFirstNameSize};
	    	
	    	String result ="";
	    	String separator = "+";
	    	
	    	for (int i = 0; i < 3; i++) {
	    		for (int j = 0; j < polje[i] + 2; j++) {
	    			separator += "=";
	    		}
	    		
	    		separator += "+";
	    	}
	    	
//	    	for (int i = 0; i < maxJmbagSize + 2; i++) {
//	    		separator += "=";
//	    	}
//	    	separator += "+";
//	    	
//	    	for (int i = 0; i < maxLastNameSize + 2; i++) {
//	    		separator += "=";
//	    	}
//	    	
//	    	separator += "+";
//	    	
//	    	for (int i = 0; i < maxFirstNameSize + 2; i++) {
//	    		separator += "=";
//	    	}
//	    	
//	    	separator += "+";
//	    	
//	    	for (int i = 0; i < 3; i++) {
//	    		separator += "=";
//	    	}
	    	
	    	separator += "==="; //for finalGrade, which always has 1 character
	    	separator += "+";
	    	
	    	result += separator + "\n";

	    	for (StudentRecord record : list) {
	    		result += "| ";
	    		result += record.getJMBAG();
	    		if (maxJmbagSize - record.getJMBAG().length() != 0) {
	    			for (int k = 0; k < maxJmbagSize - record.getJMBAG().length(); k++) {
	    				result += " ";
	    			}
	    		}
	    		result += " |";
	    		
	    		result += " ";
	    		result += record.getLastName();
	    		if (maxLastNameSize - record.getLastName().length() != 0) {
	    			for (int k = 0; k < maxLastNameSize - record.getLastName().length(); k++) {
	    				result += " ";
	    			}
	    		}
	    		result += " |";
	    		
	    		result += " ";
	    		result += record.getFirstName();
	    		if (maxFirstNameSize - record.getFirstName().length() != 0) {
	    			for (int k = 0; k < maxFirstNameSize - record.getFirstName().length(); k++) {
	    				result += " ";
	    			}
	    		}
	    		result += " | ";
	    		
	    		result += Integer.toString(record.getFinalGrade());
	    		result += " |";
	    		
	    		result += "\n";
		    }
	    	
	    	result += separator;
	    	if (list.size() != 0) {
		    	System.out.println(result);
	    	}
	    	
	    	System.out.println("Records selected: " + list.size());

	    }
	}
	
	public static List<StudentRecord> getRecordsFromDB(String query) throws IOException{
		
		String[] records = QueryMain.getDatabaseRecords();
		
		StudentDatabase database = null;

		try {
			database = new StudentDatabase(records);
		} catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
		
	    QueryParser parser = new QueryParser(query);
	    QueryFilter filter = new QueryFilter(parser.getQuery());
	    
	    List<StudentRecord> list = database.filter(filter);
	    
	    return list;	    
	}

}
