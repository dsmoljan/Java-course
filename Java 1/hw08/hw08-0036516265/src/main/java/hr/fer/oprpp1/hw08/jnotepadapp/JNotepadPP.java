package hr.fer.oprpp1.hw08.jnotepadapp;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorTable;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;

import hr.fer.oprpp1.hw08.jnotepadapp.local.FormLocalizationProvider;
import hr.fer.oprpp1.hw08.jnotepadapp.local.LocalizableAction;
import hr.fer.oprpp1.hw08.jnotepadapp.local.LocalizableJMenu;
import hr.fer.oprpp1.hw08.jnotepadapp.local.LocalizationProvider;
import hr.fer.oprpp1.hw08.jnotepadapp.models.MultipleDocumentListener;
import hr.fer.oprpp1.hw08.jnotepadapp.models.SingleDocumentListener;
import hr.fer.oprpp1.hw08.jnotepadapp.models.SingleDocumentModel;
import hr.fer.oprpp1.hw08.jnotepadapp.models.impl.DefaultMultipleDocumentModel;

public class JNotepadPP extends JFrame{

	private static final long serialVersionUID = 1L;
	
	private DefaultMultipleDocumentModel documents;
	private StatusBar statusBar;
	private JPanel tabbedPanePanel;
	private FormLocalizationProvider flp = new FormLocalizationProvider(LocalizationProvider.getInstance(), JNotepadPP.this);


	
	public JNotepadPP() {
		super();
		flp =  new FormLocalizationProvider(LocalizationProvider.getInstance(), this);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); //imati ćemo listenera koji osluškuje je li stisnuta tipka za zatvaranje prozora
		setLocation(0, 0);
		setSize(600, 600);
		this.statusBar = new StatusBar();
		this.documents = new DefaultMultipleDocumentModel();
		this.setTitle("(unnamed) - JNotepad++");
		tabbedPanePanel = new JPanel(new BorderLayout());
		//flp = new FormLocalizationProvider(LocalizationProvider.getInstance(), this);


		this.documents.addMultipleDocumentListener(docListener);
		this.documents.getCurrentDocument().getTextComponent().addCaretListener(caretListener);
		
