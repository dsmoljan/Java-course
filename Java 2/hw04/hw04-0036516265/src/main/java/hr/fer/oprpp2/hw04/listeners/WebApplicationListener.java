package hr.fer.oprpp2.hw04.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import hr.fer.oprpp2.hw04.global.GlobalData;
import hr.fer.oprpp2.hw04.global.Kljucevi;

@WebListener
public class WebApplicationListener implements ServletContextListener{

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		GlobalData gd = new GlobalData();
		sce.getServletContext().setAttribute(Kljucevi.KEY_GLOBAL_DATA, gd);
		sce.getServletContext().log("Webapp2 je inicijalizirana");
		sce.getServletContext().setAttribute(Kljucevi.KEY_RUNNING_TIME, System.currentTimeMillis());

		
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		sce.getServletContext().removeAttribute(Kljucevi.KEY_GLOBAL_DATA);
		sce.getServletContext().removeAttribute(Kljucevi.KEY_RUNNING_TIME);
		sce.getServletContext().log("Webapp2 završava s radom");

		
	}

}
