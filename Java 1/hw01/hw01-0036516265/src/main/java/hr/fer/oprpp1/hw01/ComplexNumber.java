package hr.fer.oprpp1.hw01;

/**
 * Class which models complex numbers.
 * @author dorian
 *
 */
public class ComplexNumber {
	
	private static double PRECISION = 0.0000001;
	private double real;
	private double img;

	public ComplexNumber(double real, double img) {
		this.real = real;
		this.img = img;
	}

	/**
	 * Creates a new complex number from a real number
	 * @param real
	 * @return new complex number with real part equal to real and imaginary part equal to 0
	 */
	public static ComplexNumber fromReal(double real) {
		return new ComplexNumber(real,0);
	}

	/**
	 * Creates a new complex number from a imaginary number
	 * @param img
	 * @return new complex number with real part equal to 0 and imaginary part equal to img
	 */
	public static ComplexNumber fromImaginary(double img) {
		return new ComplexNumber(0,img);
	}

	/**
	 * Creates a new complex number from a magnitude(r) and angle (fi)
	 * @param magnitude
	 * @param angle
	 * @return
	 */
	public static ComplexNumber fromMagnitudeAndAngle(double magnitude, double angle) { //hardkodiraj one vrijednosti za koje ovo nece raditi
		double tmpReal = magnitude*Math.cos(angle);
		double tmpImg = magnitude*Math.sin(angle);
		
		if (Math.abs(Math.round(tmpReal) - tmpReal) < PRECISION) {
			tmpReal = Math.round(tmpReal);
		}
		
		if (Math.abs(Math.round(tmpImg) - tmpImg) < PRECISION) {
			tmpImg = Math.round(tmpImg);
		}
		return new ComplexNumber(tmpReal, tmpImg);
	}

	/**
	 * Parses a complex number written as string. Allowed examples are 2+3i, 2, i, -i etd. Double signs (eg. ++2--3i) are not allowed. i has to come after the number (eg. 3i, not i3)
	 * @param s string we want to parse as a complex number
	 * @return new complex number parsed from string
	 */
	public static ComplexNumber parse(String s) {
		double reTmp = 0;
		double imTmp = 0;
		boolean signFlag = false;
		char sign = '+';
		String number = "";

		if (s == "i") {
			return new ComplexNumber(0,1);
		} else if (s == "-i") {
			return new ComplexNumber(0,-1);
		}

		for (char c : s.toCharArray()) {
			if (c == '-' || c == '+') {
				if (signFlag == false) {
					signFlag = true;
					//System.out.println("Setting flag to true");
				} else {
					throw new IllegalArgumentException("Error! Double signs!");
				}
				if (number != "") { //if we come across another sign and not i, the number currently in var number is the real part
					if (sign == '-') {
						reTmp = Double.parseDouble(sign + number);
					} else {
						reTmp = Double.parseDouble(number);
					}
					number = "";
				}
				sign = c;
			} else if (c == 'i') {
				if (number == "") { //i comes before a number
					throw new IllegalArgumentException("i must come after the number!");
				}
				signFlag = false;
				//System.out.println("Settng flag to false");
				if (sign == '-') {
					imTmp = Double.parseDouble(sign + number);
				}else {
					imTmp = Double.parseDouble(number);
				}
				number = "";
			} else if ((c >= 48 && c <= 57) || c == '.') { //number
				signFlag = false;
				//System.out.println("Setting flag to false");
				number += c;
			} else {
				throw new IllegalArgumentException("Unsupported character given as input!");
			}
		}


		if (reTmp == 0 && imTmp == 0 && !number.contains("i")) {
			if (sign == '-') {
				reTmp = Double.parseDouble(sign + number);
			} else {
				reTmp = Double.parseDouble(number);
			}
		} else if (reTmp == 0 && imTmp == 0 && number.contains("i")) {
			if (sign == '-') {
				imTmp = Double.parseDouble(sign + number);
			} else {
				imTmp = Double.parseDouble(number);
			}
		}

		return new ComplexNumber(reTmp, imTmp);
	}

	@Override
	public String toString() {
		String res = "";
		res += this.real;
		if (this.img < 0) {
			res += " - " + Math.abs(this.img) + "i";
		} else {
			res += " + " + this.img + "i";
		}
		return res;
	}
	
	/**
	 * Returns the real part of the complex number
	 * @return
	 */
	public double getReal() {
		return this.real;
	}
	
