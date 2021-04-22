package hr.fer.oprpp1.hw08.jnotepadapp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class StatusBar extends JPanel{
	

	private static final long serialVersionUID = 1L;
	
	private JLabel lenValue;
	private JLabel linesValue;
	private JLabel columnsValue;
	private JLabel selValue;
	private JPanel leftPart;
	private JPanel rightPart;
	
	
	public StatusBar() {
		this.leftPart = new JPanel(new GridLayout(1,1));
		this.rightPart = new JPanel(new GridLayout(1,2));
		this.setLayout(new GridLayout(1,2));
		
		this.lenValue = new JLabel("");
		this.linesValue = new JLabel("");
		this.columnsValue = new JLabel("");
		this.selValue = new JLabel("");
		

		JComponent sat = new Sat();
		sat.setSize(400, 10);
//		komponenta1.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
//		komponenta1.setOpaque(true);
//		komponenta1.setBackground(Color.YELLOW);
		
		//this.getRootPane().add(komponenta1);
		
		this.leftPart.add(lenValue);
		this.rightPart.add(linesValue);
		this.rightPart.add(columnsValue);
		this.rightPart.add(selValue);
		this.rightPart.add(sat);
		
		this.add(leftPart);
		this.add(rightPart);
		
		this.setBorder(BorderFactory.createLineBorder(Color.black, 1));
	}
	
	public JLabel getLenValue() {
		return lenValue;
	}



	public void setLenValue(int lenValue) {
		this.lenValue.setText("Length:" + Integer.toString(lenValue));
		this.update();
	}


	public void setLinesValue(int linesValue) {
		this.linesValue.setText("Ln:" + Integer.toString(linesValue));
		this.update();
	}

	public void setColumnsValue(int columnsValue) {
		this.columnsValue.setText("Col:" + Integer.toString(columnsValue));
		this.update();
	}

	public void setSelValue(int selValue) {
		this.selValue.setText("Sel:" + Integer.toString(selValue));
		this.update();
	}

	
	public void update() {
		this.repaint();
	}
	
	static class Sat extends JLabel {

		private static final long serialVersionUID = 1L;
		
		volatile String vrijeme;
		volatile String datum;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		
		public Sat() {
			updateTime();
			
			Thread t = new Thread(()->{
				while(true) {
					try {
						Thread.sleep(500);
					} catch(Exception ex) {}
					SwingUtilities.invokeLater(()->{
						updateTime();
					});
				}
			});
			t.setDaemon(true);
			t.start();
		}
		
		public void setNesto(int nesto) {
			repaint();
		}
		
		private void updateTime() {
			vrijeme = formatter.format(LocalDateTime.now());
			this.setText(vrijeme);
			repaint();
		}

	}

}
