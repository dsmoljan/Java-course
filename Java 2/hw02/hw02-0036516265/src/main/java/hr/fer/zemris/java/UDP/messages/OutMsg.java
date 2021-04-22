package hr.fer.zemris.java.UDP.messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class OutMsg extends Message{
	
	long UID;
	String messageText;

	public OutMsg() {
		this.messageCode = 4;
	}
	
	public OutMsg(long messageID, long UID, String messageText) {
		this();
		this.messageID = messageID;
		this.UID = UID;
		this.messageText = messageText;
	}
	
	public OutMsg(byte[] buf, int offset, int length) throws IOException {
		this();
		
		ByteArrayInputStream bis = new ByteArrayInputStream(buf, offset, length);
		DataInputStream is = new DataInputStream(bis);
		
		is.readByte();
		this.messageID = is.readLong();
		this.UID = is.readLong();
		this.messageText = is.readUTF();
		
		bis.close();
		is.close();
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
		dos.writeLong(this.UID);
		dos.writeUTF(this.messageText);
		
		dos.close();
		
		return bos.toByteArray();
	}

	@Override
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

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}
	
	
	
	
	

}
