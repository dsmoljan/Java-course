package hr.fer.oprpp1.hw08.jnotepadapp.local;

import javax.swing.JMenu;

public class LocalizableJMenu extends JMenu{
	private String nameKey;
	private ILocalizationProvider lp;
	

	
	public LocalizableJMenu(String nameKey, ILocalizationProvider lp) {
		
		super(nameKey);
		
		this.nameKey = nameKey;
		this.lp = lp;
		
		this.setText((lp.getString(nameKey)));
		
		lp.addLocalizationListener(new ILocalizationListener() {
			
			@Override
			public void localizationChanged() {
				setText(lp.getString(nameKey));
			}
		});
		
	}

}
