package hr.fer.zemris.java.fractals;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;

import hr.fer.zemris.java.fractals.NewtonP1.MojProducer;
import hr.fer.zemris.java.fractals.NewtonP1.PosaoIzracuna;
import hr.fer.zemris.java.fractals.viewer.FractalViewer;
import hr.fer.zemris.java.fractals.viewer.IFractalProducer;
import hr.fer.zemris.java.fractals.viewer.IFractalResultObserver;
import hr.fer.zemris.math.Complex;
import hr.fer.zemris.math.ComplexPolynomial;
import hr.fer.zemris.math.ComplexRootedPolynomial;

/**
 * Implementacija crtanja fraktala, koja za paralelizaciju koristi ForkJoinPool. Kako ovaj način implementacije paralelizacije
 * zahtijeva rekurzivnu podijelu poslova, podjela izračuna na "trake" je obavljena na nešto drugačiji način nego u ostalim implementacijama.
 * Dovrši...
 * @author Dorian
 *
 */
public class NewtonP2 {
	
	public static double convergenceTreshold = 0.001;
	public static double rootTreshold = 0.002;
	
	public static void main(String[] args) {
		
		System.out.println("Pokrećem kod");
		
		//default vrijednosti
		int mintracks = 16; //default vrijednost

		
		//dakle ulaz može biti npr. --workers=10 ili -w 10
		
		String argName = "";
		String argValue = "";
		
	
		//parsiranje ARGUMENATA (ne korisničkog inputa preko tipkovnice)
		for (int i = 0; i < args.length; i++) {
			if (args[i].substring(0,1).equals("-")) {
				if (args[i].substring(0,2).equals("--")) { //radi se o obliku --workers=10 ili --tracks=5
					argName = args[i].split("=")[0];
					argValue = args[i].split("=")[1];
					
					if (argName.equals("--mintracks")) {
						try {
							mintracks = Integer.parseInt(argValue);
						}catch(NumberFormatException e) {
							throw new IllegalArgumentException("Error while parsing arguments - cannot parse int! Please check your syntax!");
						}
					}else {
						throw new IllegalArgumentException("Error while parsing arguments - unsupported argument name! Please check your syntax!");
					}
				}else { //radi se o obliku -w 5 ili -t 10
					argName = args[i];
				}
			}else { //ako se radi o obliku -w 5 i naiđemo na 5
				if (argName.equals("")) {
					throw new IllegalArgumentException("Error while parsing arguments - argument name missing! Please check your syntax!");
				}
				
				if (argName.equals("-m")) {
					try {
						argValue = args[i];
						mintracks = Integer.parseInt(argValue);
					}catch(NumberFormatException e) {
						throw new IllegalArgumentException("Error while parsing arguments! Please check your syntax!");
					}
				}else {
					throw new IllegalArgumentException("Error while parsing arguments - unsupported argument name! Please check your syntax!");
				}
				
			}
		}
		
		System.out.println("Mintracks value is");
		System.out.println(mintracks);

		
		//prvo parsiramo arugmente
		
		
		
		List<Complex> tmpArray = new ArrayList<>();
		
		int noOfRoots = 1;
		
		String input = "";
		
		System.out.println("Welcome to Newton-Raphson iteration-based fractal viewer, using paralellization!.");
		System.out.println("Please enter at least two roots, one root per line. Enter 'done' when done.");
		
		try(Scanner sc = new Scanner(System.in)) {
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
		}catch(Exception e) {
			System.exit(1);
		}

		
		
		System.out.print("Image of the fractal will appear shortly");
		
		//ovo ispod treba izmijeniti -> a možda i ne, možda treba samo izmijeniti implementaciju MyProducera
		Complex[] inputRoots = new Complex[tmpArray.size()];
		
		for (int i = 0; i < tmpArray.size(); i++) {
			inputRoots[i] = tmpArray.get(i);
		}
		
		System.out.println("Uneseni brojevi su:");
		
		for (Complex c : inputRoots) {
			System.out.println(c);
		}
		
		//ovi korijeni nam određuju funkciju pomoću koje računamo fraktale
		
		FractalViewer.show(new MojProducer(new ComplexRootedPolynomial(Complex.ONE, inputRoots), mintracks));
		
	}
	
	public static class PosaoIzracuna extends RecursiveAction{
		double reMin;
		double reMax;
		double imMin;
		double imMax;
		int width;
		int height;
		int yMin;
		int yMax;
		int m;
		short[] data;
		int mintracks;
		AtomicBoolean cancel;
		ComplexRootedPolynomial polinom;
		ComplexPolynomial derived;
		
		//defaultni konstruktor, najvjerojatnije nebitan
		private PosaoIzracuna() {
			
		}

		public PosaoIzracuna(double reMin, double reMax, double imMin,
				double imMax, int width, int height, int yMin, int yMax, 
				int m, short[] data, AtomicBoolean cancel, ComplexRootedPolynomial polinom, ComplexPolynomial derived, int mintracks) {
			super();
			this.reMin = reMin;
			this.reMax = reMax;
			this.imMin = imMin;
			this.imMax = imMax;
			this.width = width;
			this.height = height;
			this.yMin = yMin;
			this.yMax = yMax;
			this.m = m;
			this.data = data;
			this.cancel = cancel;
			this.polinom = polinom;
			this.derived = derived;
			this.mintracks = mintracks;
			
		}

