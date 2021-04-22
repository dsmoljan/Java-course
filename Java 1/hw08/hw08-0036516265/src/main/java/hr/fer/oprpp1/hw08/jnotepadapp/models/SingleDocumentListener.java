package hr.fer.oprpp1.hw08.jnotepadapp.models;

public interface SingleDocumentListener {
	void documentModifyStatusUpdated(SingleDocumentModel model);

	void documentFilePathUpdated(SingleDocumentModel model);
}
