package hr.fer.zemris.java.gui.charts;

import java.util.List;

/**
 * Model jednog bar charta
 * @author Dorian
 *
 */
public class BarChart {
	
	private List<XYValue> valuesList;
	private String xAxisDescription;
	private String yAxisDescription;
	int minY;
	int maxY;
	int spacing;
	
	public BarChart(List<XYValue> valuesList, String xAxisDescription, String yAxisDescription, int minY, int maxY, int spacing) {
		this.valuesList = valuesList;
		this.xAxisDescription = xAxisDescription;
		this.yAxisDescription = yAxisDescription;
		this.minY = minY;
		
		if (maxY <= minY) {
			throw new IllegalArgumentException("ymax must be > ymin!");
		}
		
		this.maxY = maxY;
		if ((maxY - minY) % spacing != 0) {
			this.spacing = Math.round((maxY - minY)/spacing) + (maxY - minY) - 1;
		}else {
			this.spacing = spacing;
		}
		
		}

	public List<XYValue> getValuesList() {
		return valuesList;
	}

	public String getxAxisDescription() {
		return xAxisDescription;
	}

	public String getyAxisDescription() {
		return yAxisDescription;
	}

	public int getMinY() {
		return minY;
	}

	public int getMaxY() {
		return maxY;
	}

	public int getSpacing() {
		return spacing;
	}
	
	
}
