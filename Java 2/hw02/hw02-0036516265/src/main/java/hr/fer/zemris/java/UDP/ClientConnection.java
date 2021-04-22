package hr.fer.zemris.java.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import hr.fer.zemris.java.UDP.messages.AckMsg;
import hr.fer.zemris.java.UDP.messages.InMsg;
import hr.fer.zemris.java.UDP.messages.Message;

public class ClientConnection implements Runnable{
	
	
	private InetAddress address;
	private int port;
	private long UID;
	private String username;
	private volatile long clientMessageID;
	private volatile long serverMessageID;
	private boolean keepConnection;
	private DatagramSocket socket;
	private long key;
	
	private BlockingQueue<InMsg> inQueue = new LinkedBlockingQueue<>();  //red poruka koje čekaju slanje na ovog klijenta -> mora biti parametrizirano po Message kako bi se mogle i BYE message slati
	private BlockingQueue<AckMsg> ackQueue = new LinkedBlockingQueue<>();  //red primljenih potvrda ovog klijenta, sigurno ti treba!
	
	
	
	public ClientConnection(long key, InetAddress address, int port, long uID, String username, DatagramSocket socket) {
		super();
		this.key = key;
		this.address = address;
		this.port = port;
		UID = uID;
		this.username = username;
		this.clientMessageID = 0;
		this.serverMessageID = 0;
		this.keepConnection = true; //redpill
		this.socket = socket;
	}



	@Override
	public void run() {
		while(keepConnection) {
			InMsg msgToSend = null;
			try {
				msgToSend = inQueue.take(); //blokirajuća metoda; ako je red prazan dretva koja izvodi ovaj kod blokira se dok ne dođe nešto u taj red
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//ako si došao do ovdje, znači da imaš poruku za slanje
			
			int counter = 0;
			
			byte[] buf = null;
			try {
				buf = msgToSend.getMessageAsByte();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			packet.setAddress(address);
			packet.setPort(port);
			
			while (counter <= 10) {
				counter++;
				try {
					socket.send(packet);
					System.out.println("Šaljem INMSG na " + packet.getSocketAddress());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				AckMsg ackMsg = null;
				try {
					ackMsg = ackQueue.poll(5000, TimeUnit.MILLISECONDS); //čeka do 5 sekundi na ackqueueu da se element pojavi
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
				
				if (ackMsg == null) {
					continue; //ako se akcMsg još nije pojavio u redu, idi u sljedeći korak petlje, odnosno ponovno pošalji poruku
				}
				
				
				//ako smo došli do ovdje, znači da se pojavila AckPoruka i da smo je uspješno uzeli iz queua
				//sad samo još provjeriti je li serverMessageID dobar
				
				if (ackMsg.getMessageID() != this.serverMessageID) {
					System.out.println("Greška - za klijenta sa UID-om " + this.UID + " dobio sam ACK poruku sa rednim brojem " + ackMsg.getMessageID() + " a očekivao sam poruku sa rednim brojem " + this.serverMessageID);;
					continue;
				}
				
				break;
			}
			
			System.out.println("Ni nakon 10 pokušaja nisam primio ACK poruku");
		}
		//dakle, kad ne radiš ništa, čekaš na inQueue
		//jednom kad pošalješ neku poruku (koja je stigla na inQueue), onda čekaš na ackQueue 10 puta
		//možda je baš onda i bolje da ackQueue ne bude blokirajuća metoda hm
	}
	
	public void addInMsg(InMsg msg) {
		inQueue.add(msg);
	}
	
	public void addAckMsg(AckMsg msg) {
		ackQueue.add(msg);
	}



	public InetAddress getAddress() {
		return address;
	}



	public int getPort() {
		return port;
	}



	public long getUID() {
		return UID;
	}



	public String getUsername() {
		return username;
	}



	public long getClientMessageID() {
		return clientMessageID;
	}
	
	public void setClientMessageID(long clientMessageID) {
		this.clientMessageID = clientMessageID;
	}



	public long getServerMessageID() {
		return serverMessageID;
	}
	
	public void setServerMessageID(long serverMessageID) {
		this.serverMessageID = serverMessageID;
	}



	public BlockingQueue<InMsg> getInQueue() {
		return inQueue;
	}



	public BlockingQueue<AckMsg> getAckQueue() {
		return ackQueue;
	}
	
	/**
	 * Postavlja parametar keepConnection na false, čime efektivno gasi ovog ClientConnectiona i njegovu dretvu
	 */
	public void breakConnection() {
		this.keepConnection = false;
	}
	
	public long getKey() {
		return this.key;
	}
	
	

}
