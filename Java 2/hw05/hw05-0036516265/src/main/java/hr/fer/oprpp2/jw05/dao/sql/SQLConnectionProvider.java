package hr.fer.oprpp2.jw05.dao.sql;

import java.sql.Connection;

/**
 * Pohrana veza prema bazi podataka u ThreadLocal object. ThreadLocal je zapravo
 * mapa čiji su ključevi identifikator dretve koji radi operaciju nad mapom.
 * 
 * Mislim da služi kako bi stalno koristili istu SQL vezu za jednog korisnika, jer to možda želimo raditi na transakcijski način
 *
 */
public class SQLConnectionProvider {

	private static ThreadLocal<Connection> connections = new ThreadLocal<>();
	
	/**
	 * Postavi vezu za trenutnu dretvu (ili obriši zapis iz mape ako je argument <code>null</code>).
	 * 
	 * @param con veza prema bazi
	 */
	public static void setConnection(Connection con) {
		if(con==null) {
			connections.remove();
		} else {
			connections.set(con);
		}
	}
	
	/**
	 * Dohvati vezu koju trenutna dretva (pozivatelj) smije koristiti.
	 * 
	 * @return vezu prema bazi podataka
	 */
	public static Connection getConnection() {
		return connections.get();
	}
	
}