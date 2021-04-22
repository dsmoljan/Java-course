package hr.fer.zemris.java.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class RequestContext {
	
	private OutputStream os;
	private Charset charset;
	
	private String encoding = "UTF-8";
	private int statusCode = 200;
	private String statusText = "OK";
	private String mimeType = "text/html";
	Long contentLength = null;
	
	private Map<String, String> parameters;
	private Map<String, String> temporaryParameters;
	private Map<String, String> persistentParameters;
	private List<RCCookie> outputCookies;
	
	private List<String> additionalHeaders;
	private IDispatcher dispatcher;
	
	/**
	 * If true, all attempts to change any of the following properties: encoding, statusCode, statusText, mimeType, outputCookies, contentLength 
	 * must throw RuntimeException.
	 */
	boolean headerGenerated = false;
	
	public RequestContext(OutputStream outputStream, Map<String, String> parameters, Map<String,String> persistentParameters, List<RCCookie> outputCookies){
		
		if (outputStream == null) {
			throw new IllegalArgumentException("Parameter 'outputStream' must not be null!");
		}
		
		if (parameters == null) {
			parameters = new LinkedHashMap<String, String>();
		}
		
		if (persistentParameters == null) {
			persistentParameters = new LinkedHashMap<String, String>();
		}
		
		if (outputCookies == null) {
			outputCookies = new ArrayList<RequestContext.RCCookie>();
		}
		this.temporaryParameters = new LinkedHashMap<>();
		this.os = outputStream; // must not be null!
		this.parameters = parameters; // if null, treat as empty
		this.persistentParameters = persistentParameters; // if null, treat as empty
		this.outputCookies = outputCookies; // if null, treat as empty
		this.additionalHeaders = new ArrayList<>();
	}
	
	public RequestContext(OutputStream outputStream, Map<String, String> parameters, Map<String,String> persistentParameters, List<RCCookie> outputCookies, Map<String, String> temporaryParameters, IDispatcher dispatcher){
		this(outputStream, parameters, persistentParameters, outputCookies);
		this.temporaryParameters = temporaryParameters;
		this.dispatcher = dispatcher;
	}
	
	
	public static class RCCookie{
		String name;
		String value;
		String domain;
		String path;
		Integer maxAge;
		boolean httpOnly;
		
		public RCCookie(String name, String value, Integer maxAge, String domain, String path, boolean httpOnly) {
			this.name = name;
			this.value = value;
			this.domain = domain;
			this.path = path;
			this.maxAge = maxAge;
			this.httpOnly = httpOnly;
		}
		
		public String getName() {
			return name;
		}
		public String getValue() {
			return value;
		}
		public String getDomain() {
			return domain;
		}
		public String getPath() {
			return path;
		}
		public Integer getMaxAge() {
			return maxAge;
		}
		
		/**
		 * Ispis u formatu za header zahtjeva
		 */
		@Override
		public String toString() {
			String s = "";
			s += " " + this.name + "=" + "\"" + this.value + "\"";
			
			if (this.domain != null) {
				s += "; " + "Domain=" + this.getDomain();
			}
			
			if (this.path != null) {
				s += "; Path=" + this.path;
			}
			
			if (this.maxAge != null) {
				s += "; Max-Age=" + Integer.toString(this.maxAge);
			}
			
			if (this.httpOnly == true) {
				s += "; HttpOnly";
			}
			
			
			return s;
			
		}
		
	}
	
	/**
	 * Method that retrieves value from parameters map (or null if no association exists)
	 * @param name
	 * @return
	 */
	public String getParameter(String name) {
		return parameters.get(name);
	}
	
	/**
	 * Method that retrieves names of all parameters in parameters map (note, this set must be read-only):
	 * @return
	 */
	public Set<String> getParameterNames(){
		return parameters.keySet();
	}
	
	/**
	 * Method that retrieves value from persistentParameters map (or null if no association exists)
	 * @param name
	 * @return
	 */
	public String getPersistentParameter(String name) {
		return persistentParameters.get(name);
	}
	
	/**
	 * Stores a value to persistentParameters map
	 * @param name
	 * @param value
	 */
	public void setPersistentParameter(String name, String value) {
		persistentParameters.put(name, value);
	}
	
	/**
	 * Removes a value from persistentParameters map
	 * @param name
	 */
	public void removePersistentParameter(String name) {
		persistentParameters.remove(name);
	}
	
	/**
	 * Retrieves the requested value from temporaryParameters map (or null if no association exsists)
	 * @param name
	 * @return
	 */
	public String getTemporaryParameter(String name) {
		return temporaryParameters.get(name);
	}
	
	/**
	 * Retrieves names of all parameters in temporary parameters map. The returned set is read only
	 * @return
	 */
	public Set<String> getTemporaryParameterNames(){
		return temporaryParameters.keySet();
	}
	
	/**
	 * Retrieves an identifier which is unique for current uses session
	 * @return
	 */
	public String getSessionID() {
		return ""; //zasad, u nekom drugom zadatku će vraćati konkretnu vrijednost
	}
	
	/**
	 * Stores a value to the temporaryParameters map
	 * @param name
	 * @param value
	 */
	public void setTemporaryParameter(String name, String value) {
		temporaryParameters.put(name, value);
	}
	
	/**
	 * Removes a value from temporaryParameters map
	 * @param name
	 */
	public void removeTemporaryParameter(String name) {
		temporaryParameters.remove(name);
	}
	
	/**
	 * Writes data into this class's outputStream. First time that this, or any of the other 
	 * two write methods are called, a special header must be written into the underlying outputStream, and 
	 * only then can given data be written. This header is written only once, no matter which write method is called.
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public RequestContext write(byte[] data) throws IOException{
		
		if (headerGenerated == false) {
			os.write(createHeader());
			charset = Charset.forName(encoding);
			headerGenerated = true;
		}
		
		os.write(data);
		os.flush();
		
		return this;
	}
	
	/**
	 * Writes data into this class's outputStream.First time that this, or any of the other 
	 * two write methods are called, a special header must be written into the underlying outputStream, and 
	 * only then can given data be written. This header is written only once, no matter which write method is called.
	 * @param data
	 * @param offset
	 * @param len
	 * @return
	 * @throws IOException
	 */
	public RequestContext write(byte[] data, int offset, int len) throws IOException{

		if (headerGenerated == false) {
			os.write(createHeader());
			charset = Charset.forName(encoding);
			headerGenerated = true;
		}
		
		os.write(Arrays.copyOfRange(data, offset, offset + len));
		os.flush();
		
		return this;
	}
	
	/**
	 * Writes data into this class's outputStream, converting it from String to byte using previously configured encoding.
	 * First time that this, or any of the other 
	 * two write methods are called, a special header must be written into the underlying outputStream, and 
	 * only then can given data be written. This header is written only once, no matter which write method is called.
	 * @param text
	 * @return
	 * @throws IOException
	 */
	public RequestContext write(String text) throws IOException{
		if (headerGenerated == false) {
			os.write(createHeader());
			charset = Charset.forName(encoding);
			headerGenerated = true;
		}
		
		os.write(text.getBytes(charset));
		os.flush();
		
		return this;
	}
	
	/**
	 * Creates a request header, based on the member variables of this object.
	 * @return
	 */
	private byte[] createHeader() {
		
		//prva linija
		String s = "HTTP/1.1" + " " + Integer.toString(this.statusCode) + " " + this.statusText + "\r\n";  
		
		//druga linija
		s += "Content-Type:" + " " + this.mimeType;
		
		//dio druge linije
		if (this.mimeType.startsWith("text/")) {
			s += "; charset=" + this.encoding;
		}
		
		s += "\r\n";
		
		if (this.contentLength != null) {
			s += "Content-Length:" + " " + Long.toString(this.contentLength) + "\r\n";
		}
		
		if (outputCookies.isEmpty() == false) {
			for (RCCookie cookie : outputCookies) {
				s += "Set-Cookie:";
				s += cookie.toString() + "\r\n";
			}
		}
		
//		for (String additionalHeader : additionalHeaders) {
//			s += additionalHeader + "\r\n";
//		}
		
		s += "\r\n";
		

		
		byte[] b = s.getBytes(StandardCharsets.ISO_8859_1);
		
		return b;
	}
	
	/**
	 * Supports adding additional headers to the response.
	 */
	public void addAdditionalHeader(String header) {
		this.additionalHeaders.add(header);
		
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public void setContentLength(Long contentLength) {
		this.contentLength = contentLength;
	}

	public void addRCCookie(RCCookie rcCookie) {
		this.outputCookies.add(rcCookie);
	}

	public IDispatcher getDispatcher() {
		return dispatcher;
	}

	public void setDispatcher(IDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}
	
	public String getMimeType() {
		return this.mimeType;
	}
	
	
	
	
	
	
	
	
	

}
