package hr.fer.zemris.java.gui.charts;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.List;

/**
 * Graička komponenta koja crta BarChart
 * 
 * @author Dorian
 *
 */
public class BarChartComponent extends Component {
	
	public static int OFFSET_AXIS = 30;
	public static int OFFSET_DESCRIPTION = 15;

	private BarChart chart;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// dakle ovo je sve jedna komponenta koja na sebi nema drugih komponenata

	public BarChartComponent(BarChart chart) {
		this.chart = chart;
	}

	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setFont(new Font("Arial", Font.BOLD, 16));
		g2d.setColor(Color.BLUE);
		//g2d.drawString("Hello World", 100, 100);
		System.out.println(this.getSize());
		Point windowOrigin = new Point(0, (int)this.getSize().getHeight());
		
		AffineTransform defaultForm = g2d.getTransform();


		g2d.setColor(Color.black);
//		g2d.drawRect(50, 50, 200, 200); // s ovim ćeš crtati stupce, to je najlakši dio
		//drawArrow(g2d,(int)windowOrigin.getX() + 5,(int)windowOrigin.getY() + 5,5,5);
		
		//crtamo nazive osi
		AffineTransform at = new AffineTransform();
		at.rotate(-Math.PI / 2); 
		 
		g2d.setTransform(at); //ne zaboravi da ovo "okreće" koordinati sustav, pa koordinate moraš pisati u skladu s tim!
		
		//g2d.drawString("Hello World", -200, 50);
		System.out.println(chart.getyAxisDescription());
		System.out.println((int)windowOrigin.getX() + 5);
		System.out.println((int)windowOrigin.getY() + 5);
//		g2d.drawString(chart.getyAxisDescription(), (int)windowOrigin.getX() - 50, (int)windowOrigin.getY() - 50);
		
		//postavljanje opisa y osi
		int yDescriptionWidth = g2d.getFontMetrics().stringWidth(chart.getxAxisDescription());
		g2d.drawString(chart.getyAxisDescription(), -((int)windowOrigin.getY()/2), (int)windowOrigin.getX() + OFFSET_DESCRIPTION);
		
		//sad bi idealno ustvari bilo i nacrtati liniju i te stvari kad je koordinatni sustav već rotiran
		
		
		//postavljanje opisa x osi
		at = new AffineTransform();
		at.rotate(0);
		
		g2d.setTransform(at);
		int xDescriptionWidth = g2d.getFontMetrics().stringWidth(chart.getxAxisDescription()); //korisno i za računanje koliko piksela moraš ostaviti za tekst brojeva uz osi
		g2d.drawString(chart.getxAxisDescription(), this.getWidth() / 2 - xDescriptionWidth/2, (int)windowOrigin.getY() + OFFSET_DESCRIPTION);

		
		Point systemOrigin = new Point((int)windowOrigin.getX() + OFFSET_AXIS, (int)windowOrigin.getY() - OFFSET_AXIS);
		

		//dodajemo brojeve i linije na y-os
		//ako ide od 0-22 u koracima od 2, moramo ih sve prikazati
		//samo ce onda graf biti manji
		int ySpacing = (int) Math.round((systemOrigin.getY() - 2*OFFSET_AXIS)/((chart.getMaxY() - chart.getMinY())/chart.getSpacing()));
		Point currentPosition = new Point(systemOrigin);
		int wordWidth = 0;
		
		g2d.setTransform(defaultForm);
		for (int y = 0; y <= chart.getMaxY(); y += chart.getSpacing()) {
			
			if (g2d.getFontMetrics().stringWidth(Integer.toString(y)) > wordWidth) {
				wordWidth = g2d.getFontMetrics().stringWidth(Integer.toString(y));
			}
			g2d.drawString(Integer.toString(y), (int)currentPosition.getX(), (int)currentPosition.getY());
			currentPosition.setLocation(currentPosition.getX(), currentPosition.getY() - ySpacing);			
		}
		
