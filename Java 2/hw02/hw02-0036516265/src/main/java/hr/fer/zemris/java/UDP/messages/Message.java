package hr.fer.zemris.java.UDP.messages;

import java.io.IOException;

public abstract class Message {
	
	/**
	 * Kod koji identificira vrstu poruke:
	 * 1 - HelloMsg
	 * 2 - AckMsg
	 * 3 - ByeMsg
	 * 4 - OutMsg
	 * 5 - InMsg 
	 * @return
	 */
	protected byte messageCode;
	/**
	 * Redni broj poruke u komunikaciji između klijenta i poslužitelja
	 */
	protected long messageID;
	
	/**
	 * Vraća lkd koji identificira vrstu poruke:
	 * 1 - HelloMsg
	 * 2 - AckMsg
	 * 3 - ByeMsg
	 * 4 - OutMsg
	 * 5 - InMsg 
	 * @return
	 */
	public abstract int getMessageCode();  //0, 1, 2... označuju o kojem se tipu poruke radi
	
	/**
	 * Postavlja redni broj poruke, korišten u komunikaciji između klijenta i poslužitelja
	 * @param messageID
	 */
	public abstract void setMessageID(long messageID);
	
	/**
	 * Vraća redni broj poruke, korišten u komunikaciji između klijenta i poslužitelja
	 * @return
	 */
	
	public abstract long getMessageID();
	
	/**
	 * Pretvara poruku u polje bajtova, koji se mogu prenositi protokolm UDP. 
	 * Svaki razred koji implementira ovo sučelje mora imati svoju implementaciju ove metode,
	 * jer ovisi o njegovim članskim varijablama itd.
	 * @return
	 * @throws IOException 
	 */
	public abstract byte[] getMessageAsByte() throws IOException;
	
	/**
	 * Rekonstruira poruku iz polja bajtova primljenih preko protokola UDP.
	 * Svaki razred koji implementira ovo sučelje mora imati svoju implementaciju ove metode.
	 * Ova metoda ti najvjerojatnije neće trebati jer svaka implementacija razreda Message ima 
	 * konstruktor koji prima byte[], te na taj način iz byte[] možemo dobiti poruku
	 * @param array
	 * @deprecated
	 * @return
	 */
	public abstract Message getMessageFromByteArray(byte[] array);

}
