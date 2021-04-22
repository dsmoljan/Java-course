package hr.fer.oprpp1.hw05.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
		
	public static void main(String[] args) {
		String method = args[0];
		//String path = args[1];
		String filename = args[1];
		
		//System.out.println("Path je...");
		
		if (method.equals("checksha")) {
			checksha(filename);
		} else if (method.equals("encrypt")) {
			String newFilename = args[2];
			encryptOrDecrpyt(true, filename, newFilename);
		} else if (method.equals("decrypt")) {
			String newFilename = args[2];
			encryptOrDecrpyt(false, filename, newFilename);
		} else {
			System.err.println("Error! Unsupported method! Check your spelling!");
		}
		
	}
	
	//sad nisam siguran, ako hoćemo pokrenuti program s argumentom iz command linea, trebamo li staviti da program prima argumente?
	//mislim da ne
	/**
	 * A command line program which, given a (binary) file and a message digest, checks whether the message digest 
	 * of the file corresponds to the given one.
	 */
	public static void checksha(String filename) {
		
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Please provide expected sha-256 digest for " + filename + ":");
		
		String sha256 = sc.nextLine(); //ovo je ono što nam korisnik da, ono što trebamo usporediti sa sha256 koji mi računamo na osnovu datoteke
		sc.close();
		//System.out.println("Provided sha is " + sha256);
		
		
		Path path = Paths.get("./" + filename);
		byte[] tmpBuff = new byte[4096];
		byte[] calculatedDigest = new byte[32]; //32 jer SHA-256 vraća 32-bitni digest
		
		try (InputStream is = Files.newInputStream(path)) { //ako ga otvorimo ovako, automatski se zatvara, čak i u slučaju greške
			
			// nope ovo je za kriptiranje/dekriptiranje Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			//ovdje mi treba messagedigest
			 MessageDigest md = MessageDigest.getInstance("SHA-256");
	
			
			while(true) {
				int r = is.read(tmpBuff);
				
				if (r < 1) break;
				
				md.update(tmpBuff, 0, r);
			}
			
			calculatedDigest = md.digest();
			
		} catch (IOException e){
			System.out.println("Pogreška prilikom čitanja datoteke!");
			return;
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Pogreška prilikom računanja SHA-256 sažetka!");
			return;
		}
		
//		//sad treba usporediti digest koji je dao user (koji je u obliku stringa) sa našim digestom (u obliku byte[]!)
		
		byte[] givenDigest = Util.hextobyte(sha256);
		
		if (Arrays.equals(givenDigest, calculatedDigest)) {
			System.out.println("Digesting completed. Digest of " + filename + " matches expected digest.");
		} else {
			System.out.println("Digesting completed. Digest of " + filename + " does not match the expected digest. Digest was: " + Util.bytetohex(calculatedDigest));
		}
		
		
	}
	
	
	//ne zaboravi da većina koda mora biti zajednička encrypt i decrpyt, ne raditi copy paste

	public static void encryptOrDecrpyt(boolean encrypt, String originalFile, String outputFile) {
		
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Please provide password as hex-encoded text (16 bytes, i.e. 32 hex-digits");
		
		String password = sc.nextLine();
		
		System.out.println("Please provide initialization vector as hex-encoded text (32 hex-digits)");
		
		String initVector = sc.nextLine();

		sc.close();
		
		
		Path inputPath = Paths.get("./" + originalFile);
		Path outputPath = Paths.get("./" + outputFile);

		
		try (InputStream is = Files.newInputStream(inputPath);
				OutputStream os = Files.newOutputStream(outputPath,  java.nio.file.StandardOpenOption.CREATE_NEW)) { //ako ga otvorimo ovako, automatski se zatvara, čak i u slučaju greške
			byte[] tmpBuff = new byte[4096];
			
			SecretKeySpec keySpec = new SecretKeySpec(Util.hextobyte(password), "AES");
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(Util.hextobyte(initVector));
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, keySpec, paramSpec);
				
			
			while(true) {
				int r = is.read(tmpBuff); //čitamo određeni broj bytova i spremamo ih u polje tmpBuff. Broj bitova koje smo pročitali sprema se u varijablu r
									       //čipokušava se pročitati najmanje 1 bajt, najviše size(tmpBuff) bajtova
										  //ako više nema što za čitati (na kraju smo), metoda vraća -1
										  //zbog toga moramo paziti da ne predajemo cijelo polje tmpBuff cipheru, jer možda nije cijelo popunjeno
										  //stoga u r pohranimo koliko smo bitova actually pročitali
										  //i samo r bajtova iz tmpBuff šaljemo u metodu update (to radiš koristeći varijantu metode update gdje joj kažeš uzmi samo r bajtova iz tmpBuff)
				
				if (r < 1) { 
					os.write(c.doFinal());
					
					break;
				}
				
				os.write(c.update(tmpBuff, 0, r));
			}
			
		} catch (IOException e){
			System.out.println("Pogreška prilikom čitanja datoteke!");
			System.err.println(e.getMessage());
	        File f = new File(outputFile);
	        f.delete();
			return;
		} catch (NoSuchPaddingException
				| NoSuchAlgorithmException
				| InvalidAlgorithmParameterException
				| InvalidKeyException
				| BadPaddingException 
				| IllegalBlockSizeException e) {
			System.out.println("Pogreška prilikom enkripcije/dekripcije! Provjerite šifru i inicijalizacijski vektor!");
	        File f = new File(outputFile);
	        f.delete();
	        return;
		}
		
		if (encrypt == true) {
			System.out.println("Encryption completed. Generated file " +  outputFile + " based on file " + originalFile + ".");
		} else {
			System.out.println("Decryption completed. Generated file " +  outputFile + " based on file " + originalFile + ".");

		}
		
		//otvaramo datoteku
		
//		System.out.println("Path is " + path);
//		System.out.println("Veličina: " + Files.exists(path));
		
		//ovako dohvaćamo jedan Cipher
		
		//kako radimo kriptiranje/dekriptiranje
		//čitamo podatke iz datoteke (recimo 4096 bajtova)
		//svaki put kad pročitamo nešto pozovemo update i predamo cipheru pročitano polje
		//ona alocira (tj vraća) novo polje kriptiranih podataka
		//ako vrati polje, pišemo u izlazni tok podataka
		//ali ako vrati 0 ili null (pogledaj što točno), onda ništa
		//kad sve pročitamo, zadnji korak je doFinal
		//i štogod da on vrati isto pošaljemo u izlazni tok
		
		
		//e imati na umu da metode encrypt i decrpyt imaju jako puno zajedničkog koda
		//nemoj raditi copy paste, nego taj zajednički kod negdje izdvoji
		
		//može se napisati jedna generička metoda koja će malo pomalo čitati
		//slati cipher objetku, rezultate koje dobiva od njega spremati negdje
		//a mi toj metodi kroz neki argument samo kažemo što radi
		
		
		//pisanje u izlaznu datoteku il polje štagod također radi preko streamova
		
		//kako čitamo?
		//input stream sa read čita do 4096 bitova (jer je tolko veliko tmpBuff) i sprema ih u tmpBuff
	}

}
