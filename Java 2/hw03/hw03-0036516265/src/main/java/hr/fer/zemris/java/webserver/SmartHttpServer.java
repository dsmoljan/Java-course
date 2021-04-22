package hr.fer.zemris.java.webserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hr.fer.zemris.java.custom.scripting.exec.SmartScriptEngine;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.webserver.RequestContext.RCCookie;

public class SmartHttpServer {
	private String address;
	private String domainName;
	private int port;
	private int workerThreads;
	private static int sessionTimeout = 600; //in seconds
	private Map<String, String> mimeTypes = new HashMap<String, String>();
	private ServerThread serverThread;
	private ExecutorService threadPool;
	private Path documentRoot;
	private Map<String, IWebWorker> workersMap;
	private static volatile boolean exit = false;
	private volatile Map<String, SessionMapEntry> sessions = new HashMap<>();
	private volatile Random sessionRandom = new Random();
	
	public static void main(String[] args) {
		SmartHttpServer server = new SmartHttpServer(args[0]);
		server.start();
	}

	/**
	 * Čita config datoteku i postavlja parametre sa vrijednostima iz config datoteke.
	 * @param configFileName
	 */
	public SmartHttpServer(String configFileName) {
		//pročitaj config datoteku, i postavi vrijednosti iz config datoteke
		Properties propertiesMap = new Properties();
		File configFile = new File(configFileName);
		
		try(InputStream configFileInputStream = new FileInputStream(configFile)) {
			propertiesMap.load(configFileInputStream);
		} catch (FileNotFoundException e) {
			System.out.println("Greška prilikom čitanja config datoteke - datoteka nije pronađena!");
			return;
		}catch (IOException e) {
			System.out.println("Greška prilikom čitanja config datoteke!");
		}
		
		System.out.println(propertiesMap);
		this.address = propertiesMap.getProperty("server.address");
		this.domainName = propertiesMap.getProperty("server.domainName");
		this.port = Integer.parseInt(propertiesMap.getProperty("server.port"));
		this.workerThreads = Integer.parseInt(propertiesMap.getProperty("server.workerThreads"));
		this.sessionTimeout = Integer.parseInt(propertiesMap.getProperty("session.timeout"));
		this.documentRoot = Paths.get((propertiesMap.getProperty("server.documentRoot"))).toAbsolutePath().normalize();
		
		File mimeTypeConfigFile = new File(propertiesMap.getProperty("server.mimeConfig"));
		
		Properties mimeConfigProperties = new Properties();
		
		//System.out.println("Putanja do mimeType config datoteke: " + mimeTypeConfigFile.getAbsolutePath());

		try(InputStream configFileInputStream = new FileInputStream(mimeTypeConfigFile)) {
			mimeConfigProperties.load(configFileInputStream);
		} catch (FileNotFoundException e) {
			System.out.println("Greška prilikom čitanja mimeType config datoteke - datoteka nije pronađena!");
			return;
		}catch (IOException e) {
			System.out.println("Greška prilikom čitanja mimeType config datoteke!");
		}
		
		//dodajemo mime type postavke
		for (Entry<Object, Object> entry : mimeConfigProperties.entrySet()) {
			String key = (String)entry.getKey();
			String value = (String)entry.getValue();
			mimeTypes.put(key, value);
		}
		
		System.out.println(mimeTypes);
		
		//obradi postavke za url-workers mapiranja
		
		File workersToUrlConfig = new File(propertiesMap.getProperty("server.workers"));
		Properties workersToUrlProperties = new Properties();
		this.workersMap = new LinkedHashMap<>();
		
		try(Scanner configFileScanner = new Scanner(workersToUrlConfig)) {
			while (configFileScanner.hasNextLine()) {
				String line = configFileScanner.nextLine();
				if (line.charAt(0) == '#' || line.length() == 0) { //ako se radi o komentaru
					continue;
				}else {
					String[] tmpList = line.split("=");
					String path = tmpList[0].trim();
					String FQCN = tmpList[1].trim();
					
					//sad kad imamo FQCN (fully qualified class name), tražimo od JVM da nam stvori objekt te klase
					Class<?> referenceToClass = this.getClass().getClassLoader().loadClass(FQCN);
					Object newObject = referenceToClass.newInstance();
					IWebWorker iww = (IWebWorker)newObject;
					
					if (workersMap.get(path) != null) {
						throw new InvalidPropertiesFormatException("Error - found two or more lines with the same path!");
					}
					workersMap.put(path, iww);
				}

			}
		} catch (FileNotFoundException e) {
			System.out.println("Greška prilikom čitanja workersToUrl config datoteke - datoteka nije pronađena!");
			return;
		}catch (IOException e) {
			System.out.println("Greška prilikom čitanja workersToUrl config datoteke!");
		}catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
			System.out.println("Greška prilikom stvaranja workera sa klasom danom u postavkama!");
		}
	}

	protected synchronized void start() {
		this.serverThread = new ServerThread();
		this.serverThread.start(); //pokrećemo poslužiteljsku dretvu
		this.threadPool = Executors.newFixedThreadPool(this.workerThreads);
		SessionExpiredThread sessionExpiredCheckerThread = new SessionExpiredThread();
		sessionExpiredCheckerThread.setDaemon(true);
		sessionExpiredCheckerThread.start();
	}

	//signal server thread to stop running
	//shutdown threadpool
	protected synchronized void stop() {
		this.exit = true;
		this.threadPool.shutdown();

	}

	protected class ServerThread extends Thread {
		@Override
		public void run(){
			
			@SuppressWarnings("resource")
			ServerSocket serverSocket = null;
			try {
				serverSocket = new ServerSocket();
				serverSocket.bind(new InetSocketAddress(address, port));
			} catch (IOException e) {
				System.out.println("Greška prilikom otvaranja serverovog socketa!");
			}
			
			while(exit == false) {
				//blokirajuća metoda
				try {
					Socket client = serverSocket.accept();
					System.out.println("Stigao novi zahtjev od klijenta!");
					ClientWorker cw = new ClientWorker(client);
					threadPool.execute(cw);
				} catch (IOException e) {
					System.out.println("Greška prilikom prihvaćanja dolazne konekcije!");
				}
			}
			// given in pesudo-code:
			// open serverSocket on specified port
			// while(true) {
			// Socket client = serverSocket.accept();
			// ClientWorker cw = new ClientWorker(client);
			// submit cw to threadpool for execution
			// }
		}
	}

	/**
	 * Worker zadužen za komunikaciju sa jednim spojenim klijentom
	 * 
	 * @author Dorian
	 *
	 */
	private class ClientWorker implements Runnable, IDispatcher {
		private Socket csocket;
		private InputStream clientIS;
		private OutputStream clientOS;
		private String version;
		private String method;
		private String host;
		private Map<String, String> params = new HashMap<String, String>();
		private Map<String, String> tempParams = new HashMap<String, String>();
		private Map<String, String> permParams = new HashMap<String, String>();
		private List<RCCookie> outputCookies = new ArrayList<RequestContext.RCCookie>();
		private String SID;
		private RequestContext context = null;

		public ClientWorker(Socket csocket) {
			super();
			this.csocket = csocket;
		}

		@Override
		public void run() {
			
			try{
				// obtain input stream from socket
				// obtain output stream from socket
				// Then read complete request header from your client in separate method...
				List<String> requestHeadersList = null;
				
				try {
					clientIS = new BufferedInputStream(csocket.getInputStream());
					clientOS = new BufferedOutputStream(csocket.getOutputStream());
	
					requestHeadersList = readRequestHeader(clientIS);
				}catch(IOException e) {
					System.out.println("Greška prilikom otvaranja uzlanog streama prema klijentu ili čitanja headera!");
				}
	
				// If header is invalid (less then a line at least) return response status 400
				if (requestHeadersList == null) {
					try {
						System.out.println("Header je nevažeći, vraćam 400");
						sendEmptyResponse(clientOS, 400, "Invalid header");
					} catch (IOException e) {
						System.out.println("Greška prilikom slanja poruke...");
					}
					return;
				}
				
				String firstLine = requestHeadersList.get(0);   //header koji počinje sa GET, npr. GET /abc/def?name=joe&country=usa HTTP/1.1
				String[] tmpList = firstLine.split(" ");
				
				//iz prvog headera izvlačimo metodu (GET, POST...), requestedPath(path do traženog resursa + parameteri ako se radi o GET metodi), te http verziju
				String method = tmpList[0];
				String requestedPath = tmpList[1];
				String httpVersion = tmpList[2];
				
				// if method not GET or version not HTTP/1.0 or HTTP/1.1 return response status
				// 400
				
	
				if (method.equals("GET") == false || (httpVersion.equals("HTTP/1.0") == false && httpVersion.equals("HTTP/1.1") == false)) {
					try {
						System.out.println("Primljena je druga metoda osim GET, ili se radi o nepodržanoj verziji HTTP protokola");
						sendEmptyResponse(clientOS, 400, "Unsupported method or HTTP protocol!");
					} catch (IOException e) {
						System.out.println("Greška prilikom slanja poruke...");
					}
					return;
				}
				

				
				// Go through headers, and if there is header “Host: xxx”, assign host property
				// to trimmed value after “Host:”; else, set it to server’s domainName
				// If xxx is of form some-name:number, just remember “some-name”-part
				boolean hostHeaderFound = false;
				for (String header : requestHeadersList) {
					if (header.toLowerCase().contains("host:")) {
						tmpList = header.split(":");  //čak i ako je u obliku host:some-name:number, ovo će izvući ono što treba: some-name
						this.host = tmpList[1].trim();
						hostHeaderFound = true;
						break;
					}
				}
				
				//https://stackoverflow.com/questions/5891084/obtaining-domain-name-using-java
				if (hostHeaderFound == false) {
					this.host = domainName;
				}
				
				//add cookies if neccessary
				checkSession(requestHeadersList);
				
				
				//razmisli hoće li ovo raditi kako spada ako nisu predani nikakvi parametri, tj. ako nema ?
				System.out.println("requestedPath: " + requestedPath);
				tmpList = requestedPath.split("\\?");
				
				//iz punog patha (npr. /abc/def?name=joe&country=usa) izvlačimo path (/abc/def) te parametre (name=joe&country=usa)
				String path = tmpList[0]; 
				if (tmpList.length == 2) {
					String paramString = tmpList[1];
					//parsiramo parametre i dodajemo ih u mapu params
					parseParameters(paramString);
				}
				

				
				internalDispatchReqest(path, true);
			}catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {
					csocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		


		private void internalDispatchReqest(String path, boolean directCall) throws Exception{

			//ako se radi o pathu za koji je zadužen neki od IWebWorkera
			
			if (path.startsWith("/private") && directCall == true) {
				System.out.println("Korisnik je tražio path koji počinje sa private, pa vraćam 404");
				sendEmptyResponse(clientOS, 404, "Stranica nije pronađena");
				return;
			}
			
			if (path.startsWith("/ext/") && path.length() > 5) {
				String workerName = "";
				workerName = path.replace("/ext/", "");
				System.out.println("Ime workera: " + workerName);
				
				Class<?> referenceToClass = this.getClass().getClassLoader().loadClass("hr.fer.zemris.java.webserver.workers." + workerName);
				Object newObject = referenceToClass.newInstance();
				IWebWorker worker = (IWebWorker)newObject;
				
				if (context == null) {
					context = new RequestContext(clientOS, params, permParams, outputCookies, tempParams, this);
				}
				context.setStatusCode(200);
				worker.processRequest(context);
				
				return;
			}
			
			if (workersMap.containsKey(path)) {
				IWebWorker worker = workersMap.get(path);
				if (context == null) {
					context = new RequestContext(clientOS, params, permParams, outputCookies, tempParams, this);
				}
				context.setStatusCode(200);
				worker.processRequest(context);
				return;
			}
			
			Path requestedFilePath = documentRoot.resolve(path.substring(1)).toAbsolutePath().normalize(); //valjda spajamo root path sa pathom koji je korisnik tražio. Substring mora da maken / iz index.html
			System.out.println("Traženi file path: " + requestedFilePath);
			
			if (!requestedFilePath.toString().startsWith(documentRoot.toString())) {//želimo provjeriti nalazi li se staza koju je korisnik tražio unutar direktorija documentRoot
				try {
					System.out.println("Korisnik je zadao nedopuštenu putanju do datoteke, pa vraćam 403");
					sendEmptyResponse(clientOS, 403, "Nedopuštena putanja do datoteke!");
				} catch (IOException e) {
					System.out.println("Greška prilikom slanja poruke...");
				}
				return;
			}
			
			// check if requestedPath exists, is file and is readable; if not, return status
			// 404
			if (requestedFilePath.toFile().exists() == false || requestedFilePath.toFile().canRead() == false) {
				try {
					System.out.println("Korisnik je tražio sljedeći path: " + requestedFilePath + ", koji ne postoji, pa vraćam 404");
					sendEmptyResponse(clientOS, 404, "Datoteka nije pronađena");
				} catch (IOException e) {
					System.out.println("Greška prilikom slanja poruke...");
				}
				return;
			}
			
			// else extract file extension
			// find in mimeTypes map appropriate mimeType for current file extension
			// (you filled that map during the construction of SmartHttpServer from
			// mime.properties)
			// if no mime type found, assume application/octet-stream
			String extension = extractExtension(requestedFilePath.getFileName().toString());
			String mt = determineMimeType(extension, mimeTypes);
			

			if (extension.equals("smscr")) {
				if (context == null) {
					context = new RequestContext(clientOS, params, permParams, outputCookies, tempParams, this);
				}
				//context.setMimeType(mt);
				context.setStatusCode(200);
				
				//permParams.put("brojPoziva", "0");
				
				String documentBody = readFile(requestedFilePath.toAbsolutePath());
//				Map<String,String> parameters = new HashMap<String, String>();
//				Map<String,String> persistentParameters = new HashMap<String, String>();
//				List<RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();
				// create engine and execute it
				new SmartScriptEngine(
				new SmartScriptParser(documentBody).getDocumentNode(),
				context).execute();
			}else {
				if (context == null) {
					context = new RequestContext(clientOS, params, permParams, outputCookies);
				}
				context.setMimeType(mt);
				context.setStatusCode(200);
				
				byte[] data = null;
				
				try {
					data = Files.readAllBytes(requestedFilePath);
				} catch (IOException e) {
					System.out.println("Greška prilikom čitanja datoteke!");
				}
				
				context.addAdditionalHeader("Content-Length: " + Integer.toString(data.length));
				
				try {
					context.write(data);
				} catch (IOException e) {
					System.out.println("Greška prilikom slanja datoteke klijentu!");
				}
			}
		}
		
		/**
		 * Privatna metoda za pamćenje cookiea
		 * @param requestHeadersList
		 */
		private void checkSession(List<String> requestHeadersList) {
			String sidCandidate = "";
			boolean sidCandidateFound = false;
			 for (String header : requestHeadersList) {
				 if (!(header.startsWith("Cookie:"))) {
					 continue;
				 }
				 String[] cookieList = header.split(":")[1].split(";");
				 for (String cookie : cookieList) {
					 String[] pair = cookie.split("=");
					 if (pair[0].trim().equals("sid")) {
						 sidCandidate = pair[1].replace("\"", "");
						 sidCandidateFound = true;
						  break;
					 }
				 }
				 if (sidCandidateFound) {
					 break;
				 }
			 }
			 
			 SessionMapEntry sessionEntry = null;
			 boolean addNewCookie = false;
			 if (sidCandidateFound){
				 System.out.println("Primio sam kandidata za cookie: " + sidCandidate);
				 sessionEntry = sessions.get(sidCandidate);
				 if (sessionEntry != null) {
					 if (sessionEntry.getHost().equals(host)) {
						 if (sessionEntry.getValidUntil() > System.currentTimeMillis()) {
							 //cookie prisutan i nije istekao
							 //povećavamo mu session timeout
							 sessionEntry.setValidUntil(sessionEntry.getValidUntil() + (sessionTimeout*1000));
						 }
						 else {
							 //cookie istekao: mičemo ga iz mape, te ćemo ispod dodati novi
							 System.out.println("Cookie istekao; mičem ga i dodajem novi");
							 sessions.remove(sidCandidate);
							 addNewCookie = true;
						 }
					 }else {
						 addNewCookie = true;
						 System.out.println("Ne postoji cookie za trenutni host, pa dodajem novi");
					 }
				}else {
					addNewCookie = true;
				}
			 }else {
				 addNewCookie = true;
				 System.out.println("Cookie sa danim sid-om ne postoji u mapi, pa dodajem novi");
			 }
			 
			 if (addNewCookie == true) {
				 SID = getRandomString(20);
				 sessionEntry = new SessionMapEntry(SID, host);
				 sessions.put(SID, sessionEntry);
				 outputCookies.add(new RCCookie("sid", SID, null, host, "/", true));
			 }
			 
			 System.out.println("Dodjeljujem cookie: " + SID);
			 permParams = sessionEntry.getMap();
			 		 
			 

			 
		}
		
		private List<String> readRequestHeader(InputStream cis) throws IOException {
			
			Optional<byte[]> request = SmartHttpServer.readRequest(cis);  //pročitaj bajtove zagljavlja dolaznog zahtjeva
			if(request.isEmpty()) {
				return null;
			}
			
			String requestHeaderStr = new String(  //pretvaramo niz bajtova u ASCII String
					request.get(), 
					StandardCharsets.US_ASCII
				);
			
			//svaki redak zahtjeva = 1 header, ali postoji i mogućnost višeredčanih headera
			List<String> headers = new ArrayList<String>();
			String currentLine = null;
			for(String s : requestHeaderStr.split("\n")) {
				if(s.isEmpty()) break;
				char c = s.charAt(0);
				if(c==9 || c==32) {
					currentLine += s; //headeri u više redova
				} else {
					if(currentLine != null) {
						headers.add(currentLine);
					}
					currentLine = s;
				}
			}
			if(!currentLine.isEmpty()) {
				headers.add(currentLine);
			}
			return headers;

		}
		
		/**
		 * Parsira parametre iz danog stringa i ubacuje ih u člansku varijablu mapu params.
		 * Npr. za string <i>name=joe&country=usa</i> izvodi 
		 * <code>params.put("name", "joe");
		   params.put("country", "usa");</code>
		 * @param paramString
		 */
		private void parseParameters(String paramString) {
			String[] tmpList = paramString.split("&");
			for (String paramPair : tmpList) {
				String[] tmpList2 = paramPair.split("=");
				String paramKey = tmpList2[0];
				String paramValue = tmpList2[1];
				this.params.put(paramKey, paramValue);
			}
		}
		
		/**
		 * Pomoćna metoda za slanje odgovora bez tijela
		 * @param cos
		 * @param statusCode
		 * @param statusText
		 * @throws IOException
		 */
		private void sendEmptyResponse(OutputStream cos, int statusCode, String statusText) throws IOException {
			RequestContext rc =  new RequestContext(cos, params, permParams, outputCookies);
			rc.setStatusCode(statusCode);
			rc.setStatusText(statusText);
			System.out.println("Nalazim se u sendEmptyResponse, mimeType je: " + rc.getMimeType());
			rc.write(new byte[0]);

		}
		


		@Override
		public void dispatchRequest(String urlPath) throws Exception {
			internalDispatchReqest(urlPath, false);
		}

	}
	
	/**
	 * Dretva koja svakih 5 minuta prolazi kroz sve pohranjene cookie i miče one kojima je isteklo vrijeme.
	 * @author Dorian
	 *
	 */
	private class SessionExpiredThread extends Thread{
		
		@Override
		public void run() {
			try {
				Thread.sleep(300000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Set<String> toRemove = new LinkedHashSet<>();
			System.out.println("Započinjem micanje neaktivnih cookiea");
			for (String SID : sessions.keySet()) {
				SessionMapEntry sessionEntry = sessions.get(SID);
				if (sessionEntry.getValidUntil() > System.currentTimeMillis()) {
					toRemove.add(SID);
				}
			}
			
			for (String SID : toRemove) {
				sessions.remove(SID);
			}
		}
	}




	
	/**
	 * Metoda preuzeta iz 4. predavanja OPRPP2. DKA koji čita zagljavlje HTTP zahtjeva
	 * @param is
	 * @return
	 * @throws IOException
	 * @author Marko Čupić
	 */
	private static Optional<byte[]> readRequest(InputStream is) throws IOException {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int state = 0;
l:		while(true) {
			int b = is.read();
			if(b==-1) {
				if(bos.size()!=0) {
					throw new IOException("Incomplete header received.");
				}
				return Optional.empty();
			}
			if(b!=13) {
				bos.write(b);
			}
			switch(state) {
			case 0: 
				if(b==13) { state=1; } else if(b==10) state=4;
				break;
			case 1: 
				if(b==10) { state=2; } else state=0;
				break;
			case 2: 
				if(b==13) { state=3; } else state=0;
				break;
			case 3: 
				if(b==10) { break l; } else state=0;
				break;
			case 4: 
				if(b==10) { break l; } else state=0;
				break;
			}
		}
		return Optional.of(bos.toByteArray());
	}
	
	/**
	 * Pomoćna metoda za ekstrakciju ekstenzije datoteke. Metoda preuzeta iz 4. predavanja OPRPP2
	 * @param filename
	 * @return
	 * @author Marko Čupić
	 */
	private static String extractExtension(String filename) {
		int p = filename.lastIndexOf('.');
		
		if (p < 1) {
			return ""; //nema ekstenuziju
		}
		
		return filename.substring(p + 1).toLowerCase();
	}
	
	/**
	 * Pomoćna metoda za određivanje pripadnog mime-typea datoteke na osnovu ekstenzije datoteke.
	 * @param ekstenzija
	 * @return
	 */
	private static String determineMimeType(String extension, Map<String, String> mimeTypeMap) {
		String mimeType = mimeTypeMap.get(extension);
		if (mimeType == null) {
			mimeType = "application/octet-stream";
		}
		return mimeType;
	}
	
	private String getRandomString(int targetStringLength) {
	    int leftLimit = 65;
	    int rightLimit = 97;

	    String generatedString = sessionRandom.ints(leftLimit, rightLimit + 1)
	      .limit(targetStringLength)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();

	    return generatedString;
	}
	
	private String readFile(Path path) {
		File file = new File(path.toString());
		try(InputStream is = new FileInputStream(file)) {
			if(is==null) throw new RuntimeException("Datoteka je nedostupna.");
			byte[] data = is.readAllBytes();
			String text = new String(data, StandardCharsets.UTF_8);
			return text;
		} catch(IOException ex) {
			throw new RuntimeException("Greška pri čitanju datoteke.", ex);
		}
	}
	
	private static class SessionMapEntry{
		
		SessionMapEntry(String sid, String host){
			this.sid = sid;
			this.map = new ConcurrentHashMap<>();
			this.host = host;
			this.validUntil = System.currentTimeMillis() + (sessionTimeout*1000);
		}
		
		String sid;
		String host;
		//in miliseconds
		long validUntil;
		Map<String, String> map; //has to be some thread safe implementation
		public String getSid() {
			return sid;
		}
		public String getHost() {
			return host;
		}
		public long getValidUntil() {
			return validUntil;
		}
		
		public void setValidUntil(long validUntil) {
			this.validUntil = validUntil;
		}
		
		public Map<String, String> getMap() {
			return map;
		}
		
		
	}
	
	
	
}
