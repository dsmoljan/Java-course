package hr.fer.oprpp2.hw05.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hr.fer.oprpp2.hw05.dao.DAOProvider;
import hr.fer.oprpp2.hw05.model.Poll;
import hr.fer.oprpp2.hw05.model.PollOption;

/**
 * Support for displaying home page of the poll
 * @author Dorian
 *
 */
@WebServlet(urlPatterns = "/servleti/glasanje")
public class VotingServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

			Long pollId = Long.parseLong(req.getParameter("pollID"));
			Poll poll = DAOProvider.getDao().getPoolById(pollId);
			List<PollOption> pollOptions = DAOProvider.getDao().getPoolOptionsByPollId(pollId);
			
			req.setAttribute("pollId", pollId);
			req.setAttribute("pollTitle", poll.getTitle());
			req.setAttribute("pollMessage", poll.getMessage());
			req.setAttribute("pollOptions", pollOptions);
			
			req.getRequestDispatcher("/WEB-INF/pages/votingIndex.jsp").forward(req,resp);

		}
}
