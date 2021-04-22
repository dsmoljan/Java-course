package hr.fer.zemris.java.gui.charts;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class BarChartDemo extends JFrame {
	String path;

	public BarChartDemo(String path) {
		super();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("BarChartDemo");
		setLocation(20, 20);
		setSize(800, 400);
		this.path = path;
		initGUI();
	}

	public void initGUI() {
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		//BarChart model = null;
		
		 String xAxisDescription = "";
		 String yAxisDescription = "";
		 String[] dataString = {};
		 String minYString = "";
		 String maxYString = "";
		 String spacingString = "";
		
		try {
			File file = new File(path);
			
			 Scanner sc = new Scanner(file);
			 int i = 6;
			 xAxisDescription = sc.nextLine();
			 yAxisDescription = sc.nextLine();
			 dataString = sc.nextLine().split(" ");
			 minYString = sc.nextLine();
			 maxYString = sc.nextLine();
			 spacingString = sc.nextLine();
			 
			 sc.close();
			 
			 
			 
		}catch(NullPointerException | FileNotFoundException | IllegalStateException e) {
			System.out.println("Error while reading file! Check your path");
		}
		
		List<XYValue> lista = new ArrayList<>();
		int minY = 0;
		int maxY = 0;
		int spacing = 0;
		
		try {
			for (int i = 0; i < dataString.length; i ++) {
				String[] tmp = dataString[i].split(",");
				lista.add(new XYValue(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1])));
			}
			minY = Integer.parseInt(minYString);
			maxY = Integer.parseInt(maxYString);
			spacing = Integer.parseInt(spacingString);
			
		}catch(NumberFormatException e) {
			System.out.println("Error while parsing file - check if data is in correct format!");
		}
		
		BarChart model = new BarChart(lista, xAxisDescription, yAxisDescription, minY, maxY, spacing);
			

		
//		BarChart model = new BarChart(
//				Arrays.asList(
//				new XYValue(1,8), new XYValue(2,20), new XYValue(3,22),
//				new XYValue(4,10), new XYValue(5,4)
//				),
//				"Number of people in the car",
//				"Frequency",
//				0, // y-os kreće od 0
//				22, // y-os ide do 22
//				2
//		);
		
		JLabel pathLabel = new JLabel(path);
		
		
		BarChartComponent barChartComp = new BarChartComponent(model);
		cp.add(barChartComp, BorderLayout.CENTER);
		cp.add(pathLabel, BorderLayout.NORTH);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new BarChartDemo(args[0]).setVisible(true);
		});

	}

}
