package phase2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;

public class TestDriver {
    private static Connector2 con = null;

    public static void main(String[] args) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            connectToDB(in);
        } catch (Exception e) {
            System.out.println("Something went wrong");
        } finally {
            try {
                System.out.println("Closing connection");
                con.stmt.close();
                con.closeConnection();
            } catch (Exception e) {
                //Don't handle errors when closing connection
            }
        }
    }

    //TODO: Bypass this step eventually
    private static void connectToDB(BufferedReader in) throws Exception {
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
            mainMenu(in);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error while attempting to connect to database.");
            throw (e);
        }
    }

    private static void registerMenu(BufferedReader in) throws Exception {
        String choice;
        System.out.println("        Select Account Type     ");
        System.out.println("1. driver");
        System.out.println("2. user");
        System.out.println("3. back to main menu");
        System.out.println("please enter your choice:");
        while ((choice = in.readLine()) == null && choice.length() == 0) ;

        switch (choice) {
            case "1":
                registerUser(in, "UberDriver");
                break;
            case "2":
                registerUser(in, "UberUser");
                break;
            case "3":
                mainMenu(in);
                break;
            default:
                System.out.println("Invalid Selection...");
                registerMenu(in);
                break;
        }
    }

    private static void mainMenu(BufferedReader in) throws Exception {
        String choice;
        String sql;
        try {
            displayMainMenu();
            while ((choice = in.readLine()) == null && choice.length() == 0) ;

            switch (choice) {
                case "0":
                    System.out.println("please enter your query below:");
                    while ((sql = in.readLine()) == null && sql.length() == 0)
                        System.out.println(sql);
                    ResultSet rs = con.stmt.executeQuery(sql);
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int numCols = rsmd.getColumnCount();
                    while (rs.next()) {
                        for (int i = 1; i <= numCols; i++) {
                            System.out.print(rs.getString(i) + "  ");
                        }
                        System.out.println("");
                    }
                    System.out.println(" ");
                    rs.close();
                    break;
                case "1":
                    System.out.println("EoM");
                    con.stmt.close();
                    return;
                case "2":
                    loginMenu(in, "UberDriver");
                    break;
                case "3":
                    loginMenu(in, "UberUser");
                    break;
                case "4":
                    registerMenu(in);
                    break;
                default:
                    System.out.println("Invalid Selection...");
                    mainMenu(in);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error executing query.");
            throw (e);
        }
    }

    private static void displayMainMenu() {
        System.out.println("        Welcome to UUber System     ");
        System.out.println("0. enter your own query");
        System.out.println("1. exit");
        System.out.println("2. driver login");
        System.out.println("3. user login");
        System.out.println("4. register account");
        System.out.println("please enter your choice:");
    }

    private static void loginMenu(BufferedReader in, String table) throws Exception {
        DbUserService service = new DbUserService();
        String login;
        String password;
        System.out.println("          " + table + " Login          ");
        System.out.println("Enter your username:");
        while ((login = in.readLine()) == null && login.length() == 0) ;
        System.out.println("Enter your password:");
        while ((password = in.readLine()) == null && password.length() == 0) ;
        if (service.attemptToLogIn(con.stmt, login, password, table)) {
            //TODO: Redirect to appropriate login landing page. Save login credentials globally?
            System.out.println("Login Successful. Welcome " + login);
        } else {
            System.out.println("Invalid Credentials");
        }
    }

    /*private static void logIn(Statement stmt, BufferedReader in, String table) throws Exception {
        DbUserService service = new DbUserService();
        String login;
        String password;
        System.out.println("Enter your username:");
        while((login = in.readLine()) == null && login.length() == 0 );
        System.out.println("Enter your password:");
        while ((password = in.readLine()) == null && password.length() == 0);
        if(service.attemptToLogIn(stmt, login, password, table)){
            //TODO: Redirect to appropriate login landing page
        }

    }*/

    private static void registerUser(BufferedReader in, String table) throws Exception {
        DbUserService service = new DbUserService();
        String login = null;
        String password;
        String name;
        String address;
        String phone;
        // Check username for uniqueness
        Boolean foundUniqueLogin = false;
        System.out.println("          " + table + " Registration          ");
        while (!foundUniqueLogin) {
            System.out.println("Enter a username:");
            while ((login = in.readLine()) == null && login.length() == 0) ;
            foundUniqueLogin = service.isLoginAvailable(con.stmt, login, table);
            if (!foundUniqueLogin)
                System.out.println("Sorry, that username is already taken");
        }

        System.out.println("Enter a password:");
        while ((password = in.readLine()) == null && password.length() == 0) ;
        System.out.println("Enter your name:");
        while ((name = in.readLine()) == null && name.length() == 0) ;
        System.out.println("Enter an address:");
        while ((address = in.readLine()) == null && address.length() == 0) ;
        System.out.println("Enter a phone number:");
        while ((phone = in.readLine()) == null && phone.length() == 0) ;
        service.createUser(con.stmt, login, password, name, address, phone, table);
        mainMenu(in); //TODO: For now we redirect back to the main menu after registering
    }

}
