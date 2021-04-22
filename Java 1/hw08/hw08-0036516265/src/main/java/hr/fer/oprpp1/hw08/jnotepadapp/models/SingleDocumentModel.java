package hr.fer.oprpp1.hw08.jnotepadapp.models;

import java.nio.file.Path;

import javax.swing.JTextArea;

/**
 * Represents a model of single document, having information about file path
from which document was loaded (can be null for new document), document modification status
and reference to Swing component which is used for editing (each document has its own editor
component).
 * @author Dorian
 *
 */
public interface SingleDocumentModel {
	JTextArea getTextComponent();

	Path getFilePath();

	void setFilePath(Path path);

	boolean isModified();

	void setModified(boolean modified);

	void addSingleDocumentListener(SingleDocumentListener l);

	void removeSingleDocumentListener(SingleDocumentListener l);
}