package hr.fer.oprpp1.hw08.jnotepadapp.local;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public abstract class LocalizableAction extends AbstractAction{

	private String nameKey;
	private String descriptionKey;
	private ILocalizationProvider lp;
	
	public LocalizableAction(String nameKey, String descriptionKey, ILocalizationProvider lp) {
		
		
		this.nameKey = nameKey;
		this.descriptionKey = descriptionKey;
		this.lp = lp;
		
		this.putValue(NAME, lp.getString(nameKey));
		this.putValue(SHORT_DESCRIPTION, lp.getString(descriptionKey));
		
		lp.addLocalizationListener(new ILocalizationListener() {
			
			@Override
			public void localizationChanged() {
				putValue(NAME, lp.getString(nameKey));
				putValue(SHORT_DESCRIPTION, lp.getString(descriptionKey));
			}
		});
		
	}

}
