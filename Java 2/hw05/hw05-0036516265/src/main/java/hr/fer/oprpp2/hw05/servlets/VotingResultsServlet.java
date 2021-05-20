package hr.fer.oprpp2.hw05.servlets;

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

import hr.fer.oprpp2.hw05.dao.DAOProvider;
import hr.fer.oprpp2.hw05.model.PollOption;

@WebServlet(urlPatterns = "/servleti/glasanje-rezultati")
public class VotingResultsServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		long pollId = Long.parseLong(req.getParameter("pollId")); 
		List<PollOption> results = DAOProvider.getDao().getPoolOptionsByPollId(pollId);
		Collections.sort(results, new PollOptionComparator().reversed());

		
		List<PollOption> firstOptions = new ArrayList<>();
		firstOptions.add(results.get(0));
		

		for (int i = 1; i < results.size(); i++) {
			if (results.get(i).getVotesCount() == firstOptions.get(0).getVotesCount()) {
				firstOptions.add(results.get(i));
			}else {
				break;
			}
		}

		this.getServletContext().setAttribute("pollResults", results);
		req.setAttribute("pollResults", results);
		req.setAttribute("firstOptions", firstOptions);
		req.getRequestDispatcher("/WEB-INF/pages/glasanjeRez.jsp").forward(req, resp);
		
	}
	
	private static class PollOptionComparator implements Comparator<PollOption> {

		@Override
		public int compare(PollOption o1, PollOption o2) {
			if (o1.getVotesCount() > o2.getVotesCount()) {
				return 1;
			}else if (o2.getVotesCount() < o2.getVotesCount()) {
				return -1;
			}else {
				return o1.getOptionTitle().compareTo(o2.getOptionTitle());
			}
		}
	}
	
	
	

}
