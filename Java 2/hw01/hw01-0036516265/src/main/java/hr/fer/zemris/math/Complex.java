package hr.fer.zemris.math;

import java.util.ArrayList;
import java.util.List;


/**
 * Klasa koja modelira kompleksne brojeve. Brojevi su read-only
 * @author Dorian
 *
 */
public class Complex {
	
	private static double PRECISION = 0.0000001;
	
	private double re;
	private double im;
	
	public static final Complex ZERO = new Complex(0,0);

	public static final Complex ONE = new Complex(1,0);
	
	public static final Complex ONE_NEG = new Complex(-1,0);
	
	public static final Complex IM = new Complex(0,1);
	
	public static final Complex IM_NEG = new Complex(0,-1);
	
	public Complex() {
		this.re = 0;
		this.im = 0;
	}
	
	public Complex(double d, double e) {
		this.re = d;
		this.im = e;
	}
	
	/**
	 * Vraća modul kompleksnog broja
	 * @return
	 */
	public double module() {
		return Math.sqrt(Math.pow(this.re, 2) + Math.pow(this.im, 2));
	}
	
	/**
	 * Vraća rezultat množenja ovog kompleksnog broja s drugim, kao nov kompleksan broj
 	 * Ovaj broj ostaje neizmjenjen.
	 * @param c
	 * @return
	 */
	public Complex multiply(Complex c) {
		double resReal = (this.re*c.getRe() - this.im*c.getIm());
		double resImg = (this.re*c.getIm() + this.im*c.getRe()); //greška je ovdje u množenju :)
		
		return new Complex(resReal, resImg);
	}
	
	/**
	 * Vraća rezultat djeljenja ovog kompleksnog broja s drugim, kao nov kompleksan broj
	 * Ovaj broj ostaje neizmjenjen.
	 * @param c
	 * @return
	 */
	public Complex divide(Complex c) {
		if (c.getRe() == 0 && c.getIm() == 0) {
			throw new IllegalArgumentException("Cannot divide with 0!");
		}
		
		double resReal = ((this.re*c.getRe() + this.im*c.getIm()) / (Math.pow(c.getRe(),2) + Math.pow(c.getIm(), 2)));
		double resImg = ((this.im*c.getRe() - this.re*c.getIm()) / (Math.pow(c.getRe(),2) + Math.pow(c.getIm(), 2)));
		
		return new Complex(resReal, resImg);
	}
	
	/**
	 * Vraća rezultat zbrajanja ovog kompleksnog broja s drugim, kao nov kompleksan broj
	 * Ovaj broj ostaje neizmjenjen.
	 * @param c
	 * @return
	 */
	public Complex add(Complex c) {
		return new Complex(this.re + c.getRe(), this.im + c.getIm());
	}
	
	/**
	 * Vraća rezultat oduzimanja ovog kompleksnog broja s drugim, kao nov kompleksan broj.
	 * Ovaj broj ostaje neizmjenjen.
	 * @param c
	 * @return
	 */
	public Complex sub(Complex c) {
		return new Complex(this.re - c.getRe(), this.im - c.getIm());
	}
	
	/**
	 * Vraća -this, kao nov kompleksan broj.
	 * Ovaj broj ostaje neizmjenjen
	 * @return
	 */
	public Complex negate() {
		return new Complex(this.re * -1, this.im * -1);
	}
	
	/**
	 * Vraća rezultat potenciranja ovog broja na n-tu potenciju, kao nov komleksan broj
	 * Ovaj broj ostaje neizmjenjen
	 * @param n
	 * @return
	 */
	public Complex power(int n) {
		//DeMoivre's formula -> more precise, works for n = 0
		double resReal = Math.pow(this.module(),n)*Math.cos(n*this.getAngle());
		double resImg = Math.pow(this.module(),n)*Math.sin(n*this.getAngle());
		
		return new Complex(resReal, resImg);
	}
	
	/**
	 * Vraća n-ti korijen ovog broja
	 * Ne mijenja ovaj broj
	 * @param n > 0
	 * @return
	 */
	public List<Complex> root(int n){
		
		List<Complex> resArray = new ArrayList<>();
		double resReal = 0;
		double resImg = 0;
		double root = nthRoot(this.module(),n);
		//double root = Math.sqrt(5.485156774);
		//System.out.println("Magnitude is " + this.getMagnitude());
		//System.out.println("Root is " + root);
		
		for (int k = 0; k < n; k++) {
			resReal = root*Math.cos((this.getAngle()+2*k*Math.PI)/n);
			resImg = root*Math.sin((this.getAngle()+2*k*Math.PI)/n);
			resArray.add(new Complex(resReal, resImg));
		}
		
		return  resArray;
	}
	
