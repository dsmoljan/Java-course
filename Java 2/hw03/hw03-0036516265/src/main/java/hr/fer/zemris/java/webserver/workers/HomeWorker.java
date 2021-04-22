package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

public class HomeWorker implements IWebWorker{

	@Override
	public void processRequest(RequestContext context) throws Exception {
		String bgcolor = context.getPersistentParameter("bgcolor");
		if (bgcolor == null) {
			bgcolor = "7F7F7F";
		}
		
		context.setTemporaryParameter("background", bgcolor);
		context.getDispatcher().dispatchRequest("/private/pages/home.smscr");

	}

}
