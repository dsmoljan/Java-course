package hr.fer.oprpp2.hw04.servlets;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hr.fer.oprpp2.hw04.global.GlobalData;
import hr.fer.oprpp2.hw04.global.Kljucevi;

@WebServlet(urlPatterns = "/funny")
public class FunnyServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private static String[] colors = {"red", "blue", "green", "yellow", "black", "cyan", "orange"};
	
	public FunnyServlet() {
		
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		GlobalData gd = (GlobalData)getServletContext().getAttribute(Kljucevi.KEY_GLOBAL_DATA);
		String textColor = colors[gd.createRandomInt(0, colors.length - 1)];
		req.setAttribute("textColor", textColor);
		
		req.getRequestDispatcher("/stories/funny.jsp").forward(req, resp);
		

	}

}
