package hr.fer.oprpp2.hw04.servlets.glasanje;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

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
 * Na osnovu rezultata glasanja kreira pie chart
 * @author Dorian
 *
 */
@WebServlet(urlPatterns = "/glasanje-grafika")
public class GlasanjePieChartServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<Band> bandsSortedByResult = (List<Band>) this.getServletContext().getAttribute("bandsSortedByResult");
		
		resp.setContentType("image/png");
		
		OutputStream outputStream = resp.getOutputStream();
		JFreeChart chart = getChart(bandsSortedByResult);
		int width = 500;
		int height = 350;
		ChartUtils.writeChartAsPNG(outputStream, chart, width, height);
	}
	
	public JFreeChart getChart(List<Band> bands) {
		DefaultPieDataset<String> dataset = new DefaultPieDataset<String>();
		
		double totalVotes = 0;
		
		for (int i = 0; i < bands.size(); i++) {
			totalVotes += bands.get(i).getScore();
		}
		
		for (int i = 0; i < bands.size(); i++) {
			double percentageOfVotes = (double) (bands.get(i).getScore()/totalVotes);
			dataset.setValue(bands.get(i).getName(), percentageOfVotes);
		}
		
		boolean legend = true;
		boolean tooltips = false;
		boolean urls = false;

		JFreeChart chart = ChartFactory.createPieChart("Results", dataset, legend, tooltips, urls);

		chart.setBorderPaint(Color.BLUE);
		chart.setBorderStroke(new BasicStroke(5.0f));
		chart.setBorderVisible(true);

		return chart;
	}
	

}
