package hr.fer.zemris.java.UDP.messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class InMsg extends Message{
	

	private String senderUsername;
	private String messageText;
	
	public InMsg() {
		this.messageCode = 5;
	}
	
	public InMsg(long messageID, String senderUsername, String messageText) {
		this();
		this.messageID = messageID;
		this.senderUsername = senderUsername;
		this.messageText = messageText;
	}
	
	public InMsg(byte[] buf, int offset, int length) throws IOException {
		this();
		ByteArrayInputStream bis = new ByteArrayInputStream(buf, offset, length);
		DataInputStream is = new DataInputStream(bis);
		
		is.readByte(); //samo prolazimo prvi byte, šifru tipa poruke
		messageID = is.readLong();
		senderUsername = is.readUTF();
		messageText = is.readUTF();
		
	}

	@Override
	public int getMessageCode() {
		return this.messageCode;
	}

	@Override
	public void setMessageID(long messageID) {
		this.messageID = messageID;
	}

	@Override
	public long getMessageID() {
		return this.messageID;
	}

	@Override
	public byte[] getMessageAsByte() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		
		dos.writeByte(this.messageCode);
		dos.writeLong(this.messageID);
		dos.writeUTF(this.senderUsername);
		dos.writeUTF(this.messageText);
	
		bos.close();
		dos.close();
		
		return bos.toByteArray();
	}

	@Override
	public Message getMessageFromByteArray(byte[] array) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSenderUsername() {
		return senderUsername;
	}

	public void setSenderUsername(String senderUsername) {
		this.senderUsername = senderUsername;
	}

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}
	
	

}
