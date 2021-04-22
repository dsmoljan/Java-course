package hr.fer.zemris.java.UDP.messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ByeMsg extends Message{
	
	private long UID;
	
	public ByeMsg() {
		this.messageCode = 3;
	}
	
	public ByeMsg(long messageID, long UID) {
		this();
		this.messageID = messageID;
		this.UID = UID;
	}

	public ByeMsg(byte[] buf, int offset, int length) throws IOException {
		this();
		
		ByteArrayInputStream bis = new ByteArrayInputStream(buf, offset, length);
		DataInputStream is = new DataInputStream(bis);
		
		is.readByte();
		
		this.messageID = is.readLong();
		this.UID = is.readLong();
		
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
		
		byte[] result = bos.toByteArray();
		
		bos.close();
		dos.close();
		
		return result;
	}

	@Override
	public Message getMessageFromByteArray(byte[] array) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getUID() {
		return UID;
	}
	
	

}
