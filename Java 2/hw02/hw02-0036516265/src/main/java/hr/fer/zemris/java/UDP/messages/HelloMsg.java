package hr.fer.zemris.java.UDP.messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class HelloMsg extends Message{
	
	private long messageID;
	private String username;
	private long key; //random ključ, korišten samo u HelloMsg
	
	public HelloMsg() {
		this.messageCode = 1;
	}
	
	public HelloMsg(long messageID, String username, long key) {
		this();
		this.messageID = messageID;
		this.username = username;
		this.key = key;
	}
	
	public HelloMsg(byte[] buf, int offset, int length) throws IOException {
		
		this();
		
		ByteArrayInputStream bis = new ByteArrayInputStream(buf, offset, length);
		DataInputStream is = new DataInputStream(bis);
		
		is.readByte();	//da preskocimo sifru poruke
		this.messageID = is.readLong();
		this.username = is.readUTF();
		this.key = is.readLong();
	
		is.close();
	}

	public int getMessageCode() {
		return this.messageCode;
	}

	public void setMessageID(long messageID) {
		this.messageID = messageID;
		
	}

	public long getMessageID() {
		return this.messageID;
	}

	//svaki razred mora imati svoju konkretnu implemetaciju, jer ona ovisi o članskim varijablma razreda
	public byte[] getMessageAsByte() throws IOException {
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		
		dos.writeByte(this.messageCode);
		dos.writeLong(this.messageID);
		dos.writeUTF(this.username);
		dos.writeLong(key);
		
		dos.close();
		
		return bos.toByteArray();

	}

	public Message getMessageFromByteArray(byte[] array) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUsername() {
		return username;
	}

	public long getKey() {
		return key;
	}
	
	

}
