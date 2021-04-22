package hr.fer.zemris.java.fractals;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import hr.fer.zemris.java.fractals.viewer.FractalViewer;
import hr.fer.zemris.java.fractals.viewer.IFractalProducer;
import hr.fer.zemris.java.fractals.viewer.IFractalResultObserver;
import hr.fer.zemris.math.Complex;
import hr.fer.zemris.math.ComplexPolynomial;
import hr.fer.zemris.math.ComplexRootedPolynomial;

/**
 * Sadrži sekvencijalnu (ne-paralelnu) implementaciju Newtonovog fraktala
 * @author Dorian
 *
 */
public class Newton {
	
	public static double convergenceTreshold = 0.001;
	public static double rootTreshold = 0.002;

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		List<Complex> tmpArray = new ArrayList<>();
		
		int noOfRoots = 1;
		
		String input = "";
		
		System.out.println("Welcome to Newton-Raphson iteration-based fractal viewer.");
		System.out.println("Please enter at least two roots, one root per line. Enter 'done' when done.");
		
		while (!input.equals("done")) {
			System.out.print("Root " + noOfRoots + " >");
			input = sc.nextLine();
			
			if (input.equals("done")) {
				continue;
			}
			
			try {
				tmpArray.add(Complex.parse(input));
			} catch (Exception e) {
				System.out.println("Error while parsing root! Please enter the root again");
				continue;
			}
			
			noOfRoots += 1;
		}
		
		
		System.out.print("Image of the fractal will appear shortly");
		Complex[] inputRoots = new Complex[tmpArray.size()];
		
		for (int i = 0; i < tmpArray.size(); i++) {
			inputRoots[i] = tmpArray.get(i);
		}
		
		System.out.println("Uneseni brojevi su:");
		
		for (Complex c : inputRoots) {
			System.out.println(c);
		}
		
		//ovi korijeni nam određuju funkciju pomoću koje računamo fraktale
		
		FractalViewer.show(new MojProducer(new ComplexRootedPolynomial(Complex.ONE, inputRoots)));	
		
		//imaš grešku. zadnji broj ti ne unosi dobro
		

	}
	
	public static class MojProducer implements IFractalProducer {
		
		ComplexRootedPolynomial polinom = null;
		ComplexPolynomial derived = null;
		
		public MojProducer(ComplexRootedPolynomial polinom) {
			this.polinom = polinom;
			this.derived = polinom.toComplexPolynom().derive();
		}
		
		@Override
		public void produce(double reMin, double reMax, double imMin, double imMax,
				int width, int height, long requestNo, IFractalResultObserver observer, AtomicBoolean cancel) {
			System.out.println("Zapocinjem izracun...");
			int m = 16*16*16; //max iter
			int offset = 0;
			short[] data = new short[width * height]; //tu stavljamo indekse u kojima je neka kompleksna točka c divergirala ili ne
			for(int y = 0; y < height; y++) {
				if(cancel.get()) break;
				for(int x = 0; x < width; x++) {
					double cre = x / (width-1.0) * (reMax - reMin) + reMin; //ove dvije linije odgovaraju map_to_complex_plain
					double cim = (height-1.0-y) / (height-1) * (imMax - imMin) + imMin;
					Complex zn = new Complex(cre, cim);
					double zre = 0;
					double zim = 0;
					double module = 0;
					int iters = 0;
					do {
						//ovo je osnovni dio koda
						//vidi što ti je convergenceTreshold -> mislim da je to module < 4.0 koji već piše u while dijelu petlje
						Complex numerator = this.polinom.apply(zn);
						Complex denominator = derived.apply(zn); //e al derived uvijek ostaje isti hm, mislim da je greška u apply
						Complex znold = zn;
						Complex fraction = numerator.divide(denominator);
						zn = zn.sub(fraction);
						module = znold.sub(zn).module();
//						double zn1re = zre*zre - zim*zim + cre;
//						double zn1im = 2*zre*zim +cim;
//						module = zn1re*zn1re + zn1im*zn1im;
//						zre = zn1re;
//						zim = zn1im;
						iters++;
					} while(iters < m && Math.abs(module) > convergenceTreshold); //promijeni ovo u odgovarajući treshold
					data[offset] = (short) (polinom.indexOfClosestRootFor(zn, rootTreshold) + 1);
					//ista je boja, dakle svugdje vraća isto
					//dodaj data[offset++] = idnex+1
					//cini se da stalno vraća -1
					offset++;
				}
			}
			System.out.println("Racunanje gotovo. Idem obavijestiti promatraca tj. GUI!");
			observer.acceptResult(data, (short)(polinom.toComplexPolynom().order() + 1), requestNo);
		}
	}
	

}
