package demo;

import hr.fer.zemris.lsystems.LSystem;
import hr.fer.zemris.lsystems.LSystemBuilder;
import hr.fer.zemris.lsystems.LSystemBuilderProvider;
import hr.fer.zemris.lsystems.gui.LSystemViewer;
import hr.fer.zemris.lsystems.impl.LSystemBuilderImpl;

public class Koch1 {
	
	static Loader loader = new Loader("kochCurve");
	public static void main(String[] args) {
		LSystemViewer.showLSystem(createKochCurve(LSystemBuilderImpl::new));

	}
	
	
	private static LSystem createKochCurve(LSystemBuilderProvider provider) {
		String[] data = loader.LoadFromFile();
		return provider.createLSystemBuilder().configureFromText(data).build();
		
	}

}