		systemOrigin.setLocation(systemOrigin.getX() + wordWidth + OFFSET_DESCRIPTION, systemOrigin.getY());



		//crtanje y osi
		drawArrow(g2d,(int)systemOrigin.getX(),(int)systemOrigin.getY(),(int)systemOrigin.getX(),OFFSET_AXIS);
		//drawArrow(g2d,(int)windowOrigin.getX() + 5,(int)windowOrigin.getY() + 5,this.getWidth() - 5,this.getHeight() - 5);

//		at = new AffineTransform();
//		at.rotate(0);
		
		//drawArrow(g2d,(int)windowOrigin.getX() + 5,(int)windowOrigin.getY() + 5,5,5);
		//drawArrow(g2d,0,0,this.getWidth(), this.getHeight());

		g2d.setTransform(defaultForm);

		//crtanje x osi
		drawArrow(g2d,(int)systemOrigin.getX(),(int)systemOrigin.getY(),this.getWidth() - OFFSET_AXIS,(int)windowOrigin.getY() - OFFSET_AXIS);
		
		
		//okej kad pišu nazivi za x-osi, moraju biti na sredini stupaca
		//kad završiš sa svime, ako ti ostane vremena, sve ove varijable kao što je početak/kraj x i y osi itd. stavi u zasebne statičke varijable
		g2d.setTransform(defaultForm);

		currentPosition = new Point(systemOrigin);
		currentPosition.setLocation(currentPosition.getX() + 1, currentPosition.getY());
		int xSpacing = (this.getWidth() - OFFSET_AXIS - (int)systemOrigin.getX())/chart.getValuesList().size();
		List<XYValue> lista = chart.getValuesList();
		for (int x = 0; x < lista.size(); x++) {
			//g2d.drawRect((int)currentPosition.getX(), (int)currentPosition.getY(), (int)currentPosition.getX() + xSpacing, (int)currentPosition.getY() + lista.get(x).getY());
			
			g2d.setColor(new Color(255, 153,51));
			Rectangle stupac = new Rectangle();
			stupac.setBounds((int)currentPosition.getX(), (int)(Math.floor(currentPosition.getY() - 0.5*lista.get(x).getY()*ySpacing)), xSpacing, (int)Math.floor(0.5*lista.get(x).getY()*ySpacing));

//			stupac.addPoint((int)currentPosition.getX(), (int)currentPosition.getY());
//			stupac.addPoint((int)currentPosition.getX(), (int)currentPosition.getY() - lista.get(x).getY()*ySpacing);
//			stupac.addPoint((int)currentPosition.getX() + xSpacing, (int)currentPosition.getY());
//			stupac.addPoint((int)currentPosition.getX() + xSpacing, (int)currentPosition.getY() - lista.get(x).getY()*ySpacing);
			g2d.fill(stupac);
			
			g2d.setColor(Color.BLACK);
			String valueName = Integer.toString(lista.get(x).getX());
			currentPosition.setLocation(currentPosition.getX() + xSpacing/2, currentPosition.getY());
			g2d.drawString(Integer.toString(lista.get(x).getX()), (int)currentPosition.getX(), (int)currentPosition.getY() + OFFSET_DESCRIPTION);
			currentPosition.setLocation(currentPosition.getX() + xSpacing/2, currentPosition.getY());

			
		}
		//inspiracija http://javaceda.blogspot.com/2010/06/draw-cartesian-coordinate-system-in.html
		


	}

	private static void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
		g2d.drawLine(x1, y1, x2, y2);
		
	    double angle = Math.atan2(y2 - y1, x2 - x1);
	    AffineTransform tx = g2d.getTransform();
	    tx.translate(x2, y2);
	    tx.rotate(angle - Math.PI / 2d);
	    g2d.setTransform(tx);

	    Polygon arrowHead = new Polygon();
	    arrowHead.addPoint(0, 5);
	    arrowHead.addPoint(-5, -5);
	    arrowHead.addPoint(5, -5);
	    g2d.fill(arrowHead);

	}

}
