package demo;

import hr.fer.zemris.lsystems.LSystem;
import hr.fer.zemris.lsystems.LSystemBuilderProvider;
import hr.fer.zemris.lsystems.gui.LSystemViewer;
import hr.fer.zemris.lsystems.impl.LSystemBuilderImpl;

public class HilbertCurve {
	
	static Loader loader = new Loader("hilbertCurve");
	public static void main(String[] args) {
		LSystemViewer.showLSystem(createKochCurve(LSystemBuilderImpl::new));

	}
	
	
	private static LSystem createKochCurve(LSystemBuilderProvider provider) {
		String[] data = loader.LoadFromFile();
		return provider.createLSystemBuilder().configureFromText(data).build();
		
	}

}
