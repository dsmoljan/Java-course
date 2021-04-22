package hr.fer.oprpp2.hw04.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//da bi anotacije radile, u web.xml metadata-complete mora biti false
@WebServlet(urlPatterns = "/setcolor")
public class ColorChooserServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	public ColorChooserServlet() {
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String color = req.getParameter("color");
		
		System.out.println("Postavljam parametar color na: " + color);
		
		HttpSession session = req.getSession();
		session.setAttribute("pickedBgCol", color);
		
		//po završetku obrade, ponovno se vraćamo na početnu stranicu
		req.getRequestDispatcher("/index.jsp").forward(req,resp);
	}
}
