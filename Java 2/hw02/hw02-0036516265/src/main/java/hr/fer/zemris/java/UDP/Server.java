package hr.fer.zemris.java.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hr.fer.zemris.java.UDP.messages.AckMsg;
import hr.fer.zemris.java.UDP.messages.ByeMsg;
import hr.fer.zemris.java.UDP.messages.HelloMsg;
import hr.fer.zemris.java.UDP.messages.InMsg;
import hr.fer.zemris.java.UDP.messages.OutMsg;

//pokretanje Servera iz basha - nalazimo se u direktoriju ~/Desktop/Java 2 zadace/hw02-0036516265 i pokrenemo sljedeću naredbu
//mvn exec:java -Dexec.mainClass="hr.fer.zemris.java.UDP.server" -Dexec.args="5006"
//gdje je 5006 adresa na kojoj želimo pokrenuti server


public class Server {
	private static long UIDGenerator;
	
	public Server() {
		
	}
	
	//ideja za implementaciju
	//imat ćeš poseban razred koji će predstavljati jednog klijenta tj. vezu s jednim klijentom
	//on će implementirati runnable tako da ga dretve (tj. executor pool) mogu pozvati
	//u sebi će imati još 2 liste: listu ack poruka i listu out poruka
	//imat ćeš još jednu glavnu dretvu - ona je zadužena za slušanje i kad dođe nova hello poruka, stvara novu vezu s dretvom
	//i u njezin red ack poruka stavi ack poruku i pozove run metodu razreda (recimo kroz exexcutor pool)
	//u metodi run se prolazi prvo kroz ack polje pa kroz outmsg polje, i što god se nađe se miče iz polja
	//i odgovarajuća poruka se šalje
	//recimo nađe se jedna ack poruka -> pošalje se
	//nađe se jedna outmsg poruka -> pošalje se
	//i na taj način kad dođe neka inmsg, samo dodaš po jedan outmsg svim vezama s klijentima (njih pamtiš recimo u listi) i pozoveš im svima metodu run
	//recimo nad svakom vezom pozoveš executor pool.submit, da bi se run metoda svakog objekta izvršavala u vlastitoj dretvi
	//ako ćeš ići preko ovog Čupkovog rješenja, onda ili imaš polje dretvi, ili podatkovna struktura "veza sa klijentom" ima referencu na svoju dretvu - da se dretva može ugasiti kad više nije potrebna
	