		WindowListener wl = new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				Iterator iterator = documents.iterator();
				//iterator.next();
				SingleDocumentModel document;
				String[] options = {flp.getString("yes"), flp.getString("no"), flp.getString("cancel")};

			
				while (iterator.hasNext()) {
					document = (SingleDocumentModel) iterator.next();
					Path filePath = document.getFilePath();
					String pathString = document.getFilePath() == null ? "(untitled)" : document.getFilePath().getFileName().toString();
					if (document.isModified() == true) {
						
						int choice = JOptionPane.showOptionDialog(JNotepadPP.this,
								flp.getString("file") + " " + pathString + " " + flp.getString("fileNotSavedMessage"),
								flp.getString("warningTitle"),
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE,
								null,
								options,
								options[2]);
					switch(choice)
					{
						case 0:
							saveAsFile(filePath);
							flp.connect();
						case 1:
							flp.connect();
							continue;
						case 2:
							flp.connect();
							return;
					}
						
					}
				}
				System.exit(0);
				dispose();
			}
			
		};
		
		this.addWindowListener(wl);
		initGUI();
	}
	

	private void initGUI() {
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(tabbedPanePanel, BorderLayout.CENTER);
		tabbedPanePanel.add(this.documents, BorderLayout.CENTER);
		
		createStatusBar();
		createActions();
		createMenus();
		createToolbars();
	}
	
	private void createStatusBar() {
		this.add(statusBar, BorderLayout.SOUTH);
		
	}
	
	private void updateStatusBar() {
		
		Map<String, Integer> valuesMap = getStatusBarInfo();
		System.out.println("U update status bar sam");
		this.statusBar.setLenValue(valuesMap.get("length"));
		this.statusBar.setLinesValue(valuesMap.get("caretRow") + 1);
		this.statusBar.setColumnsValue(valuesMap.get("caretCol") + 1);
		this.statusBar.setSelValue(valuesMap.get("noOfSelected"));
	}
	
	private Action newDocumentAction = new LocalizableAction("newDocumentActionName", "newDocumentActionDescription", flp) {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			documents.createNewDocument();
		}
	};
	
	private Action openDocumentAction = new LocalizableAction("openDocumentActionName", "openDocumentActionDescription", flp) {
		
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle(flp.getString("openFile"));
			if(fc.showOpenDialog(JNotepadPP.this)!=JFileChooser.APPROVE_OPTION) {
				return;
			}
			File fileName = fc.getSelectedFile();
			Path filePath = fileName.toPath();
			if(!Files.isReadable(filePath)) {
				JOptionPane.showMessageDialog( //ovo zamijeniti sa show option dialog jer se lakše lokalizira
						JNotepadPP.this, 
						flp.getString("file") + " " +fileName.getAbsolutePath()+ " " + flp.getString("doesntExist"), 
						flp.getString("errorMessage"), 
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			documents.loadDocument(filePath);
		}
	};
	
	private Action saveDocumentAction = new LocalizableAction("saveDocumentActionName", "saveDocumentActionDescription", flp) {
		
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {	
			
			Path openedFilePath = documents.getCurrentDocument().getFilePath();
			if(openedFilePath==null) {
				
				saveAsFile(openedFilePath);
				
			}else {
				documents.saveDocument(documents.getCurrentDocument(), openedFilePath);
				
				JOptionPane.showMessageDialog(
						JNotepadPP.this, 
						flp.getString("fileSaved"), 
						flp.getString("information"), 
						JOptionPane.INFORMATION_MESSAGE);
			}
			

		}
	};
	
	private Action saveAsDocumentAction = new LocalizableAction("saveAsDocumentActionName", "saveAsDocumentActionDescription", flp) {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			
			Path openedFilePath = documents.getCurrentDocument().getFilePath();
			saveAsFile(openedFilePath);		
			
		}
	};
	
	private void saveAsFile(Path openedFilePath) {
		
		JFileChooser jfc = new JFileChooser();
		jfc.setDialogTitle("Save document");
		if(jfc.showSaveDialog(JNotepadPP.this)!=JFileChooser.APPROVE_OPTION) {
			JOptionPane.showMessageDialog(
					JNotepadPP.this, 
					flp.getString("nothingSavedMessage"), 
					flp.getString("warningTitle"), 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		openedFilePath = jfc.getSelectedFile().toPath();
		
		String[] options = {flp.getString("yes"), flp.getString("no")};
		if (Files.exists(openedFilePath) == true){
			
			int choice = JOptionPane.showOptionDialog(JNotepadPP.this,
						flp.getString("fileAlreadyExistsMessage"),
						flp.getString("warningTitle"),
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options,
						options[1]);
			switch(choice)
			{
				case 0:
					documents.saveDocument(documents.getCurrentDocument(), openedFilePath);
					
					JOptionPane.showMessageDialog(
							JNotepadPP.this, 
							flp.getString("fileSaved"), 
							flp.getString("information"), 
							JOptionPane.INFORMATION_MESSAGE);
					return;
				case 1:
					JOptionPane.showMessageDialog(
							JNotepadPP.this, 
							flp.getString("nothingSavedMessage"), 
							flp.getString("warningTitle"), 
							JOptionPane.WARNING_MESSAGE);
							return;
			}
			
		}else {
			documents.saveDocument(documents.getCurrentDocument(), openedFilePath);	
			
			JOptionPane.showMessageDialog(
					JNotepadPP.this, 
					flp.getString("fileSaved"), 
					flp.getString("information"), 
					JOptionPane.INFORMATION_MESSAGE);
		}
		
	}
	
	private Action closeDocumentAction = new LocalizableAction("closeDocumentActionName", "closeDocumentActionDescription", flp) {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			//prvo provjeriti je li trenutno otvoreni dokument modificiran
			//ako je, treba pitati korisnika želi li spremiti promjene ili ne, ili želi odustati
			//ako želi spremiti, prvo se poziva documents.save te zatim documents.close
			//ako ne želi, odmah se poziva documents.close
			documents.closeDocument(documents.getCurrentDocument());
			
		}
	};
	
	private Action getStatisticsAction = new LocalizableAction("getStatisticsActionName", "getStatisticsActionDescription", flp) {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			Map<String, Integer> valuesMap = calculateStatistics();

			JOptionPane.showMessageDialog( //ovo zamijeniti sa show option dialog jer se lakše lokalizira
					JNotepadPP.this, 
					flp.getString("statisticsMessagePart1") + " " + Integer.toString(valuesMap.get("noOfChars")) + " " + flp.getString("statisticsMessagePart2") + Integer.toString(valuesMap.get("noOfRegularChars")) + " " + flp.getString("statisticsMessagePart3")
							+ Integer.toString(valuesMap.get("noOfRows")) + " " + flp.getString("statisticsMessagePart4"), 
					flp.getString("statistics"), 
					JOptionPane.INFORMATION_MESSAGE);
		}
	};
	
	private Action copyTextAction = new LocalizableAction("copyTextActionName", "copyTextActionDescription", flp) {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			manipulateSelectedText("copy");
		}
	};
	
	private Action cutTextAction = new LocalizableAction("cutTextActionName", "cutTextActionDescription", flp) {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			manipulateSelectedText("copy");
			manipulateSelectedText("delete");
			
		}
	};
	
	private Action pasteTextAction = new LocalizableAction("pasteTextActionName", "pasteTextActionDescription", flp) {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			manipulateSelectedText("paste");
		}
	};
	
	private Action toUpperCaseAction = new LocalizableAction("toUpperCaseActionName", "toUpperCaseActionDescription", flp) {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			manipulateSelectedText("toUpperCase");
			
		}
	};
	
	private Action toLowerCaseAction = new LocalizableAction("toLowerCaseActionName", "toLowerCaseActionDescription", flp) {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			manipulateSelectedText("toLowerCase");
			
		}
	};
	
	private Action invertCaseAction = new LocalizableAction("invertCaseActionName", "invertCaseActionDescription", flp) {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			manipulateSelectedText("invertCase");
			
		}
	};
	
	private Action sortAscendingAction = new LocalizableAction("sortAscendingActionName", "sortAscendingActionDescription", flp) {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			manipulateCurrenltySelectedLines("sortAscending");	
		}
	};
	
	private Action sortDescendingAction = new LocalizableAction("sortDescendingActionName", "sortDescendingActionDescription", flp) {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			manipulateCurrenltySelectedLines("sortDescending");	
		}
	};
	
	private Action removeDuplicateLinesAction = new LocalizableAction("removeDuplicatesActionName", "removeDuplicatesActionDescription", flp) {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			manipulateCurrenltySelectedLines("remveDuplicateLines");
			
		}
	};
	
	
	private Action exitAction = new LocalizableAction("exitActionName", "exitActionDescription", flp) {
		
		private static final long serialVersionUID = 1L;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	};
	

		
	private void createMenus() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new LocalizableJMenu("file", flp);
		menuBar.add(fileMenu);

		fileMenu.add(new JMenuItem(newDocumentAction));
		fileMenu.add(new JMenuItem(openDocumentAction));
		fileMenu.add(new JMenuItem(saveDocumentAction));
		fileMenu.add(new JMenuItem(saveAsDocumentAction));
		fileMenu.add(new JMenuItem(closeDocumentAction));
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(exitAction));
		
		JMenu editMenu = new LocalizableJMenu("edit", flp);
		menuBar.add(editMenu);
		
		editMenu.add(new JMenuItem(copyTextAction));
		editMenu.add(new JMenuItem(cutTextAction));
		editMenu.add(new JMenuItem(pasteTextAction));

		editMenu.add(new JMenuItem(pasteTextAction));
		
		JMenu infoMenu = new LocalizableJMenu("info", flp);
		menuBar.add(infoMenu);
		infoMenu.add(new JMenuItem(getStatisticsAction));
		
		JMenu toolsMenu = new LocalizableJMenu("tools", flp);
		menuBar.add(toolsMenu);
		JMenu changeCaseMenu = new LocalizableJMenu("changecase", flp);
		
		changeCaseMenu.add(new JMenuItem(toUpperCaseAction));
		changeCaseMenu.add(new JMenuItem(toLowerCaseAction));
		changeCaseMenu.add(new JMenuItem(invertCaseAction));
		toolsMenu.add(changeCaseMenu);
		
		JMenu sortMenu = new LocalizableJMenu("sort", flp);
		sortMenu.add(new JMenuItem(sortAscendingAction));
		sortMenu.add(new JMenuItem(sortDescendingAction));
		toolsMenu.add(sortMenu);
		
		toolsMenu.add(removeDuplicateLinesAction);
		
		JMenu languagesMenu = new LocalizableJMenu("languages", flp);
		menuBar.add(languagesMenu);

		
		languagesMenu.add(new JMenuItem(new LocalizableAction("hrLanguageMenuName", "hrLanguageMenuDescription", flp) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				LocalizationProvider.getInstance().setLanguage("hr");
				
			}
		}));
		languagesMenu.add(new JMenuItem(new LocalizableAction("enLanguageMenuName", "enLanguageMenuDescription", flp) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				LocalizationProvider.getInstance().setLanguage("en");
				
			}
		}));
		languagesMenu.add(new JMenuItem(new LocalizableAction("deLanguageMenuName", "deLanguageMenuDescription", flp) {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				LocalizationProvider.getInstance().setLanguage("de");
				
			}
		}));
		
				
		this.setJMenuBar(menuBar);
	}
	
	private void createToolbars() {
		JToolBar toolBar = new JToolBar("Alati");
		toolBar.setFloatable(true);
		
		toolBar.add(new JButton(toLowerCaseAction));
		toolBar.add(new JButton(toUpperCaseAction));
		toolBar.add(new JButton(invertCaseAction));

		toolBar.addSeparator();
		toolBar.add(new JButton(sortAscendingAction));
		toolBar.add(new JButton(sortDescendingAction));
		toolBar.add(new JButton(removeDuplicateLinesAction));

		
		this.tabbedPanePanel.add(toolBar, BorderLayout.PAGE_START);
	}
	
	private void createActions() {
		
		newDocumentAction.putValue(
				Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke("control N"));
		newDocumentAction.putValue(
				Action.MNEMONIC_KEY,
				KeyEvent.VK_N);
		
		openDocumentAction.putValue(
				Action.ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke("control O")); 
		openDocumentAction.putValue(
				Action.MNEMONIC_KEY, 
				KeyEvent.VK_O); 
		
		saveDocumentAction.putValue(
				Action.ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke("control S")); 
		saveDocumentAction.putValue(
				Action.MNEMONIC_KEY, 
				KeyEvent.VK_S); 
		
		saveAsDocumentAction.putValue(
				Action.ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke("control alt S")); 
		saveAsDocumentAction.putValue(
				Action.MNEMONIC_KEY, 
				KeyEvent.VK_A);  
		
		closeDocumentAction.putValue(
				Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke("control W"));
		closeDocumentAction.putValue(
				Action.MNEMONIC_KEY,
				KeyEvent.VK_W);
		
		getStatisticsAction.putValue(
				Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke("control I"));
		getStatisticsAction.putValue(
				Action.MNEMONIC_KEY,
				KeyEvent.VK_I);
		
		
		exitAction.putValue(
				Action.ACCELERATOR_KEY, 
				KeyStroke.getKeyStroke("control E"));
		exitAction.putValue(
				Action.MNEMONIC_KEY, 
				KeyEvent.VK_E); 
		
		copyTextAction.putValue(
				Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke("control C"));
		copyTextAction.putValue(
				Action.MNEMONIC_KEY,
				KeyEvent.VK_C);
		
		cutTextAction.putValue(
				Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke("control X"));
		cutTextAction.putValue(
				Action.MNEMONIC_KEY,
				KeyEvent.VK_X);
		
		pasteTextAction.putValue(
				Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke("control V"));
		pasteTextAction.putValue(
				Action.MNEMONIC_KEY,
				KeyEvent.VK_V);
		
		toUpperCaseAction.putValue(
				Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke("control U"));
		toUpperCaseAction.putValue(
				Action.MNEMONIC_KEY,
				KeyEvent.VK_U);
		
		toLowerCaseAction.putValue(
				Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke("control L"));
		toLowerCaseAction.putValue(
				Action.MNEMONIC_KEY,
				KeyEvent.VK_L);
		
		invertCaseAction.putValue(
				Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke("control I"));
		invertCaseAction.putValue(
				Action.MNEMONIC_KEY,
				KeyEvent.VK_I);
		
		sortAscendingAction.putValue(
				Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke("control S A"));
		sortAscendingAction.putValue(
				Action.MNEMONIC_KEY,
				KeyEvent.VK_A);
		
		sortDescendingAction.putValue(
				Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke("control S D"));
		sortDescendingAction.putValue(
				Action.MNEMONIC_KEY,
				KeyEvent.VK_D);
		
		removeDuplicateLinesAction.putValue(
				Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke("control R"));
		removeDuplicateLinesAction.putValue(
				Action.MNEMONIC_KEY,
				KeyEvent.VK_D);;
		
	}
	
	/**
	 * Privatna metoda za manipulaciju trenutno odabranim tekstom - brisanje, kopiranje, promjena casinga itd.
	 */
	private void manipulateSelectedText(String type) {
		
		JTextArea editor = documents.getCurrentDocument().getTextComponent();
		Document doc = editor.getDocument();
		int len = Math.abs(editor.getCaret().getDot()-editor.getCaret().getMark());
		int offset = Math.min(editor.getCaret().getDot(),editor.getCaret().getMark());

		
		switch (type)
		{
			case "copy":
				try {
					if(len==0) return;
				    StringSelection selection = new StringSelection(doc.getText(offset, len));
				    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				    clipboard.setContents(selection, selection);
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
				break;
			case "paste":
				try {
					String data = (String) Toolkit.getDefaultToolkit()
			                .getSystemClipboard().getData(DataFlavor.stringFlavor);
				    
				    doc.insertString(offset, data, null);
				} catch (BadLocationException | HeadlessException | UnsupportedFlavorException | IOException e1) {
					e1.printStackTrace();
				}
				break;
			case "delete":
				if(len==0) return;
				try {
					doc.remove(offset, len);
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
				break;
			case "toUpperCase":
				if(len==0) return;
				try {
					String originalText = doc.getText(offset, len);
					doc.remove(offset, len);
					doc.insertString(offset, originalText.toUpperCase(), null);
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
				break;
			case "toLowerCase":
				if(len==0) return;
				System.out.println("Zovem toLowerCase");
				try {
					String originalText = doc.getText(offset, len);
					doc.remove(offset, len);
					doc.insertString(offset, originalText.toLowerCase(), null);
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
				break;
			case "invertCase":
				if(len==0) return;
				try {
					String originalText = doc.getText(offset, len);
					String resultText = "";
					doc.remove(offset, len);
					for (int i = 0; i < originalText.length(); i++) {
						String character = String.valueOf(originalText.charAt(i));
						if (character.equals(String.valueOf(originalText.charAt(i)).toUpperCase())) {
							resultText += character.toLowerCase();
						}else {
							resultText += character.toUpperCase();
						}
					}
					doc.insertString(offset, resultText, null);
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
				break;
				
		}
		
	}
	
	private void manipulateCurrenltySelectedLines(String type) {
		JTextArea editor = documents.getCurrentDocument().getTextComponent();
		PlainDocument doc = (PlainDocument) editor.getDocument();
		int len = Math.abs(editor.getCaret().getDot()-editor.getCaret().getMark());
		int offset = Math.min(editor.getCaret().getDot(),editor.getCaret().getMark());
		
		int pos = editor.getCaretPosition(); //current caret position
		
		Element root = doc.getDefaultRootElement();
		int row = root.getElementIndex(pos); // zero-based line index...

		int startingRow = root.getElementIndex(pos);
		int endRow = root.getElementIndex(editor.getCaret().getMark());
		
		int startingPosition = root.getElement(startingRow).getStartOffset();
		int endingPosition = root.getElement(endRow).getEndOffset();
		
		
		List<String> data = new ArrayList<>();
		
		try {
			
			String[] allSelectedLiens = doc.getText(startingPosition, endingPosition - startingPosition).split("\n");
			for (String s : allSelectedLiens) {
				data.add(s);
			}

			doc.remove(startingPosition, endingPosition - startingPosition - 1);
			
			Locale hrLocale = new Locale("hr");
			Collator hrCollator = Collator.getInstance(hrLocale);
			
			if (type.equals("sortAscending")) {
				data.sort(hrCollator);
			}else if (type.equals("sortDescending")){
				data.sort(hrCollator.reversed());
			}else {
				data = data.stream().distinct().collect(Collectors.toList());
			}
			
			String result = "";
			
			for (String s : data) {
				result += s + "\n";
			}
			
			result = result.substring(0, result.length() - 1);
			doc.insertString(startingPosition, result, null);
			
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
	}
	private Map<String, Integer> getStatusBarInfo(){
		
		Map<String, Integer> valuesMap = new LinkedHashMap<>();
		JTextArea editor = documents.getCurrentDocument().getTextComponent();
		Document doc = editor.getDocument();
		Element root = doc.getDefaultRootElement();
		
		int pos = editor.getCaretPosition();
		int caretRow = root.getElementIndex(pos); // zero-based line index...

		
		int noOfSelected = Math.abs(editor.getCaret().getDot()-editor.getCaret().getMark());
		int caretCol =  pos - root.getElement(caretRow).getStartOffset(); // also zero-based...;
		int length = doc.getLength();
		
		valuesMap.put("length", length);
		valuesMap.put("noOfSelected", noOfSelected);
		valuesMap.put("caretRow", caretRow);
		valuesMap.put("caretCol", caretCol);

		return valuesMap;
		
	}
	
	private Map<String, Integer> calculateStatistics() {
		Map<String, Integer> valuesMap = new LinkedHashMap<>();
		
		JTextArea editor = documents.getCurrentDocument().getTextComponent();
		Document doc = editor.getDocument();
		
		int noOfChars = doc.getLength();
		int noOfSpaces = 0; //tabs and \n
		int noOfRows = 1;

		String text = "";
		try {
			text = doc.getText(0, noOfChars);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
		for (int i = 0; i < noOfChars; i++) {
			char c = text.charAt(i);
			if (c == ' ' || c == '\n' || c == '\t') {
				noOfSpaces++;
			}
			if (c == '\n') {
				noOfRows++;
			}
			
		}
		
		int noOfRegularChars = noOfChars - noOfSpaces;
		
		valuesMap.put("noOfChars", noOfChars);
		valuesMap.put("noOfSpaces", noOfSpaces);
		valuesMap.put("noOfRegularChars", noOfRegularChars);
		valuesMap.put("noOfRows", noOfRows);

		
		return valuesMap;
	}

	//listeneri
	
	private MultipleDocumentListener docListener = new MultipleDocumentListener() {
		
		//ove metode ti poziva tvoja instanca DefaultMultipleDocumentModel kada dođe do neke promjene ili nešto
		//a ti ovdje definiraš što se radi prilikom toga
		
		@Override
		public void documentRemoved(SingleDocumentModel model) {
			model.removeSingleDocumentListener(singleDocListener); 
			model.getTextComponent().removeCaretListener(caretListener);
			
		}
		
		@Override
		public void documentAdded(SingleDocumentModel model) {
			System.out.println("U document added sam");
			model.addSingleDocumentListener(singleDocListener);
			model.getTextComponent().addCaretListener(caretListener);
			// ovdje možeš dodati slušača na singledocument
			// i on nek zasebno prati promjene na caretu i promjene u tekstu
			// da ne pozivaš bespotrebno računanje cijele statistike ako korisnik samo malo pomakne caret
			// i ako dođe do promjene u trenutnom dokumentu, zoveš update statistics
			//inače, ako samo dođe do promjene u caretu, zoveš samo updateCaretStatistic
			
		}
		
		@Override
		public void currentDocumentChanged(SingleDocumentModel previousModel, SingleDocumentModel currentModel) {
			//pomoću ovog kad dođe do promjene u DocumentModelu, ovdje promijeni prikaz samo
			//mijenjaš naziv prozora, te informacije u statusbaru, te info o kursoru
			if (currentModel != null) {
				//System.out.println("Promijenio se panel, postavljam naziv prozora na " + currentModel.getFilePath().toString());
				Path path = currentModel.getFilePath();
				if (path == null) {
					setTitle("(unnamed) - JNotepad++");
				}else {
					setTitle(currentModel.getFilePath().toString() +  " - JNotepad++");
				}
				
				//te također ovdje treba pozvati promjenu status bara!!!
			}
			
			updateStatusBar(); //ali po novom, currentDocumentChanged se poziva samo kad zaista dođe do promjene u dokumentu, a ne samo micanju careta
		}
	};
	
	private SingleDocumentListener singleDocListener = new SingleDocumentListener() {
		
		@Override
		public void documentModifyStatusUpdated(SingleDocumentModel model) {
			updateStatusBar();
			
		}
		
		@Override
		public void documentFilePathUpdated(SingleDocumentModel model) {
			if (model != null) {
				Path path = model.getFilePath();
				if (path == null) {
					setTitle("(unnamed) - JNotepad++");
				}else {
					setTitle(model.getFilePath().toString() +  " - JNotepad++");
				}
			}
			
		}
	};
	
	private CaretListener caretListener = new CaretListener() {
		
		@Override
		public void caretUpdate(CaretEvent e) {
			System.out.println("U caretListeneru sam");
			updateStatusBar();
			Map<String, Integer> valuesMap = getStatusBarInfo();
			int noOfSelected = valuesMap.get("noOfSelected");
			if (noOfSelected == 0) {
				toLowerCaseAction.setEnabled(false);
				toUpperCaseAction.setEnabled(false);
				invertCaseAction.setEnabled(false);
				
				sortAscendingAction.setEnabled(false);
				sortDescendingAction.setEnabled(false);
				removeDuplicateLinesAction.setEnabled(false);
			}else {
				toLowerCaseAction.setEnabled(true);
				toUpperCaseAction.setEnabled(true);
				invertCaseAction.setEnabled(true);
				
				sortAscendingAction.setEnabled(true);
				sortDescendingAction.setEnabled(true);
				removeDuplicateLinesAction.setEnabled(true);
			}
			
		}
	};
	
	//za svaku akciju (npr. stvaranje novog prozora, koja se ustvari samo svodi na pozivanje odgovrajuće metode iz document), napiši akciju
	//jer će se svaka takva akcija najčešće moći zvati iz barem 2 mjesta
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new JNotepadPP().setVisible(true);
			}
		});
	}

}
