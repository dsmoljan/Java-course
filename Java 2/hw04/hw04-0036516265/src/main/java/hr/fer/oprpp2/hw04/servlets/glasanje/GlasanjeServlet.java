package hr.fer.oprpp2.hw04.servlets.glasanje;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Podrška za prikaz "home pagea" za glasanje
 * @author Dorian
 *
 */
@WebServlet(urlPatterns = "/glasanje")
public class GlasanjeServlet extends GlasanjeServletAbstract{

	private static final long serialVersionUID = 1L;
	
	@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			String fileName = req.getServletContext().getRealPath("/WEB-INF/glasanje-definicija.txt");
			File file = new File(fileName);
			
			Map<Integer, Band> bands = readBandNamesFile(fileName);
			
			req.setAttribute("bandMap", bands);
			
			req.getRequestDispatcher("/WEB-INF/pages/glasanjeIndex.jsp").forward(req,resp);

		}
}
