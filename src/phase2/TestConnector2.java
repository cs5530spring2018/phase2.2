package phase2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;

public class TestConnector2 {

	/**
	 * @param args
	 */
	public static void displayMenu()
	{
		 System.out.println("        Welcome to UUber System     ");
    	 System.out.println("1. search a course by cname and dname:");
    	 System.out.println("2. enter your own query:");
    	 System.out.println("3. exit:");
    	 System.out.println("pleasse enter your choice:");
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Example for cs5530");
		Connector2 con=null;
		String choice;
        String cname;
        String dname;
        String sql=null;
        int c=0;

		String hostname;
		String username;
		String password;
		String dbName;

		Scanner sc = new Scanner(System.in);
		System.out.println("Enter server hostname: ");
		hostname = sc.next();
		System.out.println("Enter username: ");
		username = sc.next();
		System.out.println("Enter password: ");
		password = sc.next();
		System.out.println("Enter db name: ");
		dbName = sc.next();

         try
		 {
			//remember to replace the password
			 	 con= new Connector2(hostname, username, password, dbName);
	             System.out.println ("Database connection established");
	         
	             BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	             
	             while(true)
	             {
	            	 displayMenu();
	            	 while ((choice = in.readLine()) == null && choice.length() == 0);
	            	 try{
	            		 c = Integer.parseInt(choice);
	            	 }catch (Exception e)
	            	 {
	            		 
	            		 continue;
	            	 }
	            	 if (c<1 | c>3)
	            		 continue;
	            	 if (c==1)
	            	 {
	            		 System.out.println("please enter a cname:");
	            		 while ((cname = in.readLine()) == null && cname.length() == 0);
	            		 System.out.println("please enter a dname:");
	            		 while ((dname = in.readLine()) == null && dname.length() == 0);
	            		 Course course=new Course();
	            		 System.out.println(course.getCourse(cname, dname, con.stmt));
	            	 }
	            	 else if (c==2)
	            	 {	 
	            		 System.out.println("please enter your query below:");
	            		 while ((sql = in.readLine()) == null && sql.length() == 0)
	            			 System.out.println(sql);
	            		 ResultSet rs=con.stmt.executeQuery(sql);
	            		 ResultSetMetaData rsmd = rs.getMetaData();
	            		 int numCols = rsmd.getColumnCount();
	            		 while (rs.next())
	            		 {
	            			 //System.out.print("cname:");
	            			 for (int i=1; i<=numCols;i++)
	            				 System.out.print(rs.getString(i)+"  ");
	            			 System.out.println("");
	            		 }
	            		 System.out.println(" ");
	            		 rs.close();
	            	 }
	            	 else
	            	 {   
	            		 System.out.println("EoM");
	            		 con.stmt.close(); 
	        
	            		 break;
	            	 }
	             }
		 }
         catch (Exception e)
         {
        	 e.printStackTrace();
        	 System.err.println ("Either connection error or query execution error!");
         }
         finally
         {
        	 if (con != null)
        	 {
        		 try
        		 {
        			 con.closeConnection();
        			 System.out.println ("Database connection terminated");
        		 }
        	 
        		 catch (Exception e) { /* ignore close errors */ }
        	 }	 
         }
	}
}