	public static void main(String[] args) throws IOException {
		
		ArrayList<ClientConnection> clientList = new ArrayList<>();
		
		
		
		Random rand = new Random();
		UIDGenerator = rand.nextLong();
		
		if (args.length != 1) {
			System.out.println("Dragi korisniče, očekivao sam port");
			return;
		}
		
		int port = 0;
		
		try {
			port = Integer.parseInt(args[0]);
		}catch(NumberFormatException ex) {
			System.out.println("Nije broj: " + args[0]);
		}
		
		if(port < 1 || port > 65535) {
			System.out.println("Dragi korisniče, port mora biti između 1 i 65535");
			return;
		}
		
		//DatagramSocket socket = null;
		
		ExecutorService pool = Executors.newCachedThreadPool(); //ovo moraš zatvoriti :/ -> možda najbolje sve ovo (i socket!) puknuti u try-with-resources

		try(DatagramSocket socket = new DatagramSocket(port)) {
			
			System.out.println("Pokrećem server na portu " + port);
			while(true) {
				
				 byte[] buf = new byte[4000];
				 DatagramPacket incomingPacket = new DatagramPacket(buf, buf.length);
				
				 try {
					//ovo je blokirajući poziv - dretva koja izvršava ovaj kod ovdje će se zablokirati dok ne dođe neki zahtjev
					 socket.receive(incomingPacket);
				 }catch(IOException e) {
				 }
				 
				System.out.println("Dobio sam poruku od: " + incomingPacket.getSocketAddress().toString());

				byte messageType = incomingPacket.getData()[incomingPacket.getOffset()];
				
				if (messageType == 1) { //ako je došao HelloMsg
					boolean alreadyExsists = false;
					HelloMsg helloMsg = new HelloMsg(incomingPacket.getData(), incomingPacket.getOffset(), incomingPacket.getLength());
					System.out.println("Primio HELLOMSG od: " + incomingPacket.getSocketAddress().toString());
					
					ClientConnection con = null;
					
					for (ClientConnection client : clientList) {
						if (client.getKey() == helloMsg.getKey()) {
							System.out.println("Klijent već postoji na serveru, radim retransmisiju ACK poruke");
							con = client;
							alreadyExsists = true;
							break;
						}
					}
					
					if (alreadyExsists == false) {
						con = new ClientConnection(helloMsg.getKey(), incomingPacket.getAddress(), incomingPacket.getPort(), UIDGenerator++, helloMsg.getUsername(), socket);
						clientList.add(con);
					}
					
					
					//treba smisliti neki način za dodavanje red pill poslova, tj. da osiguramo da dretva zadužena za con objekt završi jednom kada on
					//pošalje bye poruku
					//možda bi bilo dovoljno da run posao returna?
					//mislim da bi -> dap, korištenjem red pill posla
					//recimo kada hoćemo ugasiti tu dretvu, imamo posebnu varijablu u ClientConnection objektu (dodaj sutra)
					//i na kraju svake iteracije beskonačne while petlje provjerava se je li ta varijabla true
					//e ta varijabla mora biti volatile!
					//jednom kad je, onda više ne idemo u sljedeći krug while petlje (ona while petlja iz Čupkovog pseudokoda), nego samo izađemo iz metode
					//run, efektivno gaseći thread
					

					// glavni program se bavi SLANJEM ack poruka - tipa kad primi HelloMsg, ByeMsg, InMsg, GLAVNI PROGRAM šalje ack message natrag klinetu
					
					//potvrđujem klijentu da sam primio HelloMsg
					AckMsg ackMsg = new AckMsg(helloMsg.getMessageID(), con.getUID());
					byte[] bufOut = ackMsg.getMessageAsByte();
					DatagramPacket packet = new DatagramPacket(bufOut, bufOut.length);
					packet.setAddress(con.getAddress());
					packet.setPort(con.getPort());
					
					System.out.println("Šaljem ACK poruku klijentu na " + con.getAddress() + ":" + con.getPort());
					
					
					try {
						socket.send(packet);
					}catch(IOException e) {
						System.out.println("Greška prilikom slanja poruke!");
						return; //kako se ni HelloMsg nije uspio poslati, izlazimo iz programa
					}
					
					if (alreadyExsists) {
						continue;  //pošto samo radimo retransmisiju ACK poruke, ne trebamo ponovno pokretati dretvu
					}
					
					con.setClientMessageID(con.getClientMessageID() + 1); //povećavamo counter za poruke koje klijent šalje

					
					//ali za PRIMLJENE ack poruke, e to trebaš proslijeđivati ClientConnectionima tj. stavljati u njihov ackQueue,jer oni moraju znati da su klijenti potvrdili poruke koje su oni slali
					
					pool.submit(con);  //pokreće dretvu koja izvršava run metodu u con objektu
					
				} else if (messageType == 2) { //ako je došao ackMsg
					
					AckMsg msg = new AckMsg(incomingPacket.getData(), incomingPacket.getOffset(), incomingPacket.getLength());
					System.out.println("Primio ACKMSG od:" + incomingPacket.getSocketAddress());
					long uid = msg.getUID();
					
					//stavi tu ack poruku u red ack poruka odgovarajućeg ClientConnection
					for (ClientConnection con : clientList) {
						if (con.getUID() == uid) {
							con.addAckMsg(msg);
							//eventualno novi poziv pool.submit; ovisno na koji način implementriaš metodu run
							//ako će run biti "beskonačna" petlja (osim red pill poslova), onda ne trebaš ponovno zvati pool.submit
							break; //jer ne bi smjelo biti 2 klijenta s istim UID-om, tako da čim nađemo jednog klijenta s tim UID-om, dalje ne trebamo tražiti
						}
					}
					
				}else if (messageType == 3){ //ako je došao ByeMsg
					
					ByeMsg byeMsg = new ByeMsg(incomingPacket.getData(), incomingPacket.getOffset(), incomingPacket.getLength());
					
					System.out.println("Primio BYEMSG od:" + incomingPacket.getSocketAddress());
					
					ClientConnection sender = null;
					
					for (ClientConnection con : clientList) {
						if (con.getUID() == byeMsg.getUID()) {
							sender = con;
							break;
						}
					}
					
					AckMsg ackMsg = new AckMsg(byeMsg.getMessageID(), byeMsg.getUID());
					byte[] bufOut = ackMsg.getMessageAsByte();
					DatagramPacket packet = new DatagramPacket(bufOut, bufOut.length);
					packet.setAddress(incomingPacket.getAddress());
					packet.setPort(incomingPacket.getPort());
					
					if (sender.getClientMessageID() != byeMsg.getMessageID()) {
						System.out.println("Očekivao sam BYEMSG poruku za rednim brojem " + sender.getClientMessageID() +  " a dobio sam poruku sa rednim brojem " + byeMsg.getMessageID());
						System.out.println("Šaljem ACK, ali zanemarujem paket");
						socket.send(packet);
						continue;
					}
					
					socket.send(packet);
					System.out.println("Sve ok, šaljem ACK poruku klijentu na " + incomingPacket.getAddress() + ":" + incomingPacket.getPort());

					sender.breakConnection(); //ovim efektivno gasimo dretvu ClientConnectiona 
					clientList.remove(sender); //mičemo tu instancu ClientConnectiona iz liste
					
					//TODO
					continue;
				}else if (messageType == 4) { //ako je došao OutMsg
					OutMsg outMsg = new OutMsg(incomingPacket.getData(), incomingPacket.getOffset(), incomingPacket.getLength());

					System.out.println("Primio OUTMSG od:" + incomingPacket.getSocketAddress());
					
					ClientConnection sender = null;
					
					//ovo stalno koristiš u kodu, možeš ovo izdvojiti u zasebnu metodu, al to na kraju
					for (ClientConnection con : clientList) {
						if (con.getUID() == outMsg.getUID()) {
							sender = con;
							break;
						}
					}
					
					AckMsg ackMsg = new AckMsg(outMsg.getMessageID(), outMsg.getUID());
					byte[] bufOut = ackMsg.getMessageAsByte();
					DatagramPacket packet = new DatagramPacket(bufOut, bufOut.length);
					packet.setAddress(incomingPacket.getAddress());
					packet.setPort(incomingPacket.getPort());
					
					if (sender.getClientMessageID() != outMsg.getMessageID()) {
						System.out.println("Očekivao sam OUTMSG poruku za rednim brojem " + sender.getClientMessageID() +  " a dobio sam poruku sa rednim brojem " + outMsg.getMessageID());
						System.out.println("Šaljem ACK, ali zanemarujem paket");
						socket.send(packet);
						continue;
					}
							
					socket.send(packet);
					System.out.println("Sve ok, šaljem ACK poruku klijentu na " + incomingPacket.getAddress() + ":" + incomingPacket.getPort());
					sender.setClientMessageID(sender.getClientMessageID() + 1); 
					
					for (ClientConnection con : clientList) {
						con.setServerMessageID(con.getServerMessageID() + 1);
						InMsg inMsg = new InMsg(con.getServerMessageID(), sender.getUsername(), outMsg.getMessageText());
						con.addInMsg(inMsg); //stavljamo poruku u red za slanje svakog ClientConnectiona; taj objekt će se pobrinuti za slanje
					}
				}else {
					System.out.println("Nepodržan kod poruke!");
					continue;
				}

			}
			
		}catch(SocketException ex) { //jer se može dogoditi greška, npr. pokušamo pristupiti sistemskom (rezerviranom) portu
			System.out.println("Nije moguće otvoriti pristupnu točku. Detaljnija poruka: " + ex.getMessage());
			return;
		}
		

		
	}

}
