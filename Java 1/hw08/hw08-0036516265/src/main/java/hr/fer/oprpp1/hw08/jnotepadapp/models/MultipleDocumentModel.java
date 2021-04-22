package hr.fer.oprpp1.hw08.jnotepadapp.models;

import java.nio.file.Path;

/**
 * Represents a model capable of holding zero, one or more documents,
where each document and having a concept of current document – the one which is shown to the
user and on which user works
 * @author Dorian
 *
 */
public interface MultipleDocumentModel extends Iterable<SingleDocumentModel> {
	SingleDocumentModel createNewDocument();

	SingleDocumentModel getCurrentDocument();

	SingleDocumentModel loadDocument(Path path);

	void saveDocument(SingleDocumentModel model, Path newPath);

	void closeDocument(SingleDocumentModel model);

	void addMultipleDocumentListener(MultipleDocumentListener l);

	void removeMultipleDocumentListener(MultipleDocumentListener l);

	int getNumberOfDocuments();

	SingleDocumentModel getDocument(int index);
}
