package phase2;

import java.util.Scanner;

public class TestConnector {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String hostname;
		String username;
		String password;
		String dbname;

		Scanner sc = new Scanner(System.in);
		System.out.println("Enter server hostname: ");
		hostname = sc.next();
		System.out.println("Enter username: ");
		username = sc.next();
		System.out.println("Enter password: ");
		password = sc.next();
		System.out.println("Enter db name: ");
		dbname = sc.next();
		try{
			Connector con= new Connector(hostname, username, password, dbname);
			Order order= new Order();
			
			String result=order.getOrders("login", "user1", con.stmt);
			System.out.println(result);
			con.closeConnection();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
		}
	}

}
