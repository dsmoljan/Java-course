package hr.fer.oprpp2.hw04.servlets;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/trigonometric")
public class TrigonometricServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	

	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String paramA = req.getParameter("a");
		String paramB = req.getParameter("b");
		
		int a = 0;
		int b = 360;
		
		try {
			
			if (paramA != null) {
				a = Integer.parseInt(paramA);
			}
			
			if (paramB != null) {
				b = Integer.parseInt(paramB);
			}
			
		}catch(NumberFormatException e) {
			sendErrorMessage(req, resp, "Pogrešan unos parametara a i b!");
			return;
		}
		
		if (a > b) {
			int tmp = a;
			a = b;
			b = tmp;
		}
		
		if (b > 720) {
			b = a + 720;
		}
		
		//dakle, dohvaćamo sve vrijednosti sinusa za cjelobrojne kuteve u stupnjevima od a do b
		//npr. da je a = 0, b = 90, ispisali bi sve sinuse, sin(1), sin(2)...sin(90)
		
		Map<String, String> values = new LinkedHashMap<String, String>();
		
		for (int i = a; i <= b; i++) {
			double sin = Math.sin(Math.toRadians(i));
			values.put(Integer.toString(i), Double.toString(sin));
		}
		
		req.setAttribute("valuesMap", values);
		req.getRequestDispatcher("/WEB-INF/pages/trigonometric.jsp").forward(req, resp);
		
		
		
		
		
		
	}
	
	private void sendErrorMessage(HttpServletRequest req, HttpServletResponse resp, String message) throws IOException, ServletException {
		req.setAttribute("PORUKA", message);
		req.getRequestDispatcher("/WEB-INF/pages/errorMessage.jsp").forward(req, resp);
	}

}
