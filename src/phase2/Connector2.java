package phase2;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Connector2
{
	public Statement stmt;

	public Connection conn = null;
	public Session session = null;

	public Connector2(String sshHostname, String sshUsername, String sshPassword, String dbName) throws Exception
	{
		int lport = 5656;
		String rhost = "localhost";
		int rport = 3306;

		// ?allowMultiQueryies=true this allows for multiple queries to be sent in one string // YOU MAY NOT WANT THIS FUNCTIONALITY
		String url = "jdbc:mysql://localhost:" + lport + "/" + dbName + "?allowMultiQueries=true";
		String driverName = "com.mysql.jdbc.Driver";

		// Same as ssh creds in this case
		String dbUsername = sshUsername;
		String dbPassword = sshPassword;

		try
		{
			// Set StrictHostKeyChecking property to no to avoid UnknownHostKey
			// issue
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			JSch jsch = new JSch();
			session = jsch.getSession(sshUsername, sshHostname, 22);
			session.setPassword(sshPassword);
			session.setConfig(config);
			session.connect();
			System.out.println("Connected");

			int assigned_port = session.setPortForwardingL(lport, rhost, rport);
			System.out.println("localhost:" + assigned_port + " -> " + rhost + ":" + rport);
			System.out.println("Port Forwarded");
		
			// mysql database connectivity
			Class.forName(driverName).newInstance();
			conn = DriverManager.getConnection(url, dbUsername, dbPassword);
			stmt = conn.createStatement();

			System.out.println("Database connection established");
			System.out.println("DONE");
		} catch (SQLException sql)
		{
			sql.printStackTrace();
			throw sql;
		}
	}

	public void closeConnection() throws Exception
	{
		conn.close();
		session.disconnect();
	}
}
