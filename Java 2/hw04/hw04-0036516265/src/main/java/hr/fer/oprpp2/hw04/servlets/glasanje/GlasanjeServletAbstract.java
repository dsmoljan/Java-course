package hr.fer.oprpp2.hw04.servlets.glasanje;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServlet;

//ovo je tehnički oblikovni obrazac okvirna metoda :)
public abstract class GlasanjeServletAbstract extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private static final Object LOCK = new Object();
	
	protected static Map<Integer, Band> readBandNamesFile(String fileName){
		File file = new File(fileName);
		Map<Integer, Band> bands = new TreeMap<>();
		

		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while((line = br.readLine()) != null) {
				String[] values = line.split("\t");
				int UID = Integer.parseInt(values[0].trim());
				String name = values[1].trim();
				String songURL = values[2].trim();
				
				Band band = new Band(UID, name, songURL, 0);
				bands.put(UID, band);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bands;
	}
	
	/**
	 * Čita datoteku s rezultatima, stvara ju ako ne postoji, te vraća pročitane podatke u obliku
	 * mape.
	 * @return
	 */
	protected static Map<Integer, Band> readResultsFile(Map<Integer, Band> bands, String fileName){
		synchronized (LOCK) {
			File file = new File(fileName);
			System.out.println("Čitam glasove iz datoteke: " + file.getAbsolutePath());
		
			if (file.exists() == false) {
				try(BufferedWriter br = new BufferedWriter(new FileWriter(file))) {
					System.out.println("Stvaram novu datoteku results");
					for (Integer UID : bands.keySet()) {
						String bandInfo = Integer.toString(UID) + "\t" + "0";
						br.write(bandInfo +"\n");
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			

			try(BufferedReader br = new BufferedReader(new FileReader(file))) {
				String line;
				Band band;
				while((line = br.readLine()) != null) {
					String[] values = line.split("\t");
					int UID = Integer.parseInt(values[0].trim());
					int score = Integer.parseInt(values[1].trim());
					band = bands.get(UID);
					band.setScore(score);
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			return bands;
		}
		

		
	
	}
	

}
