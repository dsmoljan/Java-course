package hr.fer.zemris.java.gui.prim;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;


public class PrimDemo extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public PrimDemo() {
		this.setTitle("Prim demo");
		
		this.setSize(500,200);
		this.initGUI();
		
	}
	
	private void initGUI() {
		
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		
		PrimListModel primList = new PrimListModel();
		
		JList<Integer> list1 = new JList<>(primList);
		JList<Integer> list2 = new JList<>(primList);
		
		JPanel panel = new JPanel(new GridLayout(1, 0));
		panel.add(new JScrollPane(list1));
		panel.add(new JScrollPane(list2));
		
		cp.add(panel, BorderLayout.CENTER);
		
		JButton nextPrimeBtn = new JButton("Next prime");
		cp.add(nextPrimeBtn, BorderLayout.SOUTH);
		
		nextPrimeBtn.addActionListener(l ->{
			primList.next();
			});
		
	}
	
	private static class PrimListModel implements ListModel<Integer>{
		
		private List<ListDataListener> observers = new ArrayList<>();
		private List<Integer> data = new ArrayList<>();
		int currentNumber = 1;

		public PrimListModel() {
			this.data.add(1);
		}
		
		@Override
		public int getSize() {
			return this.data.size();
		}

		@Override
		public Integer getElementAt(int index) {
			return this.data.get(index);
		}

		@Override
		public void addListDataListener(ListDataListener l) {
			this.observers.add(l);
			
		}

		@Override
		public void removeListDataListener(ListDataListener l) {
			this.observers.remove(l);
			
		}
		
		/**
		 * Generira sljedeći prim broj i dodaje ga u listu
		 */
		public void next() {
			boolean foundNextPrime = false;
			while (foundNextPrime != true){
				currentNumber++;
				if (isPrime(currentNumber)) {
					this.data.add(currentNumber);
					foundNextPrime = true;
				}
			}
			
			// ovdje samo kažemo evo dodano je nešto novo na zadnjem indeksu liste
			ListDataEvent e = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, data.size()-1, data.size()-1);
			for(ListDataListener l : this.observers) {
				System.out.println("Obavještavam nekoga...");
				l.intervalAdded(e); //javljamo promatračima hey nešto ti je dodano u listu, evo što
			}
		}
		
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(()->{
			new PrimDemo().setVisible(true);
		});
	}
	
	/**
	 * Pomoćna metoda za provjeru je li broj prim broj
	 * @param num
	 * @return
	 */
	public static boolean isPrime(int num) {
		if (num <= 1) {
			return false;
		}
				
		int max_div = (int) Math.floor(Math.sqrt(num));
		for (int i = 2; i < (max_div + 1); i++) {
			if (num % i == 0) {
				return false;
			}
		}
		return true;
	}


}
