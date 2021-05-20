package hr.fer.oprpp2.hw05.servlets;

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

import hr.fer.oprpp2.hw05.model.PollOption;

/**
 * Na osnovu rezultata glasanja kreira pie chart
 * @author Dorian
 *
 */
@WebServlet(urlPatterns = "/servleti/glasanje-grafika")
public class GlasanjePieChartServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<PollOption> pollResults = (List<PollOption>) this.getServletContext().getAttribute("pollResults");
		
		resp.setContentType("image/png");
		
		OutputStream outputStream = resp.getOutputStream();
		JFreeChart chart = getChart(pollResults);
		int width = 500;
		int height = 350;
		ChartUtils.writeChartAsPNG(outputStream, chart, width, height);
		outputStream.close();
	}
	
	public JFreeChart getChart(List<PollOption> pollOptions) {
		DefaultPieDataset<String> dataset = new DefaultPieDataset<String>();
		
		double totalVotes = 0;
		
		for (int i = 0; i < pollOptions.size(); i++) {
			totalVotes += pollOptions.get(i).getVotesCount();
		}
		
		for (int i = 0; i < pollOptions.size(); i++) {
			double percentageOfVotes = (double) (pollOptions.get(i).getVotesCount()/totalVotes);
			dataset.setValue(pollOptions.get(i).getOptionTitle(), percentageOfVotes);
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