	/**
	 * Vraća kut kompleksnog broja
	 * @return
	 */
	double getAngle() {
		if (this.re == 0) {
			return Math.PI/2;
		} 
		double angle = 0;
		angle = Math.atan(this.im / this.re);
		if (angle > Math.PI * 2) {
			angle = angle/Math.PI;
		}
		if (this.re >= 0 && this.im >= 0) {
			return angle;
		} else if (this.re < 0 && this.im > 0) {
			return Math.PI+angle;
		} else if (this.re > 0 && this.im < 0) {
			return Math.PI*2 + angle;
		} else {
			return Math.PI + angle;
		}
	}
	
	/**
	 * Calculates the n-th root of the given real number
	 * @param num
	 */
	private double nthRoot(double num,int n) {

		double nth = Math.pow(num, 1/(float)n);
		if (Math.abs(Math.round(nth) - nth) < PRECISION) {
			nth = Math.round(nth);
		}
		
		return nth;
	}
	
	/**
	 * Klasa za parsiranje, korištena u 2. zadatku zadaće.
	 * Parsira kompleksni broj napisan kao string. Dozvoljeni primjeri su 2+3i, i, -i itd. Dupli znakovi (npr. ++2--3i) nisu dozvoljeni
	 * i mora doći prije broja (npr. i3, a ne 3i)
	 * @param 
	 * @return
	 */
	public static Complex parse(String s) {
		double reTmp = 0;
		double imTmp = 0;
		boolean signFlag = false;
		boolean imFlag = false;
		char sign = '+';
		String number = "";

		if (s.equals("i") || s.equals("+i")) {
			return new Complex(0,1);
		} else if (s.equals("-i")) {
			return new Complex(0,-1);
		}

		for (char c : s.toCharArray()) {
			if (c == ' ') {
				continue;
			}
			if (c == '-' || c == '+') {
				if (signFlag == false) {
					signFlag = true;
					//System.out.println("Setting flag to true");
				} else {
					throw new IllegalArgumentException("Greška! Dvostruki predznak!");
				}
				if (number != "") {
					if (sign == '-') {
						if (imFlag) {
							imTmp = Double.parseDouble(sign+number);
							imFlag = false;
						} else {
							reTmp = Double.parseDouble(sign + number);
						}
					} else {
						if (imFlag) {
							imTmp = Double.parseDouble(sign + number);
							imFlag = false;
						}else {
							reTmp = Double.parseDouble(sign + number);
						}
					}
					number = "";
				}
				sign = c;
			} else if (c == 'i') {
				if (number != "") { //i comes before a number
					throw new IllegalArgumentException("i mora doći prije broja!!");
				}
				signFlag = false;
				imFlag=true;
				//System.out.println("Settng flag to false");
//				if (sign == '-') {
//					imTmp = Double.parseDouble(sign + number);
//				}else {
//					imTmp = Double.parseDouble(number);
//				}
//				number = "";
			} else if ((c >= 48 && c <= 57) || c == '.') { //number
				signFlag = false;
				//System.out.println("Setting flag to false");
				number += c;
			} else {
				throw new IllegalArgumentException("Greška prilikom parsiranja! Nedozvoljeni znak dan na ulazu");
			}
		}


		if (reTmp == 0 && imTmp == 0 && !imFlag) {
			if (sign == '-') {
				reTmp = Double.parseDouble(sign + number);
			} else {
				reTmp = Double.parseDouble(number);
			}
		} else if (reTmp == 0 && imTmp == 0 && imFlag) {
			if (sign == '-') {
				imTmp = Double.parseDouble(sign + number);
			} else {
				imTmp = Double.parseDouble(number);
			}
		}

		return new Complex(reTmp, imTmp);
	}
	
	
	@Override
	public String toString() {
		String res = "(";
		res += this.re;
		if (this.im < 0) {
			res += "-" + Math.abs(this.im) + "i";
		} else {
			res += "+" + this.im + "i";
		}
		return res + ")";
	}

	public double getRe() {
		return re;
	}

	public double getIm() {
		return im;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(im);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(re);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Complex))
			return false;
		Complex other = (Complex) obj;
		if (Double.doubleToLongBits(im) != Double.doubleToLongBits(other.im))
			return false;
		if (Double.doubleToLongBits(re) != Double.doubleToLongBits(other.re))
			return false;
		return true;
	}
	
	
	


	
	
}

