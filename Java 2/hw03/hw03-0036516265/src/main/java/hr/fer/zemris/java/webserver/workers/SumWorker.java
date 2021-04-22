package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

public class SumWorker implements IWebWorker{

	@Override
	public void processRequest(RequestContext context) throws Exception {
		String aAsString = context.getParameter("a");
		String bAsString = context.getParameter("b");
		
		int a = 1;
		int b = 2;
		
		try {
			a = Integer.parseInt(aAsString);
		}catch(Exception e) {
		}
		
		try {
			b = Integer.parseInt(bAsString);
		}catch(Exception e) {
			
		}
		context.setMimeType("text/html");
		
		context.setTemporaryParameter("zbroj", Integer.toString(a+b));
		
		context.setTemporaryParameter("varA", Integer.toString(a));
		context.setTemporaryParameter("varB", Integer.toString(b));
		
		String imgName = "";
		
		if ((a+b)%2 == 0) {
			imgName = "images/palm.jpg";
		}else {
			imgName = "images/dogs.jpg";
		}
		
		context.setTemporaryParameter("imgName", imgName);
		
		context.getDispatcher().dispatchRequest("/private/pages/calc.smscr");
		
	}

}
