package hr.fer.zemris.java.gui.layouts;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Custom layout manager. Za ograničenja (constraints) koristi klasu RCPosition. Za raspoređivanje komponenti ima na raspolaganju
 * fiksnu mrežu 5x7 (5 redaka i 7 stupaca). Zadaća layout managera je da razmjesti komponente containera.
 * @author Dorian
 *
 */
public class CalcLayout implements LayoutManager2 {

	
	//metode za prefered size ti ne rade dobro
	
	private int gap;
	private Map<RCPosition,Component> componentMap;
	
	public static int NO_OF_ROWS = 5;
	public static int NO_OF_COLUMNS = 7;
	
	public interface DimensionGetter{
		Dimension getDimension(Component c);
	}
	
	public static class preferedDimensionGetter implements DimensionGetter{

		@Override
		public Dimension getDimension(Component c) {
			return c.getPreferredSize();
		}
		
	}
	
	public static class minimumDimensionGetter implements DimensionGetter{

		@Override
		public Dimension getDimension(Component c) {
			return c.getMinimumSize();
		}
		
	}
	
	public static class maximumDimensionGetter implements DimensionGetter{

		@Override
		public Dimension getDimension(Component c) {
			return c.getMaximumSize();
		}
		
	}
	
	public Dimension findDimension(DimensionGetter getter) {
		
		int maximumHeight = 0;
		int maximumWidth = 0;
		Component c;
		
		for (RCPosition pos : componentMap.keySet()) {
			c = componentMap.get(pos);
			if (getter.getDimension(c).width > maximumWidth) {
				maximumWidth = getter.getDimension(c).width;
			}
			
			if (getter.getDimension(c).height > maximumHeight) {
				maximumHeight = getter.getDimension(c).height;
			}
		}
		
		//e ove stvari ti ne rade dobro, maximumwidth mora ići noofcolumns puta, iso i maxheight
		return new Dimension((maximumWidth*NO_OF_COLUMNS + gap*(NO_OF_COLUMNS - 1)), (maximumHeight*NO_OF_ROWS + gap*(NO_OF_ROWS - 1))); 
	}
	
	/**
	 * Konstruktor koji kao argument prima jedan integer - razmak između redova i stupaca, u pikselima.
	 * @param gap
	 */
	public CalcLayout(int gap) {
		this.gap = gap;
		this.componentMap = new HashMap<>();
	}
	
	/**
	 * Defaultni konstruktor, postavlja gap na 0.
	 */
	public CalcLayout() {
		this(0);
	}
	
	
	@Override
	public void addLayoutComponent(String constraintsAsString, Component comp) {
		throw new UnsupportedOperationException("This operation is not supported. Use addLayoutComponent(Component comp, Object constraints) instead.");

	}
	
	@Override
	public void addLayoutComponent(Component comp, Object constraints) {
		// ovdje provjeri je li sve ispravno dodano jel (tipa da nije predana komponenta sa RCPosition (-3,18) itd)
		
		RCPosition pos;
		
		if (comp.equals(null)) throw new NullPointerException("Component cannot be null!");
		if (constraints.equals(null)) throw new NullPointerException("Constraints cannot be null!");
		
		if (constraints instanceof String) {
			pos = RCPosition.fromTextSpec((String)constraints);
		} else if (constraints instanceof RCPosition) {
			pos = (RCPosition) constraints;
		}else {
			throw new CalcLayoutException("Error - possible types for component constraints are String or RCPosition!");
		}
		
		if (pos.getRow() < 1 || pos.getRow() > 5 ||
		    pos.getColumn() < 1 || pos.getColumn() > 7 ||
			(pos.getRow() == 1 && (pos.getColumn() > 2 && pos.getColumn() < 6))){
					throw new CalcLayoutException("Invalid RCPosition for component " + comp.toString());
		}
		
		if (componentMap.get(pos) == null) { //ako u toj "kućici" trenutno nije druga komponenta
			componentMap.put(pos, comp);
		}else {
			throw new CalcLayoutException("Error while adding component " + comp.toString() + ", another component already occupies that position!");
		}

	}

	@Override
	public void removeLayoutComponent(Component comp) {
		componentMap.remove(comp);

	}

	@Override
	public Dimension preferredLayoutSize(Container parent) { //računa optimalni prostor koji mu je potreban za prikaz nekog containera															//piše ti u uputama kako implementirati ove metode
		return findDimension(new DimensionGetter() {

			@Override
			public Dimension getDimension(Component c) {
				return c.getPreferredSize();
			}
			
		});
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) { //piše u uputi kako implementirati
		
		return findDimension(new DimensionGetter() {

			@Override
			public Dimension getDimension(Component c) {
				return c.getMinimumSize();
			}
			
		});
	}
	
	@Override
	public Dimension maximumLayoutSize(Container target) { //znači mislim da ovdje samo ideš po svim komponentama koje su dodane u ovaj layout jel i računaš, ako bi 
		// TODO Auto-generated method stub				   //im svima dodao maksimalno mjesta koliko žele, koliko bi ti velik prozor mogao biit
		return findDimension(new DimensionGetter() {

			@Override
			public Dimension getDimension(Component c) {
				return c.getMaximumSize();
			}
			
		});								   //a sjeti se, svaka "kućica", osim prve, je jednako velika, pa je sve što trebaš naći je pronaći komponentu koja ima najveću širinu od svih (dobivenu sa komponenta.getMaxSize.width), te 
	}													   //te komponentu koja ima najveću visinu od svih, izračunati veličinu jedne kućice i visinu, te na osnovu veličine jedne kućice, i valjd gapa (i 

	
	//ove 3 metode su jako slične i cilj je ne ponavljati kod
	//možda funkcijska sučelja ili nešto slično
	
	
	
