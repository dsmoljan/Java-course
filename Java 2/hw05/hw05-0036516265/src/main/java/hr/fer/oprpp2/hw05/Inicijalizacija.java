package hr.fer.oprpp2.hw05;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.management.RuntimeErrorException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

@WebListener
public class Inicijalizacija implements ServletContextListener {
	
	private static String initPath = "WEB-INF/dbinit";

	//postavljamo pool veza, gasimo ga prilikom gašenja aplikacije
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		//pitanje - zašto ResourceBundle ne radi?
//		ResourceBundle config = ResourceBundle.getBundle(sce.getServletContext().getRealPath("WEB-INF/dbsettings"));
		Properties config = new Properties();

		try(FileInputStream is = new FileInputStream(sce.getServletContext().getRealPath("WEB-INF/dbsettings.properties"))){
			config.load(is);
		}catch(Exception e) {
			e.printStackTrace();
		}

		if (config.isEmpty()) {
			throw new RuntimeException("Error! Properties file not read correctly!");
		}
		
		
		String dbName = config.getProperty("name");
		String host = config.getProperty("host");
		String port = config.getProperty("port");
		String user = config.getProperty("user");
		String password = config.getProperty("password");
		
		String connectionURL = "jdbc:derby://" + host + ":" + port + "/" + dbName + ";user=" + user + ";password=" + password;
		System.out.println("Puni connection URL: " + connectionURL);

		ComboPooledDataSource cpds = new ComboPooledDataSource();
		cpds.setInitialPoolSize(3); //koliko veza prilikom uspostavljanja da pokrene
		
		try {
			cpds.setDriverClass("org.apache.derby.client.ClientAutoloadedDriver");
		} catch (PropertyVetoException e1) {
			throw new RuntimeException("Pogreška prilikom inicijalizacije poola.", e1);
		}
		cpds.setJdbcUrl(connectionURL);
		

