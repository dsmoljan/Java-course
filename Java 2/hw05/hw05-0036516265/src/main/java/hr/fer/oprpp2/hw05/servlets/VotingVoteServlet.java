package hr.fer.oprpp2.hw05.servlets;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hr.fer.oprpp2.hw05.dao.DAOProvider;
import hr.fer.oprpp2.hw05.model.Poll;
import hr.fer.oprpp2.hw05.model.PollOption;

@WebServlet(urlPatterns = "/servleti/glasanje-glasaj")
public class VotingVoteServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		long pollId = Long.parseLong(req.getParameter("pollId"));
		long optionId = Long.parseLong(req.getParameter("optionId"));
		
		boolean success = DAOProvider.getDao().voteForOption(pollId, optionId);
		if (!success) {
			req.setAttribute("message", "Glas nije uspješno zabilježen!");
			req.getRequestDispatcher("/WEB-INF/pages/errorMessage.jsp").forward(req,resp);
		}

		//ako je sve usopjelo, onda pozovi servlet za obradu rezultata glasanja
		//List<PollOption> results = DAOProvider.getDao().getPoolOptionsByPollId(pollId);
		req.setAttribute("pollId", pollId);
		System.out.println("Context path: " + req.getContextPath());
		req.getRequestDispatcher("/servleti/glasanje-rezultati").forward(req,resp);

		//resp.sendRedirect(req.getContextPath() + "/servleti/glasanje-rezultati");

	}
}
