package hr.fer.oprpp1.hw08.jnotepadapp.models.impl;

import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import hr.fer.oprpp1.hw08.jnotepadapp.models.SingleDocumentListener;
import hr.fer.oprpp1.hw08.jnotepadapp.models.SingleDocumentModel;

/**
 * Default implementation of SingleDocumentModel interface.
 * @author Dorian
 *
 */
public class DefaultSingleDocumentModel implements SingleDocumentModel{
	
	private JTextArea editor;
	private JScrollPane scrollPane;
	private JPanel displayPanel;
	private Path filePath;
	//private String textContent;
	boolean isModified;
	private List<SingleDocumentListener> listeners;
	
	public DefaultSingleDocumentModel(Path filePath, String textContent) {
		this.editor = new JTextArea();
		this.editor.setText(textContent);
		this.filePath = filePath;
		this.isModified = false; //tko osim konstruktora postavlja ovo na false?
		this.listeners = new ArrayList();
		
		//mislim da ovo nije dobar listener
		//barem ne registrira novi znak u editoru
		editor.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				isModified = true;
				for (SingleDocumentListener listener : listeners) {
					listener.documentModifyStatusUpdated(DefaultSingleDocumentModel.this);
				}
				
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				isModified = true;
				for (SingleDocumentListener listener : listeners) {
					listener.documentModifyStatusUpdated(DefaultSingleDocumentModel.this);
				}
				
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				isModified = true;
				for (SingleDocumentListener listener : listeners) {
					listener.documentModifyStatusUpdated(DefaultSingleDocumentModel.this);
				}
				
			}
		});
		editor.addPropertyChangeListener(l -> { //mozda dodati neki drugi listener ovdje jer ovaj prati svaku sitnicu (npr. postoji caret listener)
			this.isModified = true;
			for (SingleDocumentListener listener : listeners) {
				listener.documentModifyStatusUpdated(this);
			}
			//this.notifyAllListeners(); nepotrebno?
			
		});
	}

	public JTextArea getTextComponent() {
		return this.editor;
	}

	public Path getFilePath() {
		return this.filePath;
	}

	public void setFilePath(Path path) {
		this.filePath = path;
		for (SingleDocumentListener l : this.listeners) {
			l.documentFilePathUpdated(this); // je li ovo ispravno? kakvog ovo smisla ima?
		}
		
	}

	public boolean isModified() {
		return this.isModified;
	}

	public void setModified(boolean modified) {
		this.isModified = modified;
		
	}

	public void addSingleDocumentListener(SingleDocumentListener l) {
		this.listeners.add(l);
		
	}

	public void removeSingleDocumentListener(SingleDocumentListener l) {
		this.listeners.remove(l);
		
	}

	
//	/**
//	 * Private method used to notify all listeners
//	 * makni ovo ne treba ti
//	 */
//	private void notifyAllListeners() {
//		for (SingleDocumentListener l : this.listeners) {
//			l.notify();
//		}
//	}

}
