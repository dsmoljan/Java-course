package hr.fer.zemris.java.UDP;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import hr.fer.zemris.java.UDP.messages.AckMsg;
import hr.fer.zemris.java.UDP.messages.ByeMsg;
import hr.fer.zemris.java.UDP.messages.HelloMsg;
import hr.fer.zemris.java.UDP.messages.InMsg;
import hr.fer.zemris.java.UDP.messages.Message;
import hr.fer.zemris.java.UDP.messages.OutMsg;

//pokretanje klijenta iz basha - nalazimo se u direktoriju ~/Desktop/Java 2 zadace/hw02-0036516265 i pokrenemo sljedeću naredbu
//mvn exec:java -Dexec.mainClass="hr.fer.zemris.java.UDP.Client" -Dexec.args="127.0.0.1 5006 AnteAntic"
//gdje je 5006 adresa na kojoj je pokrenut server, a 127.0.0.1 samo predstavlja localhost


/**
 * Implementacija klijenta za uslugu razgovora. Za prijenos poruka 
 * korišten je protokol UDP.
 * @author Dorian
 *
 */
public class Client extends JFrame{
	
	private JTextField input;
	private JTextArea output;
	private InetAddress IPAdress;
	private int port;
	private String username;
	private long messageID;
	private volatile long clientMessageID; //jer i klijent i server numerira svaki svoje poruke -> na to treba paziti -> ovo će biti redni broj poruka koje klijent šalje
	private volatile long serverMessageID;  //recimo ovo će uvijek biti redni broj OutMsg koji primimo, i onda s ovim potvrđujemo
	private String text; 
	
	private DatagramSocket socket;
	private long UID;
	
	public static int SIZE_OF_ACK = 17; //jedna ACK poruka je duljine 17 okteta
	
	
	private final BlockingQueue<AckMsg> ackQueue = new LinkedBlockingQueue<>();
	private final BlockingQueue<InMsg> inQueue = new LinkedBlockingQueue<>();
	private final BlockingQueue<Message> outQueue = new LinkedBlockingQueue<>();
	
	
	public Client(String IPAdress, String port, String username) {
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); //imati ćemo listenera koji osluškuje je li stisnuta tipka za zatvaranje prozora
		setLocation(0, 0);
		setSize(600, 600);
		
		try {
			this.IPAdress = InetAddress.getByName(IPAdress);
		}catch(UnknownHostException e) {
			System.out.println("Ne može se razrješiti: " + IPAdress);
			dispose();
			return;
		}
		
		try {
			this.port = Integer.parseInt(port);
		}catch (NumberFormatException e) {
			System.out.println("Port neispravno zadan");
			close();
			return;
		}
		
		this.username = username;
		
		this.messageID = 0; //svaki klijent numerira svoje poruke - počinju od 0
		this.clientMessageID = 0;
		this.serverMessageID = 1; //serverovi ID-evi kreću od 1
		
		this.setTitle("Klijent za čavrljanje - " + username); 
		
		this.text = "";
		
