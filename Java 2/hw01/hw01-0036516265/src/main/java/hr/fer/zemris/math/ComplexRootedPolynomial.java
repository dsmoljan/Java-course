package hr.fer.zemris.math;

/**
 * Razred modelira polinom koji kao argumente ima kompleksne brojeve, prema predlošku f(z) = z0*(z-z1)*(z-z2)...*(z-zn)
 * z1...zn su njegove nultočke, a z0 konstanta
 * @author Dorian
 *
 */
public class ComplexRootedPolynomial {
	
	private Complex constant = null;
	private Complex[] roots = null;
	
	
	/**
	 * Konstruktor prima nultočke polinoma - krenuvši od konstante (constant) do svih ostalih nultočaka u obliku polja.
	 * @param constant
	 * @param roots
	 */
	public ComplexRootedPolynomial(Complex constant, Complex ... roots) {
		this.constant = constant;
		this.roots = roots;
	}
	
	/**
	 * Prima neki kompleksni broj z i računa vrijednost koju polinom ima u toj točci
	 * Dakle samo uvrstimo broj z u formulu f(z) = z0*(z-z1)*(z-z2)...*(z-zn)
	 * @param z
	 * @return
	 */
	public Complex apply(Complex z) {
		
		Complex result = this.constant;
		
		for (int i = 0; i < roots.length; i++) {
			result = result.multiply(z.sub(roots[i]));
		}
		
		return result;
	}
	
	/**
	 * Pretvara polinom u način prikaza ComplexPolynomial
	 * @return
	 */
	public ComplexPolynomial toComplexPolynom() { //postupak najvjerojatnije sličan mutliplyu kod ComplexPolynomial
		Complex[] factors = new Complex[roots.length];
		ComplexPolynomial[] tmpList = new ComplexPolynomial[roots.length + 1];
		
		//stupanj je onoliki koliko korijena ima, npr. stupanj (x-1)(x-2) je 2
		//ono što piše uz x^0 je konstanta*svaki korijen
		//konstanta sve množi
		//zadnji koeficijent je samo konstanta (jer recimo 3*(x-1)(x-2)(x-3)(x-4), uz x^4 može jedino stajati 3
		
		//mogao bi i svaki dio (recimo (x-2)) prvo pretvoriti u ComplexPolynom, onda izmnožiti sve te ComplexPolynom koristeći metodu multiply od tamo hmmmmm
		
		ComplexPolynomial pomocni = new ComplexPolynomial(constant); //sad je na 1. mjestu polinom stupnja 0
		//na ostalim mjestima će biti polinomi stupnja 1
		
		
		
		for (int i = 0; i < roots.length; i++) {
			Complex[] tmpFactors = {roots[i].negate(),Complex.ONE};			
			pomocni = pomocni.multiply(new ComplexPolynomial(tmpFactors));
		}
		
		// i sad samo prođemo po toj listi i pomnožimo sve u njoj!
		
		return pomocni;
	}
	
	@Override
	public String toString() {
		
		String res = "";
		res += constant.toString() + "*";
		
		for (int i = 0; i < roots.length; i++) {
			res += "(z-" + roots[i].toString() + ")*";
		}
		
		return res.substring(0,res.length() - 1);
	}
	
	/**
	 * Pronalazi indeks najbližeg korijena za dani kompleksni broj z koji je unutar tresholda
	 * Ako nema takvog korijena, vraća -1
	 * Prvi korijen ima indeks 0, drugi indeks 1 itd.
	 * @param z
	 * @param treshold
	 * @return
	 */
	public int indexOfClosestRootFor(Complex z, double treshold) {
		
 		int index = -1;
		
		Complex currentRoot = null;
		double minDistance = Double.MAX_VALUE;
		
		//ovdje mora vrijediti i da je nultočka najbliža z hmmmmm možda to nemaš
		
		for (int i = 0; i < roots.length; i++) {
			
			double distance = Math.sqrt(Math.pow(z.getRe()-roots[i].getRe(),2) + Math.pow(z.getIm() - roots[i].getIm(), 2));
			
			
			if (distance < treshold && distance <= minDistance){
				minDistance = distance;
				index = i;
			}
		}
		
		
		return index;
		
	}

}