	//svaki put kada dodamo neku novu komponentu (koje pamtimo recimo
	//u nekoj internoj mapi ili nečemu), pozivamo ovu metodu
	//kako bi ponovno rasporedili sve komponente na odgovarajući 
	//način
	//također se zove prilikom resizanja prozora
	@Override
	public void layoutContainer(Container parent) {  //the main thing
		System.out.println("Pozivam ovu metodu");
		
		//provjeravamo ima li parent container bordere
		//ne zaboravi da ćeš onda i ukupno raspoloživo mjesto računati uzevši prvo u obzir bordere
		Insets ins = parent.getInsets(); 
		
		//dohvaćamo dimenzije prozora
		int w = parent.getWidth() - ins.left - ins.right - gap*(NO_OF_COLUMNS - 1); //ukupna raspoloživa širina
 		int h = parent.getHeight() - ins.top - ins.bottom - gap*(NO_OF_ROWS - 1); //ukupna raspoloživa visina
 		
 		int boxWidthActual = w/NO_OF_COLUMNS;
 		int boxHeightActual = h/NO_OF_ROWS;
 		
 		int boxWidth = Math.round(w/NO_OF_COLUMNS);
 		int boxHeight = Math.round(h/NO_OF_ROWS);
 		
 		
 		int[] boxWidths = new int[NO_OF_COLUMNS];
 		int[] boxHeights = new int[NO_OF_ROWS];
 		
 		int noOfSmallerWidths = boxWidth*NO_OF_COLUMNS  - w; //ovdje bi možda već trebalo gap uračunati tako da kasnije sve kućice mogu biti iste širine
 		int noOfLargerWidths = NO_OF_COLUMNS - noOfSmallerWidths;
 		int noOfSmallerHeights = boxHeight*NO_OF_ROWS - h;
 		int noOfLargerHeights = NO_OF_ROWS - noOfSmallerHeights;
 		
 		for (int i = 0; i < NO_OF_COLUMNS; i++) {
 			if (i%2 == 0 && noOfLargerWidths != 0) {
 				boxWidths[i] = boxWidth;
 				noOfLargerWidths -= 1;
 			}else if (i%2 == 0 && noOfLargerWidths == 0) {
 				boxWidths[i] = boxWidth - 1;
 				noOfSmallerWidths -= 1;
 			}
 			
 			if (i%2 != 0 && noOfSmallerWidths != 0) {
 				boxWidths[i] = boxWidth - 1;
 			}else if (i%2 != 0 && noOfSmallerWidths == 0) {
 				boxWidths[i] = boxWidth;
 				noOfLargerWidths -= 1;
 			}
 			
 		}
 		
 		for (int i = 0; i < NO_OF_ROWS; i++) {
 			if (i%2 == 0 && noOfLargerHeights != 0) {
 				boxHeights[i] = boxHeight;
 				noOfLargerHeights -= 1;
 			}else if (i%2 == 0 && noOfLargerHeights == 0) {
 				boxHeights[i] = boxHeight - 1;
 				noOfSmallerHeights -= 1;
 			}
 			
 			if (i%2 != 0 && noOfSmallerHeights != 0) {
 				boxHeights[i] = boxHeight - 1;
 			}else if (i%2 != 0 && noOfSmallerHeights == 0) {
 				boxHeights[i] = boxHeight;
 				noOfLargerHeights -= 1;
 			}
 			
 		}
 		
 		
 		//okej kako saznati koliko brojeva za 1 int nižih treba biti
 		//osnovni broj je Math.round(w/NO_OF_COLUMNS))
 		//recimo da je to 29, jer je širina ekrana 200 piksela
 		//29*7 = 203, a širina ekrana je 200 -> dakle kako dobijemo 200?
 		//jednostavno, tako da stavimo (203-200) kućica veličine 29 -1 (28), te (7-3) kućica veličine 29
 		
 		
 		
		
 		int x = ins.left;
		int y = ins.top;
		Component c;
		
		for (int i = 1; i <= NO_OF_ROWS; i++) {
			for (int j = 1; j <= NO_OF_COLUMNS; j++) {
				c = componentMap.get(new RCPosition(i, j));
				
				if (i == 1 && j == 1) { //poseban slucaj
					
					if (c != null) {
						c.setBounds(x, y, 5*boxWidths[j - 1] + 4*gap, boxHeights[i - 1]);
					}
					
					x += 5*boxWidth + 5*gap; //zbog toga što iksu dodajemo boxWidhts[j] koji je točno gap veći od c-ove širine, gap će biti automatski uključen
					
					j = 5; //ako nešto ne radi, ovo stavi na 6
					continue;
				}else {
					if (c != null) {
						c.setBounds(x,y,boxWidths[j-1], boxHeights[i-1]);
					}
					
					//x += boxWidths[j-1] + gap;
					x += boxWidth + gap;
					
					//sve se čini ok, samo ti se događa ono na što je upozoreno u zadaći - da nemaš kontinurirano pomicanje elemenata
					//kako širiš prozor, već kad polako širiš prozor, jedno vrijeme stoji, a onda skoči
				}
				
			}
			
			y += boxHeight + gap;
			x = ins.left;
		}

	}


	@Override
	public float getLayoutAlignmentX(Container target) {
		return 0;
	}

	@Override
	public float getLayoutAlignmentY(Container target) {
		return 0;
	}

	@Override
	public void invalidateLayout(Container target) {
		

	}

}
