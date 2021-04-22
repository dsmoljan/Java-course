package hr.fer.oprpp1.hw08.jnotepadapp.local;

public interface ILocalizationProvider {
	
	void addLocalizationListener(ILocalizationListener listener);
	
	void removeLocalizationListener(ILocalizationListener listener);
	
	/**
	 * Based on the given key - translation key, returns that translation
	 * @param key
	 * @return
	 */
	String getString(String key);
	
	String getCurrentLanguage();
	

}
