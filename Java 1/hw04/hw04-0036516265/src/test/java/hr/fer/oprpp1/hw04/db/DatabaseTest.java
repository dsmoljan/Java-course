package hr.fer.oprpp1.hw04.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DatabaseTest {
	
	
	//for better code management, database.txt has been placed in test/resources and this snippet given in homework no.2 is used for reading the text file
	public static String[] getDatabaseRecords() throws IOException{
		try(InputStream is = DatabaseTest.class.getClassLoader().getResourceAsStream("database/" + "database.txt")) {
			if(is==null) throw new RuntimeException("Datoteka extra/" + "database.txt " + " + je nedostupna.");
			byte[] data = is.readAllBytes();
			String text = new String(data, StandardCharsets.UTF_8);
			return text.split("\n");
		} catch(IOException ex) {
			throw new RuntimeException("Greška pri čitanju datoteke.", ex);
		}
	}
	
	@Test
	public void testForJMBAG() throws IOException {
		String[] records = this.getDatabaseRecords();
//		for (int i = 0; i < records.length; i++) {
//			System.out.println(records[i]);
//		}
		
		StudentDatabase database = new StudentDatabase(records);
		//System.out.println(database.forJMBAG("0000000001"));
		List<StudentRecord> list1 = database.filter(f -> false);
		List<StudentRecord> list2 = database.filter(f -> true);
		
		Assertions.assertEquals(0, list1.size());
		Assertions.assertEquals(63, list2.size());
		
	}
	
	@Test
	public void testLikeComparisonOperator() {
		IComparisonOperator oper = ComparisonOperators.LIKE;
		
		Assertions.assertTrue(oper.satisfied("ababc", "abab*")); //true
		Assertions.assertTrue(oper.satisfied("abxab", "ab*ab")); //true
		Assertions.assertTrue(oper.satisfied("abababababab", "ab*ab")); //true
		Assertions.assertFalse(oper.satisfied("abxabc", "ab*ab")); //false
		Assertions.assertTrue(oper.satisfied("xxxabab", "*abab")); //true
		Assertions.assertFalse(oper.satisfied("xxxabab", "*ababc")); //false
		Assertions.assertFalse(oper.satisfied("xxxababc", "*abab")); //false
		Assertions.assertTrue(oper.satisfied("abab", "abab")); //true
		Assertions.assertFalse(oper.satisfied("Zagreb", "Aba*"));  // false
		Assertions.assertFalse(oper.satisfied("AAA", "AA*AA"));    // false
		Assertions.assertTrue(oper.satisfied("AAAA", "AA*AA"));   // true
	}
	
	@Test
	public void testFieldValueGetters() {
		StudentRecord record = new StudentRecord("0036516265", "Smoljan", "Dorian", 4);
		
		
		Assertions.assertEquals("0036516265", FieldValueGetters.JMBAG.get(record));
		Assertions.assertEquals("Dorian", FieldValueGetters.FIRST_NAME.get(record));
		Assertions.assertEquals("Smoljan", FieldValueGetters.LAST_NAME.get(record));	
	}
	
	@Test
	public void testConditionalExpression() {
		StudentRecord record = new StudentRecord("0036516265", "Smoljan", "Dorian", 4);
		
		ConditionalExpression expr = new ConditionalExpression(FieldValueGetters.LAST_NAME, "S*", ComparisonOperators.LIKE);
		
		boolean recordSatisfies = expr.getComparisonOperator().satisfied(
				expr.getFieldGetter().get(record),  // returns lastName from given record
				expr.getStringLiteral()             // returns "Bos*"
				);
		Assertions.assertTrue(recordSatisfies);
		
		expr = new ConditionalExpression(FieldValueGetters.LAST_NAME, "D*", ComparisonOperators.LIKE);
		
		recordSatisfies = expr.getComparisonOperator().satisfied(
				expr.getFieldGetter().get(record),  // returns lastName from given record
				expr.getStringLiteral()             // returns "Bos*"
				);
		
		Assertions.assertFalse(recordSatisfies);
	}
	
	@Test
	public void testQueryParser() {
		
		String expression = "firstName=\"Ivan\"";
		QueryParser parser = new QueryParser(expression);
		ConditionalExpression exp = parser.getQuery().get(0);
		Assertions.assertTrue((exp.getFieldGetter().equals(FieldValueGetters.FIRST_NAME)));
		Assertions.assertTrue((exp.getComparisonOperator().equals(ComparisonOperators.EQUALS)));
		Assertions.assertTrue(exp.getStringLiteral().equals("Ivan"));
		
		expression = "jmbag = \"0000000001\"";
		parser = new QueryParser(expression);
		Assertions.assertTrue(parser.getQueriedJMBAG().equals("0000000001"));
		Assertions.assertTrue(parser.isDirectQuery());
		
		expression = "firstName LIKE \"I*\" AND     lastName=\"Horvat\"";
		
		parser = new QueryParser(expression);
		
		ConditionalExpression exp1 = parser.getQuery().get(0);
		ConditionalExpression exp2 = parser.getQuery().get(1);
		
		Assertions.assertTrue(exp1.getComparisonOperator().equals(ComparisonOperators.LIKE));
		Assertions.assertTrue(exp1.getFieldGetter().equals(FieldValueGetters.FIRST_NAME));
		Assertions.assertTrue(exp1.getStringLiteral().equals("I*"));
		
		Assertions.assertTrue(exp2.getComparisonOperator().equals(ComparisonOperators.EQUALS));
		Assertions.assertTrue(exp2.getFieldGetter().equals(FieldValueGetters.LAST_NAME));
		Assertions.assertTrue(exp2.getStringLiteral().equals("Horvat"));;
		
	}
	
	//test for query filter is done through the class QueryMain

}
