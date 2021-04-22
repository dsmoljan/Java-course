package hr.fer.oprpp1.hw08.jnotepadapp.local;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Singleton
 * @author Dorian
 *
 */
public class LocalizationProvider extends AbstractLocalizationProvider{

	public static final LocalizationProvider instance = new LocalizationProvider();
	
	
	private String language;
	private Locale locale;
	private ResourceBundle bundle;
	
	private LocalizationProvider() {
		this.language = "en"; //default language
		this.locale = Locale.forLanguageTag(language);
		this.bundle = ResourceBundle.getBundle("hr.fer.oprpp1.hw08.jnotepadapp.local.prijevodi", locale);
	}
	


	
	public static LocalizationProvider getInstance() {
		return instance;
	}
	
	public void setLanguage(String language) {
		this.language = language;
		this.locale = Locale.forLanguageTag(language);
		this.bundle = ResourceBundle.getBundle("hr.fer.oprpp1.hw08.jnotepadapp.local.prijevodi", locale);
		this.fire();
	}

	@Override
	public String getString(String key) {
		return bundle.getString(key);
	}




	@Override
	public String getCurrentLanguage() {
		return this.language;
	}
	
	

}
