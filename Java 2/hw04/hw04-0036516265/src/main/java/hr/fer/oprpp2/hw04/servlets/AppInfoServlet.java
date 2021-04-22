package hr.fer.oprpp2.hw04.servlets;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hr.fer.oprpp2.hw04.global.Kljucevi;

/**
 * Daje informaciju koliko dugo je server pokrenut
 * @author Dorian
 *
 */
@WebServlet(urlPatterns = "/appinfo")
public class AppInfoServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Long currentTime = System.currentTimeMillis();
		Long timeStarted = (Long) this.getServletContext().getAttribute(Kljucevi.KEY_RUNNING_TIME);
		
		Long timeRunning = currentTime - timeStarted;
	
		
		int numberOfDays = 0;
		int numberOfHours = 0;
		int numberOfMinutes = 0;
		int numberOfSeconds = 0;
		int numberOfMiliseconds = 0;

		numberOfDays = (int) ((timeRunning/1000) / 86400);
		numberOfHours = (int) (((timeRunning/1000) % 86400 ) / 3600);
		numberOfMinutes = (int) ((((timeRunning/1000) % 86400 ) % 3600 ) / 60); 
		numberOfSeconds = (int) ((((timeRunning/1000) % 86400 ) % 3600 ) % 60);
		numberOfMiliseconds = (int) (((((timeRunning/1000) % 86400 ) % 3600 ) % 60)/60);
		
		req.setAttribute("days", Integer.toString(numberOfDays));
		req.setAttribute("hours", Integer.toString(numberOfDays));
		req.setAttribute("minutes", Integer.toString(numberOfMinutes));
		req.setAttribute("seconds", Integer.toString(numberOfSeconds));
		req.setAttribute("miliseconds", Integer.toString(numberOfMiliseconds));
		
		req.getRequestDispatcher("/WEB-INF/pages/info.jsp").forward(req,resp);

		
	}

}
