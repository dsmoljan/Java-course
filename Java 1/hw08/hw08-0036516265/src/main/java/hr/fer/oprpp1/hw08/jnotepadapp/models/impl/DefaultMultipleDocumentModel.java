package hr.fer.oprpp1.hw08.jnotepadapp.models.impl;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import hr.fer.oprpp1.hw08.jnotepadapp.local.FormLocalizationProvider;
import hr.fer.oprpp1.hw08.jnotepadapp.local.LocalizationProvider;
import hr.fer.oprpp1.hw08.jnotepadapp.models.MultipleDocumentListener;
import hr.fer.oprpp1.hw08.jnotepadapp.models.MultipleDocumentModel;
import hr.fer.oprpp1.hw08.jnotepadapp.models.SingleDocumentListener;
import hr.fer.oprpp1.hw08.jnotepadapp.models.SingleDocumentModel;

/**
 * Default implementation of MultipleDocumentModel interface.
 * @author Dorian
 *
 */
public class DefaultMultipleDocumentModel extends JTabbedPane implements MultipleDocumentModel{

	private static final long serialVersionUID = 1L;
	
	private SingleDocumentModel currentDocument;
	private int currentDocIndex = 0;
	private int previousDocIndex = 0;
	private boolean addedFlag;
	private boolean removedFlag;
	private SingleDocumentModel previousDocument; //index prijašnjeg 
	private String clipboard;
	private ImageIcon redDiskIcon;
	private ImageIcon greenDiskIcon;

	//private 

	private List<SingleDocumentModel> allDocuments;
	List<MultipleDocumentListener> listeners;
	
	public DefaultMultipleDocumentModel(){
		this.allDocuments = new ArrayList();
		this.listeners = new ArrayList();
		this.addedFlag = false;
		this.removedFlag = false;
		this.clipboard = "";
		this.redDiskIcon = this.loadIcon("icons/redDisk.png");
		this.greenDiskIcon = this.loadIcon("icons/greenDisk.png");;
		
		this.createNewDocument();

		
		//ovdje definiramo što se događa kad promijenimo panel
		//ovo je ok za switchanje tabbova, ali prilikom dodavanja novog taba ovo se ne bi trebalo pokrenuti
		//tj da oke je da se pokrene
		this.addChangeListener(l -> { 
			if (this.getSelectedIndex() != -1) {
				if (this.addedFlag == true) {
					this.addedFlag = false;
				}else if(this.removedFlag == true){
					this.removedFlag = false;
				}else {
					System.out.println("Stanje prije klika");
					System.out.println("Previous doc index: " + this.previousDocIndex);
					System.out.println("Current doc index " + this.currentDocIndex);
					this.previousDocument = currentDocument;
					this.previousDocIndex = this.currentDocIndex;
					this.currentDocument = allDocuments.get(this.getSelectedIndex());
					this.currentDocIndex = this.getSelectedIndex();
					System.out.println("Stanje poslije klika");
					System.out.println("Previous doc index: " + this.previousDocIndex);
					System.out.println("Current doc index " + this.currentDocIndex);
					System.out.println("------------------");
				}

			}


			//prilikom svake promjene trenutnog taba, obavijesti sve zainteresirane listenere
			for (MultipleDocumentListener multipleDocListener : listeners) {
				multipleDocListener.currentDocumentChanged(this.previousDocument, this.currentDocument);
			}
		});
		
		//this.firePropertyChange(propertyName, oldValue, newValue);
		

		
		//dodao bi i ovo, jer nemoguće je da išta drugo osim novog praznog dokumneta bude otvoreno prilikom otvaranja notepada
		//ali sad kad smo ga stvorili, treba ga negdje i dodati
		

	}

	public Iterator<SingleDocumentModel> iterator() {
		return this.allDocuments.iterator();
	}

	public SingleDocumentModel createNewDocument() {
		DefaultSingleDocumentModel newDocument = new DefaultSingleDocumentModel(null, "");
		this.allDocuments.add(newDocument);
		this.previousDocIndex = this.currentDocIndex;
		this.currentDocIndex = this.allDocuments.size() - 1; //novu karticu uvijek dodajemo na kraj
		this.previousDocument = this.currentDocument;

		this.currentDocument = newDocument;
		this.addedFlag = true;
		this.add(new JScrollPane(this.currentDocument.getTextComponent()));
		this.setTitleAt(this.getTabCount() - 1, "untitled");
		this.setToolTipTextAt(this.currentDocIndex, "(unnamed)");
		this.setIconAt(currentDocIndex, this.greenDiskIcon);
		
		newDocument.addSingleDocumentListener(documentListener);
		newDocument.getTextComponent().addCaretListener(caretListener);

		this.setSelectedIndex(this.getTabCount() - 1); // služi da postavi trenutno odabrani tab na tab novog dokumenta
		
		for (MultipleDocumentListener l : listeners) {
			l.documentAdded(newDocument);
		}
		
		return newDocument;
	}

	public SingleDocumentModel getCurrentDocument() {
		return this.currentDocument;
	}

