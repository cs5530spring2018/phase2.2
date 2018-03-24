package phase2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;

public class TestDriver {
    private static Connector2 con = null;

    public static void main(String[] args) {
        try{
            connectToDB();
        } catch(Exception e) {
            System.out.println("Something went wrong");
        } finally {
            try {
                con.closeConnection();
            } catch (Exception e) {
                //Don't handle errors when closing connection
            }
        }
    }

    private static void connectToDB() throws Exception {
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

        try {
            con = new Connector2(hostname, username, password, dbName);
            System.out.println("Database connection established");
            selectionMenu();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println ("Error while attempting to connect to database.");
            throw(e);
        }
    }

    private static void selectionMenu() throws Exception {
        String choice;
        String sql;
        int c;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while(true) {
                displayMenu();
                while ((choice = in.readLine()) == null && choice.length() == 0);
                try{
                    c = Integer.parseInt(choice);
                }catch (Exception e) {
                    continue;
                }
                switch(c){
                    case 1:
                        System.out.println("Doing Nothing");
                        break;
                    case 2:
                        System.out.println("please enter your query below:");
                        while ((sql = in.readLine()) == null && sql.length() == 0)
                            System.out.println(sql);
                        ResultSet rs = con.stmt.executeQuery(sql);
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int numCols = rsmd.getColumnCount();
                        while (rs.next()) {
                            for (int i=1; i<=numCols;i++) {
                                System.out.print(rs.getString(i) + "  ");
                            }
                            System.out.println("");
                        }
                        System.out.println(" ");
                        rs.close();
                        break;
                    case 3:
                        System.out.println("EoM");
                        con.stmt.close();
                        return;
                    default:
                        System.out.println("Invalid selection");
                        continue;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println ("Error executing query.");
            throw(e);
        }
    }

    private static void displayMenu() {
        System.out.println("        Welcome to UUber System     ");
        System.out.println("1. do nothing:");
        System.out.println("2. enter your own query:");
        System.out.println("3. exit:");
        System.out.println("please enter your choice:");
    }

}
