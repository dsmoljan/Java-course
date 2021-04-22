package hr.fer.zemris.java.webserver.workers;

import java.io.IOException;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

public class EchoParams implements IWebWorker{

	@Override
	public void processRequest(RequestContext context) throws Exception {
		context.setMimeType("text/html");
		String odgovor = "\"<html><body><h1>Poslani parametri su</h1><table border=\"1\">OVDJE</table></body></html>";
		try {
			StringBuilder sb = new StringBuilder();
			for (String parameterName : context.getParameterNames()) {
				sb.append("<tr><td>").append(parameterName).append("</td><td>").append(context.getParameter(parameterName)).append("</td></tr>\n");
			}
			odgovor = odgovor.replace("OVDJE", sb.toString());
			context.write(odgovor);
		}catch(IOException e) {
			System.out.println("Greška prilikom slanja odgovora klijentu");
		}
	}

}