		@Override
		protected void compute() {
			
			//na početku dobijem yMin i yMax koji ustvari odgovaraju visini cijele slike
			//prvo bi mogao napraviti dva posla -> svakom podijelim pola
			//onda opet podijelim itd., sve dok razlika yMin i yMax nije manja od mintracks?
			//recimo da je cijela slika jedna cijela traka
			//podijelom na 2 dijela obrade dobijemo 2 trake
			//daljnjom podijelom dobijemo 4 trake itd.
			//to radimo dok ne dođemo do mintracks, onda računamo direktno
			
			if ((height/mintracks) > (yMax - yMin)) {
				computeDirect();
				return;
			}
			
			int diff = yMax - yMin;
			int yMin1 = yMin;
			int yMax1 = yMin + diff/2;
			int yMin2 = yMin + diff/2 + 1;
			int yMax2 = yMax;
			
			PosaoIzracuna p1 = new PosaoIzracuna(reMin, reMax, imMin, imMax, width, height, yMin1, yMax1, m, data, cancel, polinom, derived, mintracks);
			PosaoIzracuna p2 = new PosaoIzracuna(reMin, reMax, imMin, imMax, width, height, yMin2, yMax2, m, data, cancel, polinom, derived, mintracks);

			invokeAll(p1,p2);
		}
		
		private void computeDirect() {
			
			//ovaj dio koda sad izvršavaš kad broj traka bude manji od mintracks
			
			int offset = this.yMin * this.width;
			int m = 16*16*16; //max iter
			
			for (int y = this.yMin; y <= this.yMax; y++) {
				if (cancel.get()) break;
				for (int x = 0; x < this.width; x++) {
					double cre = x / (width-1.0) * (reMax - reMin) + reMin; //ove dvije linije odgovaraju map_to_complex_plain
					double cim = (height-1.0-y) / (height-1) * (imMax - imMin) + imMin;
					Complex zn = new Complex(cre, cim);
					double module = 0;
					int iters = 0;
					do {
						//ovo je osnovni dio koda
						//vidi što ti je convergenceTreshold -> mislim da je to module < 4.0 koji već piše u while dijelu petlje
						Complex numerator = this.polinom.apply(zn);
						Complex denominator = this.derived.apply(zn); //e al derived uvijek ostaje isti hm, mislim da je greška u apply
						Complex znold = zn;
						Complex fraction = numerator.divide(denominator);
						zn = zn.sub(fraction);
						module = znold.sub(zn).module();
						iters++;
					} while(iters < m && Math.abs(module) > convergenceTreshold); //promijeni ovo u odgovarajući treshold
					data[offset] = (short) (polinom.indexOfClosestRootFor(zn, rootTreshold) + 1); //tu baca grešku "index 250000 out of bounds for length 250000"
					offset++;
					
				}
			}
			//Mandelbrot.calculate(reMin, reMax, imMin, imMax, width, height, m, yMin, yMax, data, cancel);
			
		}
		
	}
	
	//realno za prvi zadatak treba samo još ovo modificirati i gotov sam
	public static class MojProducer implements IFractalProducer{
		
		ComplexRootedPolynomial polinom = null;
		ComplexPolynomial derived = null;
		int mintracks;
		ForkJoinPool pool;
		ArrayList<PosaoIzracuna> jobList;
		ArrayList<Future<?>> rezultati; //ovo ? mislim da znali da Futuri tj. poslovi čije informacije oni pohranjuju ništa ne vraćaju
		
		
		public MojProducer(ComplexRootedPolynomial polinom, int mintracks) {
			this.polinom = polinom;
			this.derived = polinom.toComplexPolynom().derive();
			this.mintracks = mintracks;

		}
		

		
		/**
		 * Jednom kada objekt za generiranje fraktala više nije potreban (primjerice, kad se zatvara prozor), nad njim će se pozvati
			ova metoda, čija je zadaća osloboditi eventualno zauzete resurse. Objektu se garantira da se metoda
			close neće pozvati tako dugo dok se izvodi barem jedan poziv metode produce.
		 */
		@Override
		public void close() {
			pool.shutdown();
		}

		/**
		 * Poziva se svaki put kada treba izgenerirati novu sliku fraktala. Neće se pozvati prije nego završi metoda setup.
		 */
		@Override
		public void produce(double reMin, double reMax, double imMin, double imMax, int width, int height, long requestNo,
				IFractalResultObserver observer, AtomicBoolean cancel) {
			System.out.println("Zapocinjem izracun...");
			//System.out.println("Na raspolaganju je " + noOfTracks + ". Obrađujem ih koristeći " + noOfWorkers + " dretvi");
			
			int m = 16*16*16;
			short[] data = new short[width * height]; //mislim da je ok da ovo bude ovdje, a ne u metodi setup jer su potrebni width i height, koje saznajemo tek u ovoj metodi

			
			int yMin = 0;
			int yMax = height - 1;
			PosaoIzracuna posao = new PosaoIzracuna(reMin, reMax, imMin, imMax, width, height, yMin, yMax, m, data, cancel, polinom, derived, mintracks);
			
			pool.invoke(posao);
			
			System.out.println("Racunanje gotovo. Idem obavijestiti promatraca tj. GUI!");
			observer.acceptResult(data, (short)(polinom.toComplexPolynom().order() + 1), requestNo);
			
		}

		
		/**
		 * Priprema sve potrebne resurse koji će se koristiti pri generiranju fraktala. Garantirano se poziva prije svih ostalih metoda ovog razreda.
		 */
		@Override
		public void setup() {
			pool = new ForkJoinPool();
		}
		
	}

}
