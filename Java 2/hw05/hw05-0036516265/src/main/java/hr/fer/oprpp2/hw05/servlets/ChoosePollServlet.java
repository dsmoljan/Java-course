package hr.fer.oprpp2.hw05.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hr.fer.oprpp2.hw05.dao.DAOProvider;
import hr.fer.oprpp2.hw05.model.Poll;

/**
 * Obtains a list of defined pools and renders them to the user as a list of clickable links
 * @author Dorian
 *
 */
@WebServlet(urlPatterns = "/servleti/index.html")
public class ChoosePollServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<Poll> pollList = DAOProvider.getDao().getAllPolls();
		req.setAttribute("pollList", pollList);
		
		req.getRequestDispatcher("/WEB-INF/pages/choosePoll.jsp").forward(req, resp);
		}
	
	
	

}