	public SingleDocumentModel loadDocument(Path path) {
		
		Path fileName = path.getFileName();
		byte[] okteti;
		try {
			okteti = Files.readAllBytes(path);
		} catch(Exception ex) {
			JOptionPane.showMessageDialog(
					DefaultMultipleDocumentModel.this, 
					"Pogreška prilikom čitanja datoteke "+ fileName +".", 
					"Pogreška", 
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
		this.createNewDocument();
		
		String tekst = new String(okteti, StandardCharsets.UTF_8);
		this.currentDocument.getTextComponent().setText(tekst);
		this.currentDocument.setFilePath(path);
		this.setTitleAt(this.getTabCount() - 1, fileName.toString());
		this.setIconAt(currentDocIndex, this.greenDiskIcon);
		
		return this.currentDocument;
	}

	public void saveDocument(SingleDocumentModel model, Path newPath) {
		
		byte[] podatci = model.getTextComponent().getText().getBytes(StandardCharsets.UTF_8);
		try {
			Files.write(newPath, podatci);
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(
					DefaultMultipleDocumentModel.this, 
					"Pogreška prilikom zapisivanja datoteke "+ newPath.toFile().getAbsolutePath()+ ".\nPažnja: nije jasno u kojem je stanju datoteka na disku!", 
					"Pogreška", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		model.setFilePath((newPath));
		model.setModified(false);
		this.setIconAt(currentDocIndex, this.greenDiskIcon);
	}
		
		// ova metoda prvo sprema dokument nad kojim se poziva
		// a zatim stavlja njegov modified flag na false!
		// čini se također da može postaviti novi path za dokument (valjda ako se koristi save as)
		
	

	//treba doraditi
	public void closeDocument(SingleDocumentModel model) {
		
		for (MultipleDocumentListener l : listeners) {
			l.documentRemoved(currentDocument);
		}
	
		if (this.allDocuments.size() == 1) {
			System.out.println("Ovdje!");
			this.getCurrentDocument().removeSingleDocumentListener(documentListener);
			this.allDocuments.remove(this.currentDocument);
			this.removedFlag = true;
			this.remove(0);
			this.previousDocIndex = 0;
			this.currentDocIndex = 0;
			this.createNewDocument();
			return;
		}
		
	
		//ovdje je problem
		//on kad makne jedan indeks, ne uzme u obzir da ga je maknuo
		this.getCurrentDocument().removeSingleDocumentListener(documentListener);
		System.out.println("Broj tabova prije " + this.getTabCount());
		this.removedFlag = true;
		this.allDocuments.remove(this.currentDocument);
		this.remove(currentDocIndex);
		
		if (this.currentDocIndex < this.previousDocIndex) {
			this.currentDocIndex--;
			this.previousDocIndex--;
		}
		
		this.currentDocIndex = this.previousDocIndex;
		//ili this.currentDocIndex = this.currentDocIndex - 1
		this.currentDocument = this.previousDocument;
		System.out.println("Broj tabova poslije " + this.getTabCount());

		if (this.currentDocIndex != 0) {
			this.previousDocIndex = this.currentDocIndex - 1;
		}else {
			this.previousDocIndex = this.currentDocIndex;
		}
		
		this.previousDocument = this.allDocuments.get(this.previousDocIndex);
		this.fireStateChanged(); //ukoliko bude bugova, makni ovo
		
		//također imaj na umu da ako si zatvorio zadnji "pravi" dokument moraš otvoriti barem 1 prazni dokument
		
	}

	public void addMultipleDocumentListener(MultipleDocumentListener l) {
		this.listeners.add(l);
		
	}

	public void removeMultipleDocumentListener(MultipleDocumentListener l) {
		this.listeners.remove(l);
		
	}

	public int getNumberOfDocuments() {
		return this.allDocuments.size();
	}

	public SingleDocumentModel getDocument(int index) {
		return this.allDocuments.get(index);
	}
	
	private ImageIcon loadIcon(String path){

		byte[] bytes = null;
		try(InputStream is = this.getClass().getResourceAsStream(path)) {
			if(is==null) {
				throw new FileNotFoundException("Cannot open the image file!");
			}
			bytes = is.readAllBytes();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//skaliramo sliku da ne ud
		ImageIcon newImageIcon = new ImageIcon(bytes);
		Image image = newImageIcon.getImage(); // transform it 
		Image newimg = image.getScaledInstance(15, 15,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
		newImageIcon = new ImageIcon(newimg);  // transform it back
		
		return newImageIcon;
		
	}
	

	//prati promjene u trenutno otvorenom dokumentu
	//može se koristiti za enablanje/disablanje tipki koje služe za manipulaciju teksta ili nešto hm
	private SingleDocumentListener documentListener = new SingleDocumentListener() {
		
		@Override
		public void documentModifyStatusUpdated(SingleDocumentModel model) {
			setIconAt(currentDocIndex, redDiskIcon);
			for (MultipleDocumentListener l : listeners) {
				l.currentDocumentChanged(previousDocument, currentDocument);
			}
			
			//treba javiti i slušaču jnotepada
			
			//ovdje postaviti ikonu na crvenu, jer ovo znači da se nešto pisalo
			//i da je edited flag true
			
		}
		
		@Override
		public void documentFilePathUpdated(SingleDocumentModel model) {
			setTitleAt(currentDocIndex, model.getFilePath().getFileName().toString());
			setToolTipTextAt(currentDocIndex, model.getFilePath().toString());
			for (MultipleDocumentListener l : listeners) {
				l.currentDocumentChanged(previousDocument, currentDocument);
				//ovdje samo promijeni naziv trenutnog taba, te eventualno javi i JNotepaduPP da treba promijeniti naslov
			}
			// TODO Auto-generated method stub
			//kad se ovo zove? kako editor može sam sebi path promijeniti?
			//možda kad se pozove metoda save as, koja mijenja ime singledocumenta
			//onda on pozove ovo, a ovaj listener obavijesti slušača multipledoclistener (slušač kojeg je postavio notepad), i taj slušač o
			
		}
	};
	
	private CaretListener caretListener = new CaretListener() {
		
		@Override
		public void caretUpdate(CaretEvent e) {
//			for (MultipleDocumentListener l : listeners) {
//				l.currentDocumentChanged(previousDocument, currentDocument);
//			}
			
		}
	};
}
