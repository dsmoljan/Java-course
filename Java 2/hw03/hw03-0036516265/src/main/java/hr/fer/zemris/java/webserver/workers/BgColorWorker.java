package hr.fer.zemris.java.webserver.workers;

import java.awt.Color;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

public class BgColorWorker implements IWebWorker{

	@Override
	public void processRequest(RequestContext context) throws Exception {
		String bgcolor = context.getParameter("bgcolor");
		boolean colorIsValid = true;
		try {
		    Color color = Color.getColor(bgcolor);
		} catch (IllegalArgumentException iae) {
		    colorIsValid = false;
		}
		
		context.setPersistentParameter("bgcolor", bgcolor);
		
		context.getDispatcher().dispatchRequest("/index2.html");
	}

}
