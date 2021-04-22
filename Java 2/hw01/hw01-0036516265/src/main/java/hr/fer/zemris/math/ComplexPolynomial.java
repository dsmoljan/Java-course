package hr.fer.zemris.math;

/**
 * Modelira polinom nad kompleksnim brojevima prema predlošku f(z) = zn*z^n+zn-1*z^n-1...+z2*z^2+z1*z+z0
 * z0 do zn su koeficijenti koji pišu uz odgovarajuće potencije z
 * Svi koeficijenti zadaju se kao kompleksni brojevi
 * @author Dorian
 *
 */
public class ComplexPolynomial {
	
	Complex[] factors = null;
	
	
	/**
	 * Konstruktor, prima listu kompleksnih brojeva - koeficijenata polinoma
	 * Lista je redom - z0,z1,...zn
	 * @param factors
	 */
	public ComplexPolynomial(Complex ... factors) {
		this.factors = factors;
	}
	
	/**
	 * Vraća stupanj polinoma
	 * @return
	 */
	public short order() {
		return (short)(factors.length - 1);
	}
	
	/**
	 * Vraća novi polinom, rezultat množenja trenutnog polinoma sa drugim polinomom
	 * @param p
	 * @return
	 */
	public ComplexPolynomial multiply(ComplexPolynomial p) {
		
		int newOrder = this.order() + p.order();
		
		Complex[] newFactors = new Complex[newOrder + 1];
		
		for (int i = 0; i < newOrder + 1; i++) {
			newFactors[i] = Complex.ZERO;
		}
		
		for (int i = 0; i < factors.length; i++) {
			for (int j = 0; j < p.factors.length; j++) { 
				newFactors[i + j] = newFactors[i + j].add(factors[i].multiply(p.factors[j]));
			}
		}
		return new ComplexPolynomial(newFactors);
	}
	
	/**
	 * Računa prvu derivaciju polinoma
	 * @return
	 */
	public ComplexPolynomial derive() { //dakle derivacija - uvijek stupanj manja, cilj je jedino izračunati nove faktore jer oni idu u konstruktor
		
		if (factors.length == 0) {
			return new ComplexPolynomial(new Complex[0]);
		}
		
		Complex[] newFactors = new Complex[factors.length - 1];
		
		//idemo od 1 jer je derivacija konstante, koja je prva u polju, = 0
		for (int i = 1; i < factors.length; i++) {
			newFactors[i - 1] = factors[i].multiply(new Complex(i,0));
		}
		
		return new ComplexPolynomial(newFactors);
	}
	
	
	/**
	 * Računa vrijednost polinoma u točci z
	 * @param z
	 * @return
	 */
	public Complex apply(Complex z) {
		
		Complex result = Complex.ZERO;
		
		for (int i = 0; i < factors.length; i++) {
			result = result.add(factors[i].multiply(z.power(i)));
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		String res = "";
		
		for (int i = factors.length - 1; i >=0; i--) {
			if (i != 0) {
				res += factors[i].toString() + "*" + "z^" + i + "+";
			}else {
				res += factors[i].toString();

			}
		}
		
		return res.substring(0, res.length());
	}
	
	

}
