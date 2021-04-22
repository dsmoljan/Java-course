package demo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Loader {
	
	String filename;
	
	public Loader(String filename) {
		this.filename = filename;
	}
	
	public String[] LoadFromFile() {
		try(InputStream is = this.getClass().getClassLoader().getResourceAsStream("extra/" + filename +".txt")) {
			if(is==null) throw new RuntimeException("Datoteka extra/"+filename+".txt je nedostupna.");
			byte[] data = is.readAllBytes();
			String text = new String(data, StandardCharsets.UTF_8);
			return text.split("\n");
		} catch(IOException ex) {
			throw new RuntimeException("Greška pri čitanju datoteke.", ex);
		}
		
	}
}
