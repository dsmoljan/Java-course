package hr.fer.oprpp1.hw08.jnotepadapp.local;

/**
 * A class which acts as a decorator for LocalizationProvider; it "intercepts" request heading its way, does something extra (in
 * this case takes care of listeners management) and passes the request further.
 * @author Dorian
 *
 */
public class LocalizationProviderBridge extends AbstractLocalizationProvider{
	
	private boolean connected;
	String currentLanguage;
	private ILocalizationProvider localizatonProvider;
	
	public LocalizationProviderBridge(LocalizationProvider localizationProvider) {
		this.localizatonProvider = localizationProvider;
		connected = false;
	}
	
	public void connect() {
		System.out.println("Connecting...");
		if (this.currentLanguage != localizatonProvider.getCurrentLanguage()) {
			System.out.println("Language of the localization provider has changed in relation to the language of the localization bridge");
		}
		
		this.currentLanguage = localizatonProvider.getCurrentLanguage();
		
		if (!connected) {
			localizatonProvider.addLocalizationListener(anonimousLocalizationListener);
			connected = true;
		}

}
	
	public void disconnect() {
		localizatonProvider.removeLocalizationListener(anonimousLocalizationListener);
		connected = false;
	}
	
	public String getString(String key) {
		return localizatonProvider.getString(key);
	}
	
	//svi listeneri se ustvari registriraju nad ovom klasom
	//a nitijedan nad "pravim" localizationProviderom
	private ILocalizationListener anonimousLocalizationListener = new ILocalizationListener() {
		
		@Override
		public void localizationChanged() {
			fire();
			
		}
	};

	@Override
	public String getCurrentLanguage() {
		return localizatonProvider.getCurrentLanguage();
	}


	
	

}