	/**
	 * Returns the imaginary part of the complex number
	 * @return
	 */
	public double getImaginary() {
		return this.img;
	}
	
	
	/**
	 * Returns the magnitude of the complex number
	 * @return
	 */
	public double getMagnitude() {
		return Math.sqrt(Math.pow(this.real, 2) + Math.pow(this.img, 2));
	}
	
	/**
	 * Returns the angle of the complex number.
	 * @return
	 */
	public double getAngle() {
		if (this.real == 0) {
			return Math.PI/2;
		} 
		double angle = 0;
		angle = Math.atan(this.img / this.real);
		if (angle > Math.PI * 2) {
			angle = angle/Math.PI;
		}
		if (this.real >= 0 && this.img >= 0) {
			return angle;
		} else if (this.real < 0 && this.img > 0) {
			return Math.PI+angle;
		} else if (this.real > 0 && this.img < 0) {
			return Math.PI*2 + angle;
		} else {
			return Math.PI + angle;
		}
		/*if (angle < 0) {
			angle = Math.PI + angle;
		}*/
	}
	/**
	 * Add a complex number
	 * @param c
	 * @return new complex number, the result of the addition
	 */
	public ComplexNumber add(ComplexNumber c) {
		return new ComplexNumber(this.real + c.getReal(), this.img + c.getImaginary());
	}
	
	/**
	 * Subtract a complex number
	 * @param c
	 * @return new complex number, the result of the subtraction
	 */
	public ComplexNumber sub(ComplexNumber c) {
		return new ComplexNumber(this.real - c.getReal(), this.img - c.getImaginary());
	}
	
	public ComplexNumber mul(ComplexNumber c) {
		double resReal = (this.real*c.getReal() - this.img*c.getImaginary());
		double resImg = (this.real*c.getImaginary() + this.img*c.getReal());
		
		return new ComplexNumber(resReal, resImg);
	}
	
	public ComplexNumber div(ComplexNumber c) {
		if (c.getReal() == 0 && c.getImaginary() == 0) {
			throw new IllegalArgumentException("Cannot divide with 0!");
		}
		
		double resReal = ((this.real*c.getReal() + this.img*c.getImaginary()) / (Math.pow(c.getReal(),2) + Math.pow(c.getImaginary(), 2)));
		double resImg = ((this.img*c.getReal() - this.real*c.getImaginary()) / (Math.pow(c.getReal(),2) + Math.pow(c.getImaginary(), 2)));
		
		return new ComplexNumber(resReal, resImg);	
	}
	
	/**
	 * Raises the complex number to the n-th power using DeMoivre's formula. Also works correctly for n = 0
	 * @param n
	 * @return a new complex number
	 */
	public ComplexNumber power(int n) {
		//regular way, probaly doesn't work correctly for n = 0
		/*ComplexNumber res = this;
		
		for (int i = 0; i < n - 1; i++) {
			res = res.mul(this);
		}
		return res;*/
		
		//DeMoivre's formula -> more precise, works for n = 0
		ComplexNumber res = this;
		double resReal = Math.pow(this.getMagnitude(),n)*Math.cos(n*this.getAngle());
		double resImg = Math.pow(this.getMagnitude(),n)*Math.sin(n*this.getAngle());
		
		return new ComplexNumber(resReal, resImg);
	}
	
	/**
	 * Calculates all n-roots of a complex number and returns them in an array
	 * @param n degree of the root
	 * @return an array of complex numbers, the roots of the given complex number
	 */
	public ComplexNumber[] root(int n) {
		ComplexNumber[] resArray = new ComplexNumber[n];
		double resReal = 0;
		double resImg = 0;
		double root = nthRoot(this.getMagnitude(),n);
		//double root = Math.sqrt(5.485156774);
		//System.out.println("Magnitude is " + this.getMagnitude());
		//System.out.println("Root is " + root);
		
		for (int k = 0; k < n; k++) {
			resReal = root*Math.cos((this.getAngle()+2*k*Math.PI)/n);
			resImg = root*Math.sin((this.getAngle()+2*k*Math.PI)/n);
			resArray[k] = new ComplexNumber(resReal, resImg);
		}
		
		return  resArray;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(img);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(real);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ComplexNumber))
			return false;
		ComplexNumber other = (ComplexNumber) obj;
		if (Double.doubleToLongBits(img) != Double.doubleToLongBits(other.img))
			return false;
		if (Double.doubleToLongBits(real) != Double.doubleToLongBits(other.real))
			return false;
		return true;
	}
	
	
}
