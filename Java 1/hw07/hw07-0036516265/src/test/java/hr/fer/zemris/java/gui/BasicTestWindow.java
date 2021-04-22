package hr.fer.zemris.java.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import hr.fer.zemris.java.gui.layouts.CalcLayout;

import java.awt.Container;

public class BasicTestWindow extends JFrame{
	
	
	
	public BasicTestWindow() {
		super();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Prozor1");
		setLocation(20, 20);
		setSize(500, 200);
		initGUI();
		
	}
	
	private void initGUI() {
		
		//znači mi stvaramo panele i nad njima pozivamo metode add, remove itd. sa odgovarajućim komponentama
		//a paneli pozivaju odgovarajuće metode svojih layour managera koje im kažu gdje točno da te komponente nacrtaju i 
		//pamete gdje se nalaze
		
		Container cp = getContentPane();
		cp.setLayout(new CalcLayout(3));
		
		cp.add(new JLabel("x"), "1,1"); 
		cp.add(new JLabel("y"), "2,3");
		cp.add(new JLabel("z"), "2,7");
		cp.add(new JLabel("w"), "4,2");
		cp.add(new JLabel("a"), "4,5");
		cp.add(new JLabel("b"), "4,7");
		
//		JPanel p = new JPanel(new CalcLayout(3));
//		p.add(new JLabel("x"), "1,1"); 
//		p.add(new JLabel("y"), "2,3");
//		p.add(new JLabel("z"), "2,7");
//		p.add(new JLabel("w"), "4,2");
//		p.add(new JLabel("a"), "4,5");
//		p.add(new JLabel("b"), "4,7");
		
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				BasicTestWindow prozor = new BasicTestWindow();
				prozor.setVisible(true);
			}
		});
	}

}
