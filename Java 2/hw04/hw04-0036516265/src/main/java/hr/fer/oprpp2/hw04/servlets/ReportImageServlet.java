package hr.fer.oprpp2.hw04.servlets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import hr.fer.oprpp2.hw04.global.GlobalData;
import hr.fer.oprpp2.hw04.global.Kljucevi;

/**
 * Server dinamički generira pie chart u obliku png slike.
 * @author Dorian
 *
 */
@WebServlet(urlPatterns = "/reportImage")
public class ReportImageServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setContentType("image/png");
			
			OutputStream outputStream = resp.getOutputStream();
			JFreeChart chart = getChart();
			int width = 500;
			int height = 350;
			ChartUtils.writeChartAsPNG(outputStream, chart, width, height);	
		}
	
	public JFreeChart getChart() {
		DefaultPieDataset dataset = new DefaultPieDataset();
		
		GlobalData gd = (GlobalData)getServletContext().getAttribute(Kljucevi.KEY_GLOBAL_DATA);
		
		int randomUsage1 = gd.createRandomInt(20, 40);
		int randomUsage2 = gd.createRandomInt(20, 40);
		int randomUsage3 = 100 - (randomUsage1+randomUsage2);
		
		dataset.setValue("Java", randomUsage1);
		dataset.setValue("Python", randomUsage2);
		dataset.setValue("C++", randomUsage3);

		boolean legend = true;
		boolean tooltips = false;
		boolean urls = false;

		JFreeChart chart = ChartFactory.createPieChart("OS usage", dataset, legend, tooltips, urls);

		chart.setBorderPaint(Color.BLUE);
		chart.setBorderStroke(new BasicStroke(5.0f));
		chart.setBorderVisible(true);

		return chart;
	}
}
