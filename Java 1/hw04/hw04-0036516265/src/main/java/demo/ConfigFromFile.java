package demo;

import hr.fer.zemris.lsystems.gui.LSystemViewer;
import hr.fer.zemris.lsystems.impl.LSystemBuilderImpl;

public class ConfigFromFile {
	
	public static void main(String[] srgs) {
		LSystemViewer.showLSystem(LSystemBuilderImpl::new);
	}

}
