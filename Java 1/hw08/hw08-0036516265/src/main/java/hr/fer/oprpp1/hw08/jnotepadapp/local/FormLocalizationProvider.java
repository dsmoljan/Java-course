package hr.fer.oprpp1.hw08.jnotepadapp.local;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.nio.file.Path;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class FormLocalizationProvider extends LocalizationProviderBridge{

	public FormLocalizationProvider(LocalizationProvider localizationProvider, JFrame frame) {
		super(localizationProvider);
		WindowListener wl = new WindowAdapter() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				connect();
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("Zatvaram sve slušače");
				disconnect();
			}
			
		};
		
		frame.addWindowListener(wl);
	}

}
