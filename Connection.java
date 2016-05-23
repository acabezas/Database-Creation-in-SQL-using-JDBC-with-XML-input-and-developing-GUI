package CS430;

import java.sql.*;
import CS430.Parser;

public class Connection {
	
    Statement stmt;
    ResultSet rs;
    Connection con;

	/**
	 * @param args
	 */

	private boolean _connectDBServer() {
		
		boolean isConnected = true;
		
		try {

		    // Register the JDBC driver for MySQL.
		    Class.forName("com.mysql.jdbc.Driver");
		    
		    // Define URL of database server for
		    // database named 'user' on the faure.
		    String url = "jdbc:mysql://faure/aksdarak";
		    con = DriverManager.getConnection(url,"aksdarak", "830701950");
		    
		    // Get a connection to the database for a
		    // user named 'user' with the password
		    // 123456789.
		    System.out.println("URL: " + url);

		    // Display URL and connection information
		    
		    System.out.println("Connection: " + con);
		    
		    stmt = con.createStatement();
			
		    } catch (Exception e) {
		    	isConnected = false;
		    	System.out.println("Unable to connect DB server");
		    	e.printStackTrace();
		    }
		
		return isConnected;
	}
	
	
	private void Start(String[] args) {
		
		if (_connectDBServer()) {
			if(args.length >= 1) {
				new Parser(args[0], con).readXML();
			} else {
				System.out.println("XML file path should be first argument");
			}
			
		} else {
			System.out.println("Unable to connect the Server");
		}
	}
	
	public static void main(String[] args) {	
		Connection labMain = new Connection();
		labMain.Start(args);
		
	}

}
