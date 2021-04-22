package hr.fer.oprpp1.custom.scripting.parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import hr.fer.oprpp1.custom.scripting.nodes.DocumentNode;

public class SmartScriptTester {
	
	@Disabled
	@Test
	public void testParserViaPrint() {

		SmartScriptParser parser = null;
		String text = readExample(11);

		//System.out.println(text);
		try {
			parser = new SmartScriptParser(text);
		} catch(SmartScriptParserException e) {
			System.out.println("Unable to parse document");
			//e.printStackTrace();
			System.exit(-1);
		}catch (Exception e) {
			//throw (e);
			System.err.println("If this line ever executes, you're in trouble");
			System.exit(-1);
		}

		DocumentNode document = parser.getDocumentNode();
		String originalDocumentBody = document.toString();
		System.out.println(originalDocumentBody);
		//System.out.println("Hi");
	} 
	
	
	@Test
	//test inputs which should return ok results and not throw an error
	//note: primjer10 is the example from homework text, starts with This is sample text.
	//primjer11 is example from homework text, the one with Joe "Long" Smith
	public void testCorrectInputs() {
		int[] inputs = {1,2,3,6,7,10,11};
		
		SmartScriptParser parser = null;
		SmartScriptParser parser2 = null;
		
		for (int i : inputs) {
			String text = readExample(i);
			parser = new SmartScriptParser(text);
			DocumentNode document = parser.getDocumentNode();
			
			String originalDocumentBody = document.toString();
			
			parser2 = new SmartScriptParser(originalDocumentBody);
			DocumentNode document2 = parser2.getDocumentNode();
			
			Assertions.assertEquals(document, document2);
			//System.out.println(document.equals(document2));
		}
	}
	
	@Test
	public void testIncorrectInputs() {
		int[] inputs = {4,5,8,9,12};
		
		for (int i : inputs) {
			String text = readExample(i);
			Assertions.assertThrows(SmartScriptParserException.class, () -> new SmartScriptParser(text));
			
		}

	}
	
	
	
	private String readExample(int n) {
		try(InputStream is = this.getClass().getClassLoader().getResourceAsStream("extra/primjer"+n+".txt")) {
			if(is==null) throw new RuntimeException("Datoteka extra/primjer"+n+".txt je nedostupna.");
			byte[] data = is.readAllBytes();
			String text = new String(data, StandardCharsets.UTF_8);
			return text;
		} catch(IOException ex) {
			throw new RuntimeException("Greška pri čitanju datoteke.", ex);
		}
	}

}
