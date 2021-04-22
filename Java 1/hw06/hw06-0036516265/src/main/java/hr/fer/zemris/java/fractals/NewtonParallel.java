package hr.fer.zemris.java.fractals;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import hr.fer.zemris.java.fractals.Newton.MojProducer;
import hr.fer.zemris.java.fractals.mandelbrot.Mandelbrot;
import hr.fer.zemris.java.fractals.viewer.FractalViewer;
import hr.fer.zemris.java.fractals.viewer.IFractalProducer;
import hr.fer.zemris.java.fractals.viewer.IFractalResultObserver;
import hr.fer.zemris.math.Complex;
import hr.fer.zemris.math.ComplexPolynomial;
import hr.fer.zemris.math.ComplexRootedPolynomial;

/**
 * Paralelizirana implementacija Newtonovog fraktala
 * @author Dorian
 *
 */
public class NewtonParallel {
	
	public static double convergenceTreshold = 0.001;
	public static double rootTreshold = 0.002;

	
	//koristi se vrlo sličan kod za parsiranje ulaznih argumenata kao u Newtonu, ali ui
	//ovom slučaju mislim da to olakšava razumljivost i čitljivost koda više nego razbijanje tog dijela koda
	//na posebnu (statičku) metodu u zasebnoj klasi
	public static void main(String[] args) {
		
		System.out.println("Pokrećem kod");
		
		//default vrijednosti
		int noOfWorkers = Runtime.getRuntime().availableProcessors();
		int noOfTracks = 4 * noOfWorkers;
		
		//dakle ulaz može biti npr. --workers=10 ili -w 10
		
		String argName = "";
		String argValue = "";
		
	
		for (int i = 0; i < args.length; i++) {
			if (args[i].substring(0,1).equals("-")) {
				if (args[i].substring(0,2).equals("--")) { //radi se o obliku --workers=10 ili --tracks=5
					argName = args[i].split("=")[0];
					argValue = args[i].split("=")[1];
					
					if (argName.equals("--workers")) {
						try {
							noOfWorkers = Integer.parseInt(argValue);
						}catch(NumberFormatException e) {
							throw new IllegalArgumentException("Error while parsing arguments - cannot parse int! Please check your syntax!");
						}
					}else if (argName.equals("--tracks")){
						try {
							noOfTracks = Integer.parseInt(argValue);
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
				
				if (argName.equals("-w")) {
					try {
						argValue = args[i];
						noOfWorkers = Integer.parseInt(argValue);
					}catch(NumberFormatException e) {
						throw new IllegalArgumentException("Error while parsing arguments! Please check your syntax!");
					}
				}else if (argName.equals("-t")){
					try {
						argValue = args[i];
						noOfTracks = Integer.parseInt(argValue);
					}catch(NumberFormatException e) {
						throw new IllegalArgumentException("Error while parsing arguments! Please check your syntax!");
					}
				}else {
					throw new IllegalArgumentException("Error while parsing arguments - unsupported argument name! Please check your syntax!");
				}
				
			}
		}
		
		System.out.println("Workers value is");
		System.out.println(noOfWorkers);
		System.out.println("Tracks value is");
		System.out.println(noOfTracks);
		
		//prvo parsiramo arugmente
		
		Scanner sc = new Scanner(System.in);
		
		List<Complex> tmpArray = new ArrayList<>();
		
		int noOfRoots = 1;
		
		String input = "";
		
		System.out.println("Welcome to Newton-Raphson iteration-based fractal viewer, using paralellization!.");
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
		
		FractalViewer.show(new MojProducer(new ComplexRootedPolynomial(Complex.ONE, inputRoots), noOfWorkers, noOfTracks));

	}
	
	
	
	public static class PosaoIzracuna implements Runnable{
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
		AtomicBoolean cancel;
		ComplexRootedPolynomial polinom;
		ComplexPolynomial derived;
		public static PosaoIzracuna NO_JOB = new PosaoIzracuna();
		
		//koristi se samo za inicijalizaciju red-pill posla
		private PosaoIzracuna() {
			
		}

		public PosaoIzracuna(double reMin, double reMax, double imMin,
				double imMax, int width, int height, int yMin, int yMax, 
				int m, short[] data, AtomicBoolean cancel, ComplexRootedPolynomial polinom, ComplexPolynomial derived) {
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
			
		}
		
		@Override
		public void run() {
			
			//mislim da ovo treba zamijeniti odgovarajućim kodom za naš fraktal
			//mislim da offset računaš na osnovu y ili što već -> najbolje dodati lokalni offset i inicijalizirati ga na yMin
			//i povećavati u svakom koraku za 1 jel
			//dakle svaki radnik radi po n redova
			//dakle, recimo da mora obraditi samo prvi i drugi red, te da je širina prozora 500
			//onda kreće od 0, te ide do 1000 (1000=broj redova koji mora obraditi * širina reda)
			//kako znamo odakle kreće offset -> jednostavno
			//gledamo ymin -> ako je on recimo 10 -> krećemo od 10 reda, dakle offset je 10*širina reda = 5000
			
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
					data[offset] = (short) (polinom.indexOfClosestRootFor(zn, rootTreshold) + 1);
					offset++;
					
				}
			}

			
			
			//Mandelbrot.calculate(reMin, reMax, imMin, imMax, width, height, m, yMin, yMax, data, cancel);
			
		}
		
	}
	
	public static class MojProducer implements IFractalProducer {
		
		ComplexRootedPolynomial polinom = null;
		ComplexPolynomial derived = null;
		int noOfWorkers = 0;
		int noOfTracks = 0;
		
		
		public MojProducer(ComplexRootedPolynomial polinom, int noOfWorkers, int noOfTracks) {
			this.polinom = polinom;
			this.derived = polinom.toComplexPolynom().derive();
			this.noOfWorkers = noOfWorkers;
			this.noOfTracks = noOfTracks;
		}
		
		

		
		//znači ustvari samo treba podijeliti posao tako da svaki thread primi jednu traku i za nju računa boje i puni polje data
		//polje data je zajedničko jel
		@Override
		public void produce(double reMin, double reMax, double imMin, double imMax,
				int width, int height, long requestNo, IFractalResultObserver observer, AtomicBoolean cancel) {
			System.out.println("Zapocinjem izracun...");
			System.out.println("Na raspolaganju je " + noOfTracks + ". Obrađujem ih koristeći " + noOfWorkers + " dretvi");
			int m = 16*16*16;
			short[] data = new short[width * height];
			if (this.noOfTracks > height) { //u slucaju da korisnik unese veci broj traka nego sto je redaka u slici, za broj traka koristimo broj redaka u slici
				this.noOfTracks = height;
			}
			final int brojTraka = this.noOfTracks;
			int brojYPoTraci = height / brojTraka;
			
			final BlockingQueue<PosaoIzracuna> queue = new LinkedBlockingQueue<>();
			
			Thread[] radnici = new Thread[this.noOfWorkers];
			for(int i = 0; i < radnici.length; i++) {
				radnici[i] = new Thread(new Runnable() {
					@Override
					public void run() {
						while(true) {
							PosaoIzracuna p = null;
							try {
								p = queue.take();
								if(p==PosaoIzracuna.NO_JOB) break; //kažemo dretvi "hey, ako naletiš na red pill posao, gotova si s radom"
							} catch (InterruptedException e) {
								continue;
							}
							p.run();
						}
					}
				});
			}
			for(int i = 0; i < radnici.length; i++) {
				radnici[i].start();
			}
			
			for(int i = 0; i < brojTraka; i++) {
				int yMin = i*brojYPoTraci;
				int yMax = (i+1)*brojYPoTraci-1;
				if(i==brojTraka-1) {
					yMax = height-1;
				}
				PosaoIzracuna posao = new PosaoIzracuna(reMin, reMax, imMin, imMax, width, height, yMin, yMax, m, data, cancel, polinom, derived);
				while(true) {
					try {
						queue.put(posao);
						break;
					} catch (InterruptedException e) {
					}
				}
			}
			for(int i = 0; i < radnici.length; i++) {
				while(true) {
					try {
						queue.put(PosaoIzracuna.NO_JOB); //dodajemo "red pill" poslove, pomoću kojih dretve znaju kad stati
						break;
					} catch (InterruptedException e) {
					}
				}
			}
			
			for(int i = 0; i < radnici.length; i++) { //ovdje samo kažemo glavnom programu da čeka na završetak svih dretvi
				while(true) {
					try {
						radnici[i].join();
						break;
					} catch (InterruptedException e) {
					}
				}
			}
			
			System.out.println("Racunanje gotovo. Idem obavijestiti promatraca tj. GUI!");
			observer.acceptResult(data, (short)(polinom.toComplexPolynom().order() + 1), requestNo);

			
//			for(int y = 0; y < height; y++) {
//				if(cancel.get()) break;
//				for(int x = 0; x < width; x++) {
//					double cre = x / (width-1.0) * (reMax - reMin) + reMin; //ove dvije linije odgovaraju map_to_complex_plain
//					double cim = (height-1.0-y) / (height-1) * (imMax - imMin) + imMin;
//					Complex zn = new Complex(cre, cim);
//					double zre = 0;
//					double zim = 0;
//					double module = 0;
//					int iters = 0;
//					do {
//						//ovo je osnovni dio koda
//						//vidi što ti je convergenceTreshold -> mislim da je to module < 4.0 koji već piše u while dijelu petlje
//						Complex numerator = this.polinom.apply(zn);
//						Complex denominator = derived.apply(zn); //e al derived uvijek ostaje isti hm, mislim da je greška u apply
//						Complex znold = zn;
//						Complex fraction = numerator.divide(denominator);
//						zn = zn.sub(fraction);
//						module = znold.sub(zn).module();
//
//						iters++;
//					} while(iters < m && Math.abs(module) > convergenceTreshold); //promijeni ovo u odgovarajući treshold
//					data[offset] = (short) (polinom.indexOfClosestRootFor(zn, rootTreshold) + 1);
//					offset++;
//				}
//			}
//			System.out.println("Racunanje gotovo. Idem obavijestiti promatraca tj. GUI!");
//			observer.acceptResult(data, (short)(polinom.toComplexPolynom().order() + 1), requestNo);
		}
	}

}
