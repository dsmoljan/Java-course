package hr.fer.oprpp2.hw04.servlets.glasanje;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/glasanje-glasaj")
public class GlasanjeGlasajServlet extends GlasanjeServletAbstract{

	private static final long serialVersionUID = 1L;
	
	 /**
	  *Objekt koji koristimo za sinkronizaciju pri provjeri postoji
	  *li na disku datoteka s glasovima, koji štiti stvaranje i unos tog objekta.
	 */
	private static final Object LOCK = new Object();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String bandNamesFileName = req.getServletContext().getRealPath("/WEB-INF/glasanje-definicija.txt");
		String resultsFileName = req.getServletContext().getRealPath("/WEB-INF/glasanje-rezultati.txt");
		File file = new File(resultsFileName);
		int recievedUID = Integer.parseInt(req.getParameter("id"));
		Map<Integer, Band> bands = readBandNamesFile(bandNamesFileName);
		
		
		
		synchronized (LOCK) {
			
			bands = readResultsFile(bands, resultsFileName);
			
			//nakon ovog u bands bi trebali biti bendovi sa ažuriranim bodovima
			
			//ako ne postoje, defaultna vrijednost scorea je 0 i ona će se zapisati u datoteku
			BufferedWriter br = new BufferedWriter(new FileWriter(file));
			for (Integer UID : bands.keySet()) {
				Band band = bands.get(UID);
				if (UID == recievedUID) {
					band.setScore(band.getScore() + 1);
				}
				String bandInfo = Integer.toString(UID) + "\t" + band.getScore();
				br.write(bandInfo +"\n");
			}
			br.close();
		}
		
		resp.sendRedirect(req.getContextPath() + "/glasanje-rezultati");

	}


}
