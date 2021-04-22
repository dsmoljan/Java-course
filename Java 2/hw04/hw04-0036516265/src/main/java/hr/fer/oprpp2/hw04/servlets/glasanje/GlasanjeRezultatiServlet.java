package hr.fer.oprpp2.hw04.servlets.glasanje;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/glasanje-rezultati")
public class GlasanjeRezultatiServlet extends GlasanjeServletAbstract{

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String bandNamesFileName = req.getServletContext().getRealPath("/WEB-INF/glasanje-definicija.txt");
		String resultsFileName = req.getServletContext().getRealPath("/WEB-INF/glasanje-rezultati.txt");
		Map<Integer, Band> bands = readBandNamesFile(bandNamesFileName);
		
		bands = readResultsFile(bands, resultsFileName);
		
		List<Band> bandsSortedByResult = new ArrayList<>();
		
		for (Integer UID: bands.keySet()) {
			bandsSortedByResult.add(bands.get(UID));
		}
		
		Collections.sort(bandsSortedByResult, new BandComparator().reversed());
		
		List<Band> firstBands = new ArrayList<>();
		firstBands.add(bandsSortedByResult.get(0));
		

		for (int i = 1; i < bandsSortedByResult.size(); i++) {
			if (bandsSortedByResult.get(i).getScore() == firstBands.get(0).getScore()) {
				firstBands.add(bandsSortedByResult.get(i));
			}else {
				break;
			}
		}
		

		
		
		req.setAttribute("bandResults", bandsSortedByResult);
		req.setAttribute("firstBands", firstBands);
		this.getServletContext().setAttribute("bandsSortedByResult", bandsSortedByResult);
		req.getRequestDispatcher("/WEB-INF/pages/glasanjeRez.jsp").forward(req, resp);
		
	}
	
	private static class BandComparator implements Comparator<Band> {

		@Override
		public int compare(Band b1, Band b2) {
			if (b1.getScore() > b2.getScore()) {
				return 1;
			}else if (b1.getScore() < b2.getScore()) {
				return -1;
			}else {
				return 0;
			}
		}
	}
	
	
	

}
