package hr.fer.zemris.java.UDP.messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class AckMsg extends Message{
	
	private long UID;
	
	//TODO: konstruktor kakav će koristiti poslužitelj
	public AckMsg() {
		this.messageCode = 2;
	}
	
	//TODO: konstruktor kakav će koristiti poslužitelj - još malo prepravi
	public AckMsg(long messageID, long UID) {
		this();
		this.messageID = messageID;
		this.UID = UID;
		
	}
	
	/**
	 * Konstruktor - prima polje bajtova - UDP datagram, a vraća AckMsg
	 * @param buf - polje bajtova
	 * @param offset - offset od kojeg konkretne informacije počinju
	 * @param length - duljina polja2
	 * @throws IOException
	 */
	public AckMsg(byte[] buf, int offset, int length) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(buf, offset, length);
		DataInputStream is = new DataInputStream(bis);
		
		is.readByte(); //samo da prođemo prvi bajt - messageCode
		
		messageID = is.readLong();
		UID = is.readLong();
		
		bis.close();
		is.close();

	}

	public int getMessageCode() {
		// TODO Auto-generated method stub
		return this.messageCode;
	}

	public void setMessageID(long messageID) {
		this.messageID = messageID;
		
	}

	public long getMessageID() {
		return this.messageID;
	}

	public byte[] getMessageAsByte() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		
		dos.writeByte(this.getMessageCode());
		dos.writeLong(this.getMessageID());
		dos.writeLong(this.getUID());
		
		dos.close();
		

		//dovrši...
		return bos.toByteArray(); //hm hoće li ovo raditi...
	}

	//mislim da ovo nije ni potrebno, jer ionako moramo pročitati
	public Message getMessageFromByteArray(byte[] array) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getUID() {
		return UID;
	}

	public void setUID(long uID) {
		UID = uID;
	}

	
	

}
