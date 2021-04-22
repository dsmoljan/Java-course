package hr.fer.oprpp1.hw08.jnotepadapp.local;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLocalizationProvider implements ILocalizationProvider{
	
	private List<ILocalizationListener> listeners = new ArrayList<>();
	

	public AbstractLocalizationProvider() {
	}
	
	@Override
	public void addLocalizationListener(ILocalizationListener listener) {
		listeners.add(listener);
		
	}

	@Override
	public void removeLocalizationListener(ILocalizationListener listener) {
		listeners.remove(listener);
	}

	@Override
	public String getString(String key) {
		return null;
	}
	
	/**
	 * Notifies all registred listeners that a language change has occured
	 */
	void fire() {
		for (ILocalizationListener l : listeners) {
			l.localizationChanged();
		}
	}
	

}