		WindowListener wl = new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				ByeMsg byeMsg = new ByeMsg(++clientMessageID, UID);
				sendOutgoingMessage(byeMsg);
				close();
				dispose();
			}
			

		};
		
		this.addWindowListener(wl);
		initGUI();
		//connect();
	}
	
	
	private void initGUI() {
		this.getContentPane().setLayout(new BorderLayout());
		input = new JTextField();
		output = new JTextArea();
		output.setEditable(false);
		this.add(input, BorderLayout.NORTH);
		this.add(new JScrollPane(output), BorderLayout.CENTER);
		//ActionPerfomed se aktivira po defaultu kad korisnik klikne enter, a fokusiran je u text fieldu
		this.input.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(() -> {
					OutMsg outMsg = new OutMsg(++clientMessageID, UID, input.getText());
					sendOutgoingMessage(outMsg);
				});
				t.start();
				
			}
		});
		
	}
	
	/**
	 * Spaja se na poslužitelja, te održava komunikaciju s poslužiteljom.
	 * https://stackoverflow.com/questions/24754469/starting-a-thread-with-the-gui-java
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	private void connect() throws InterruptedException, IOException {
		
		//pristupna točka za klijenta
		//želimo da nam OS za tu pristupnu točku dodijeli jedan slobodan port - to se događa u default konstruktoru DatagramSocket razreda
		socket = null;
		
		try {
			socket = new DatagramSocket();
		}catch(SocketException e) {
			System.out.println("Ne mogu otvoriti pristupnu točku!");
			return;
		}
		
		//setUpConnection(); //ovo je ok
		//ako smo prošli korak iznad, znači da smo se povezali s poslužiteljem, te možemo početi osluškivati za poruke.
		//metoda za osluškivanje poruka
		//treba postojati i jedna zasebna metoda za slanje poruka
		
		
		//ovo samo osluškuje poruke, te ako se pojavi INMSG, ispisuje ju
		//moguće da će ovo trebati biti vlastita dretva, ali otom potom
		
		Random rd = new Random();
		long randomKey = rd.nextLong();
		HelloMsg helloMsg = new HelloMsg(clientMessageID, username, randomKey);
		byte[] buf = null;
		
		try {
			buf = helloMsg.getMessageAsByte();
		} catch (IOException e) {
			System.out.println("Greška prilikom stvaranja HelloMsg");
			close();
		} 
		
		Thread setUpConnectionThread = new Thread(() -> {
			sendOutgoingMessage(helloMsg);
			System.out.println("Konekcija s poslužiteljem uspješno uspostavljena");
		});
		
		setUpConnectionThread.start();

		
		byte[] bufIncoming = new byte[4000]; //ovako radimo ako ne znamo koliko će dolazeći paket biti velik
		
		DatagramPacket incomingPacket = null;
		
		while(true) {
			try {
				socket.setSoTimeout(5000);
			}catch(SocketException e) {}
			
			try {
				incomingPacket = new DatagramPacket(bufIncoming, bufIncoming.length);
				socket.receive(incomingPacket); 
			}catch(SocketTimeoutException e) {
				continue;							//ako nisi u 5 sekundi primio poruku, idi u sljedeći korak while petlje, tj. nastavi čekati
			} catch (IOException e) {
				//TODO: nekako zatvori sve ovo
				System.out.println("Zatvaranje klijenta?");
				return;
			}
			
			
			byte messageType = incomingPacket.getData()[incomingPacket.getOffset()];
					
			if (messageType == 2) { //ako je došao ackMsg
				ackQueue.put(new AckMsg(incomingPacket.getData(), incomingPacket.getOffset(), incomingPacket.getLength()));
			} else if (messageType == 5){ //ako je došao inMsg
				inQueue.put(new InMsg(incomingPacket.getData(), incomingPacket.getOffset(), incomingPacket.getLength()));
				Thread t = new Thread(() -> {
					processIncMsg();
				});
				t.start();
				continue;
			}
			
		}
	}
	
	private void processIncMsg() {
		InMsg inMsg = null;
		
		System.out.println("Primio INMSG");
		
		try {
			inMsg = inQueue.take();
		} catch (InterruptedException e) {
			System.out.println("Greška prilikom uzimanja InMsg sa ackQueue");
			return;
		}
		
		if (inMsg == null) {
			System.out.println("InMsg je null, izlazim. Ne bi smjela biti null, tako da je negdje greška");
			return;
		}
		
		if (inMsg.getMessageID() != serverMessageID) {
			System.out.println("Greška! Očekivao sam poruku sa rednim brojem " + Long.toString((serverMessageID)) + " a dobio sam poruku sa rednim brojem " + Long.toString(inMsg.getMessageID()));
		}
		
		//ispis primljene poruke na ekran
		//this.text += "\n";
		this.text += "[/" + this.IPAdress + ":" + this.port + "] Poruka od korisnika: " + inMsg.getSenderUsername() + "\n";
		this.text += inMsg.getMessageText() + "\n\n";
		this.output.setText(text);
		
		sendAckMessage(serverMessageID); //mislim da je OK da ova dretva šalje ACK poruku, jer kod slanja ACKA nema čekanja
		serverMessageID++;
		
	}
	
	/**
	 * Obavlja sve potrebne radnje za slanje jedne izlazne poruke - HelloMsg, OutMsg, ByeMsg. Čeka 5s AckMsg, te ako nije,
	 * vrši retransmisiju. 
	 * @throws
	 */
	private void sendOutgoingMessage(Message msg) {
		byte[] buf;
		
		try {
			buf = msg.getMessageAsByte();
		} catch (IOException e1) {
			System.out.println("Greška prilikom konstruiranja izlazne poruke! Poruka nije poslana");
			return;
		}
		
		int counter = 0;
		
		while (counter <= 10) {
			counter++;
			sendMessage(buf);
			System.out.println("Šaljem izlaznu poruku sa rednim brojem clientID " + clientMessageID);
			
			AckMsg ackMsg = null;
			
			 try {
				ackMsg = ackQueue.poll(5000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} //čeka do 5 sekundi da se pojavi ACK poruka u redu ACK porukam, ako ni nakon 5 sekundi ne dođe ack poruka, radi retransmisiju
														
			
			 
			 if (ackMsg == null) { //ni nakon 5 sekundi nije primljena ACK poruka, ponovno šaljemo poruku
				 System.out.println("Nije primljena ACK poruka, ponovno šaljem poruku");
				 continue;
			 }
			 
			if (this.clientMessageID == 0) {
				this.UID = ackMsg.getUID();
			}
			 
			if (ackMsg.getMessageID() != clientMessageID) {
				System.out.println("Greška! Očekivao sam poruku sa rednim brojem " + Long.toString((messageID)) + " a dobio sam poruku sa rednim brojem " + Long.toString(ackMsg.getMessageID()));
				close();
				return; 
			}
			
			//ako smo došli do ovdje, sve je ok
			
			System.out.println("Primio sam ACK poruku sa rednim brojem " + ackMsg.getMessageID() + ". Sve OK");
			
			
			input.setText(""); //ovo radi dretva koja obrađuje ack poruke
			return;
		}
		
		System.out.println("Ni nakon 10 pokušaja nisam dobio odgovor od poslužitelja, pa pozivam metodu za gašenje");
		close();
		
	}
	
	
	/**
	 * Šalje ACK poruku, koristi člansku varijablu messageID da zna koji je broj poruke na koji treba odgovoriti.
	 * Pošto je ovo samo ACK poruka, nema nikakvog ponovnog slanja nego se ACK samo jednom šalje.
	 */
	private void sendAckMessage(long messageID) {
		AckMsg ackMsg = new AckMsg(messageID, UID);
		byte[] buf = new byte[SIZE_OF_ACK];
		
		try {
			buf = ackMsg.getMessageAsByte();
		} catch (IOException e) {
			System.out.println("Greška prilikom stvaranja ACK poruke!");
			return; //ili close?
		}  
		
		//šaljemo ACK
		
		sendMessage(buf);
	}
	
	/**
	 * Jednostavna pomoćna metoda za slanje izlazne poruke, bez obzira o tipu poruke.
	 */
	private void sendMessage(byte[] buf) {
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		packet.setAddress(IPAdress);
		packet.setPort(port);
		
		System.out.println("Šaljem poruku");
		
		try {
			socket.send(packet);
		}catch(IOException e) {
			System.out.println("Greška prilikom slanja poruke!");
			return; //kako se ni HelloMsg nije uspio poslati, izlazimo iz programa
		}
	}
	
	/**
	 * Zatvaranje klijentskog programa, bilo regularno, bilo zbog iznimke.
	 * Oslobađaju se svi zauzeti resursi
	 */
	private void close() {
		System.out.println("Zatvaram klijenta...");
		if (socket != null) {
			socket.close();
		}
		dispose();
		return;
	}

	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Greška - pogrešan broj argumenata!");
			return;
		}
		final String IPAdress = args[0];
		final String port = args[1];
		final String username = args[2];
		
		final Client c = new Client(IPAdress, port, username);
		
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				c.setVisible(true);
			}
		});
		;

		
		Thread t = new Thread(() ->{
			try {
				c.connect();
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		});
		t.start();
		
	}

}

/*
 * Zasad ideja: jedna glavna dretva koja samo radi socket.recieve()
 * Ako nešto stigne (npr. InMsg), trpa to u red za InMsg, i zove novu dretvu da to odradi
 * Slično i ako stigne AckMsg
 * Dok recimo slanje poruka može direktno obavljati EDT (odnosno i "glavna" dretva u slučaju HelloMsg, koji se šalje samo jednom) - samo negdje naznači (navjerojatnije preko clientMessageID/serverMessageID) da očekuje ACK poruku
 * određenog indexa 
 * Što se tiče slanja, problem je retransmisije ako nije stigla potvrda
 * Bolje da EDT napravi novu dretvu koja će se poslati poruku, te ju dodati u outMsg - time naznačivši da očekuje potvrdu za to. Svakih 5 sekundi provjeri je
 * li outMsgQueue prazan, te ako nije, ponovno šalje poruku. PROVJERAVA SA CONTAINS!!!!
 * Kad potvrda stigne, zove se metoda processAck, koja samo provjeri je li to odgovarajuća potvrda, te makne jedan element iz outMsqQueue
 * */