		sce.getServletContext().setAttribute("hr.fer.zemris.dbpool", cpds);
		initTables(cpds, config.getProperty("user"));
		
	}

	private static void initTables(DataSource dataSource, String schemaName) {
		try(Connection con = dataSource.getConnection()){
			String createPollsTableString = "CREATE TABLE Polls" +
					 "(id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
							 "title VARCHAR(150) NOT NULL,"+
							 "message CLOB(2048) NOT NULL)";
			
			String createPollsOptionsTableString = "CREATE TABLE PollOptions"+
					 "(id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"+
							 "optionTitle VARCHAR(100) NOT NULL,"+
							 "optionLink VARCHAR(150) NOT NULL,"+
							 "pollID BIGINT,"+
							 "votesCount BIGINT,"+
							 "FOREIGN KEY (pollID) REFERENCES Polls(id))";

			
			//provjeravamo postoji li tablica Polls te ju stvaramo ako ne postoji
			try(ResultSet res = con.getMetaData().getTables(null, schemaName.toUpperCase(), "Polls".toUpperCase(), null)){
				if (res.next()) {
					//sve ok, ne treba ništa raditi
				}else {
					try(PreparedStatement pst = con.prepareStatement(createPollsTableString)){
						pst.executeUpdate();
						System.out.println("Stvaram tablicu Polls");
					}
				}
			}catch (SQLException e) {
				System.out.println("Greška prilikom provjere/inicijalizacije tablice PollsOptions!");
				e.printStackTrace();
			}
			
			//provjeravamo postoji li tablica PollsOptions i stvaramo ju ako ne postoji
			try(ResultSet res = con.getMetaData().getTables(null, schemaName.toUpperCase(), "PollOptions".toUpperCase(), null)){
				if (res.next()) {
					//sve ok, ne treba ništa raditi
				}else {
					try(PreparedStatement pst = con.prepareStatement(createPollsOptionsTableString)){
						pst.executeUpdate();
						System.out.println("Stvaram tablicu PollsOptions");
					}
				}
			}catch (SQLException e) {
				System.out.println("Greška prilikom provjere/inicijalizacije tablice PollsOptions!");
				e.printStackTrace();
			}
			
			
			try(PreparedStatement pst = con.prepareStatement("SELECT COUNT(*) FROM POLLS")){
				ResultSet rset = pst.executeQuery();
				while(rset.next()) {
					long noOfSelected = rset.getLong(1);
					if (noOfSelected == 0) {
						System.out.println("Tablice su prazne, pozivam metodu za punjenje tablica!");
						populateTables(dataSource);
					}
				}
			}catch (SQLException e) {
				e.printStackTrace();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}


		
		
		
		
	}
	
	//ovo izdvoji u zaseban razred...
	public static void populateTables(DataSource dataSource) {
		

		try(Connection con = dataSource.getConnection()){
			try(PreparedStatement pstPolls = con.prepareStatement("INSERT INTO Polls (title, message) VALUES (?,?)",Statement.RETURN_GENERATED_KEYS);
					PreparedStatement pstPollsOptions = con.prepareStatement("INSERT INTO PollOptions (optionTitle, optionLink, pollID, votesCount) VALUES (?,?,?,?)")) {
				pstPolls.setString(1, "Glasanje za omiljeni bend");
				pstPolls.setString(2, "Od sljedećih bendova, koji vam je najdraži? Kliknite na link kako biste glasali!");
				pstPolls.executeUpdate();
				
				
				try(ResultSet rset = pstPolls.getGeneratedKeys()){
					rset.next();
					long pollID = rset.getLong(1);
					System.out.println("Unos nove ankete (bendovi) je obavljen i podaci su pohranjeni pod ključem " + Long.toString(pollID));
					
					int counter = 0;
					
					pstPollsOptions.setString(1, "Arctic Monkeys");
					pstPollsOptions.setString(2, "https://www.youtube.com/watch?v=R7A1mIdiheE");
					pstPollsOptions.setLong(3, pollID);
					pstPollsOptions.setLong(4, 505);
					counter += pstPollsOptions.executeUpdate();
					
					pstPollsOptions.setString(1, "Daft Punk");
					pstPollsOptions.setString(2, "https://www.youtube.com/watch?v=a5uQMwRMHcs");
					pstPollsOptions.setLong(3, pollID);
					pstPollsOptions.setLong(4, 500);
					counter += pstPollsOptions.executeUpdate();
					
					pstPollsOptions.setString(1, "Red Hot Chilli Peppers");
					pstPollsOptions.setString(2, "https://www.youtube.com/watch?v=rn_YodiJO6k");
					pstPollsOptions.setLong(3, pollID);
					pstPollsOptions.setLong(4, 490);
					counter += pstPollsOptions.executeUpdate();
					
					pstPollsOptions.setString(1, "The Weeknd");
					pstPollsOptions.setString(2, "https://www.youtube.com/watch?v=fHI8X4OXluQ");
					pstPollsOptions.setLong(3, pollID);
					pstPollsOptions.setLong(4, 220);
					counter += pstPollsOptions.executeUpdate();
					
					pstPollsOptions.setString(1, "Buč Kesidi");
					pstPollsOptions.setString(2, "https://www.youtube.com/watch?v=81SmEWDYvPA");
					pstPollsOptions.setLong(3, pollID);
					pstPollsOptions.setLong(4, 300);
					counter += pstPollsOptions.executeUpdate();
					
					pstPollsOptions.setString(1, "Nothing But Thieves");
					pstPollsOptions.setString(2, "https://www.youtube.com/watch?v=ZHv0OFyb-aM");
					pstPollsOptions.setLong(3, pollID);
					pstPollsOptions.setLong(4, 504);
					counter += pstPollsOptions.executeUpdate();
					
					pstPollsOptions.setString(1, "Simply Red");
					pstPollsOptions.setString(2, "https://www.youtube.com/watch?v=zTcu7MCtuTs");
					pstPollsOptions.setLong(3, pollID);
					pstPollsOptions.setLong(4, 100);
					counter += pstPollsOptions.executeUpdate();
					
					pstPollsOptions.setString(1, "Green Day");
					pstPollsOptions.setString(2, "https://www.youtube.com/watch?v=Soa3gO7tL-c");
					pstPollsOptions.setLong(3, pollID);
					pstPollsOptions.setLong(4, 100);
					counter += pstPollsOptions.executeUpdate();
					
					if (counter != 8) {
						throw new SQLException("Error! Some bend poll options haven't been correctly inserted!");
					}
					
					System.out.println("Unos opcija za anketu za bendove uspješan!");
					
				}catch (SQLException e) {
					throw new SQLException("Greška prilikom unosa u tablicu PollsOptions!");
				}
				
				
				pstPolls.setString(1, "Glasanje za omiljeni predmet!");
				pstPolls.setString(2, "Od sljedećih predmeta, koji vam je najdraži? Kliknite na link kako biste glasali!");
				pstPolls.executeUpdate();
				
				try(ResultSet rset = pstPolls.getGeneratedKeys()){
					rset.next();
					long pollID = rset.getLong(1);
					System.out.println("Unos nove ankete (predmeti) je obavljen i podaci su pohranjeni pod ključem " + Long.toString(pollID));
					
					int counter = 0;
					
					pstPollsOptions.setString(1, "Odabrana poglavlja razvoja programske potpore (1 i 2!)");
					pstPollsOptions.setString(2, "http://java.zemris.fer.hr/home/");
					pstPollsOptions.setLong(3, pollID);
					pstPollsOptions.setLong(4, 200);
					counter += pstPollsOptions.executeUpdate();
					
					pstPollsOptions.setString(1, "Umjetna inteligencija");
					pstPollsOptions.setString(2, "https://www.fer.unizg.hr/predmet/umjint");
					pstPollsOptions.setLong(3, pollID);
					pstPollsOptions.setLong(4, 199);
					counter += pstPollsOptions.executeUpdate();
					
					pstPollsOptions.setString(1, "Statistička analiza podataka");
					pstPollsOptions.setString(2, "https://www.fer.unizg.hr/predmet/sap_a");
					pstPollsOptions.setLong(3, pollID);
					pstPollsOptions.setLong(4, 150);
					counter += pstPollsOptions.executeUpdate();
					
					pstPollsOptions.setString(1, "Operacijski sustavi");
					pstPollsOptions.setString(2, "https://www.fer.unizg.hr/predmet/opsus");
					pstPollsOptions.setLong(3, pollID);
					pstPollsOptions.setLong(4, 150);
					counter += pstPollsOptions.executeUpdate();
					
					pstPollsOptions.setString(1, "Matematička analiza 1 i 2");
					pstPollsOptions.setString(2, "https://www.fer.unizg.hr/predmet/matan1");
					pstPollsOptions.setLong(3, pollID);
					pstPollsOptions.setLong(4, 170);
					counter += pstPollsOptions.executeUpdate();
					
					
					if (counter != 5) {
						throw new SQLException("Error! Some subjects poll options haven't been correctly inserted!");
					}
					
					System.out.println("Unos opcija za anketu za predmete uspješan!");
					
				}catch (SQLException e) {
					throw new SQLException("Greška prilikom unosa u tablicu PollsOptions!");
				}
				
				
				
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}


		
	}

	

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ComboPooledDataSource cpds = (ComboPooledDataSource)sce.getServletContext().getAttribute("hr.fer.zemris.dbpool");
		if(cpds!=null) {
			try {
				DataSources.destroy(cpds);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
