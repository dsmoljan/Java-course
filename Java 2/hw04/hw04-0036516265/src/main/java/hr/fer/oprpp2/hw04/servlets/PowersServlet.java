package hr.fer.oprpp2.hw04.servlets;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Na osnovu dobivenih podataka generira XLS dokument
 * @author Dorian
 *
 */
@WebServlet(urlPatterns = "/powers")
public class PowersServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;

	@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			String paramA = req.getParameter("a");
			String paramB = req.getParameter("b");
			String paramN = req.getParameter("n");
			
			int a = 0;
			int b = 0;
			int n = 0;
			
			try {
				a = Integer.parseInt(paramA);
				b = Integer.parseInt(paramB);
				n = Integer.parseInt(paramN);
				
			}catch(NumberFormatException e) {
				sendErrorMessage(req, resp, "Unešeni neispravni parametri! Molimo unesite ispravne parametre a, b i n");
				return;
			}
			
			if (a>100 || a <-100 || b > 100 || b < -100 || n < 1 || n > 5) {
				sendErrorMessage(req, resp, "Neki od parametara je izvan dopuštenog intervala! Molimo pošaljite parametre u ispravnom intervalu.");
				return;
			}
			
			resp.setContentType("application/vnd.ms-excel");
			resp.setHeader("Content-Disposition", "attachment; filename=\"tablica.xls\"");

			
			HSSFWorkbook workbook = new HSSFWorkbook();
			
			
			for (int i = 1; i <= n; i++) {
				HSSFSheet sheet =  workbook.createSheet(Integer.toString(i));
				//prvi stupac - integeri od a do b
				//drugi stupac - i-te potencije tih integera
				HSSFRow rowhead =   sheet.createRow((short)0);
				rowhead.createCell((short) 0).setCellValue("Broj");
				rowhead.createCell((short) 1).setCellValue("Potencija");
				
				int j = 1;
				
				int tmpA = a;
				int tmpB = b;
				
				while (tmpA <= tmpB) {
					HSSFRow row = sheet.createRow((short)j);
					row.createCell((short) 0).setCellValue(tmpA);
					row.createCell((short) 1).setCellValue(Math.pow(tmpA, i));

					j++;
					tmpA++;
				}
			}
			
			OutputStream outputStream = resp.getOutputStream();
			workbook.write(outputStream);
		}
	
	private void sendErrorMessage(HttpServletRequest req, HttpServletResponse resp, String message) throws IOException, ServletException {
		req.setAttribute("PORUKA", message);
		req.getRequestDispatcher("/WEB-INF/pages/errorMessage.jsp").forward(req, resp);
	}
}
