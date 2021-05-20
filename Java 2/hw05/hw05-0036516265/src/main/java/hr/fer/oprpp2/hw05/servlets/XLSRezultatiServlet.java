package hr.fer.oprpp2.hw05.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import hr.fer.oprpp2.hw05.model.PollOption;

/**
 * Na osnovu rezultata glasanja generira XLS dokument
 * @author Dorian
 *
 */
@WebServlet(urlPatterns = "/servleti/glasanje-xls")
public class XLSRezultatiServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//rezultate glasanja prenosimo kao parametar konteksta
		List<PollOption> pollResults = (List<PollOption>) this.getServletContext().getAttribute("pollResults");
		
		resp.setContentType("application/vnd.ms-excel");
		resp.setHeader("Content-Disposition", "attachment; filename=\"Rezultati glasanja.xls\"");
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet =  workbook.createSheet("Rezultati glasanja");
		HSSFRow rowhead =   sheet.createRow((short)0);
		rowhead.createCell((short) 0).setCellValue("Opcija");
		rowhead.createCell((short) 1).setCellValue("Broj glasova");
		
		for (int i = 0; i < pollResults.size(); i++) {
			PollOption option = pollResults.get(i);
			HSSFRow row = sheet.createRow((short)i + 1);
			row.createCell((short) 0).setCellValue(option.getOptionTitle());
			row.createCell((short) 1).setCellValue(option.getVotesCount());
		}
		
		OutputStream outputStream = resp.getOutputStream();
		workbook.write(outputStream);
	}

}
