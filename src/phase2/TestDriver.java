package phase2;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;

public class TestDriver {
    private static Connector2 con = null;
    private static  BufferedReader in;
    /*Global app data*/
    private static String loggedInUsername;
    private static boolean loggedInIsDriver;

    /**Start of the Application*/
    public static void main(String[] args) {
        try {
            in = new BufferedReader(new InputStreamReader(System.in));
            connectToDB();
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

    /**
     * The first menu seen when the application is launced
     * */
    private static void mainMenu() throws Exception {
        String choice;
        String sql;
        try {
            // main menu structure
            System.out.println("        Welcome to Team 61's Uber App!     ");
            System.out.println("0. enter your own query"); //TODO: Eventually Remove this option
            System.out.println("1. driver login");
            System.out.println("2. user login");
            System.out.println("3. register account");
            System.out.println("4. exit");
            System.out.println("please enter your choice:");

            while ((choice = in.readLine()) == null && choice.length() == 0) ;

            switch (choice) {
                case "0": //TODO: Eventually remove this option
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
                    loginMenu("UberDriver");
                    return;
                case "2":
                    loginMenu("UberUser");
                    break;
                case "3":
                    registerMenu();
                    break;
                case "4":
                    System.out.println("Exiting Application...");
                    con.stmt.close();
                    break;
                default:
                    System.out.println("Invalid Selection...");
                    mainMenu();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error executing query.");
            throw (e);
        }
    }

    /**
     * Menu responsible for guiding the user's logging in
     * */
    private static void loginMenu(String table) throws Exception {
        DbUserService service = new DbUserService();
        String login;
        String password;

        System.out.println("          " + table + " Login          ");
        System.out.println("Enter your username (or type 'back' to go back):");
        while ((login = in.readLine()) == null && login.length() == 0) ;

        if(login.toLowerCase().trim().equals("back")) { mainMenu(); return; } // check if they want to go back

        System.out.println("Enter your password:");
        while ((password = in.readLine()) == null && password.length() == 0) ;

        if (service.attemptToLogIn(con.stmt, login, password, table)) {
            System.out.println("Login Successful. Welcome " + login);
            loggedInUsername = login;
            loggedInIsDriver = table.equals("UberDriver");

            if(loggedInIsDriver){
                driverLandingMenu();
            } else {
                userLandingMenu();
            }

        } else {
            System.out.println("Invalid Credentials");
            loginMenu(table);
        }
    }

    /**Menu responsible for registering the user*/
    private static void registerMenu() throws Exception {
        String choice;
        //register menu structure
        System.out.println("        Select Account Type     ");
        System.out.println("1. driver");
        System.out.println("2. user");
        System.out.println("3. back to main menu");
        System.out.println("please enter your choice:");

        while ((choice = in.readLine()) == null && choice.length() == 0) ;

        switch (choice) {
            case "1":
                registerUser("UberDriver");
                break;
            case "2":
                registerUser("UberUser");
                break;
            case "3":
                mainMenu();
                break;
            default:
                System.out.println("Invalid Selection...");
                registerMenu();
                break;
        }
    }

    /**
     * Console prompts asking for information required for registration
     * then adds the user to the database
     * */
    private static void registerUser(String table) throws Exception {
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
            System.out.println("Type 'c' and hit enter at any point to cancel registration");
            System.out.println("Enter a username:");
            while ((login = in.readLine()) == null && login.length() == 0);
            if(isCancelRegistration(login)) { return; } // check if they want to cancel
            foundUniqueLogin = service.isLoginAvailable(con.stmt, login, table);
            if (!foundUniqueLogin)
                System.out.println("Sorry, that username is already taken");
        }

        System.out.println("Enter a password:");
        while ((password = in.readLine()) == null && password.length() == 0);
        if(isCancelRegistration(password)) { return; }
        System.out.println("Enter your name:");
        while ((name = in.readLine()) == null && name.length() == 0);
        if(isCancelRegistration(name)) { return; }
        System.out.println("Enter an address:");
        while ((address = in.readLine()) == null && address.length() == 0);
        if(isCancelRegistration(address)) { return; }
        System.out.println("Enter a phone number:");
        while ((phone = in.readLine()) == null && phone.length() == 0);
        if(isCancelRegistration(phone)) { return; }
        service.createUser(con.stmt, login, password, name, address, phone, table);
        mainMenu();
    }

    /**Helper method for when user wants to cancel while registering */
    private static boolean isCancelRegistration(String input) throws Exception{
        if(input.toLowerCase().trim().equals("c")) {
            System.out.println("Registration cancelled...");
            mainMenu();
            return true;
        }
        return false;
    }
    /**
     * Connects the application to our Database
     * */
    private static void connectToDB() throws Exception {
        String hostname;
        String username;
        String password;
        String dbName;

        File dbCreds = new File("resources/dbCreds");
        Scanner sc = new Scanner(dbCreds);

        hostname = sc.next();
        username = sc.next();
        password = sc.next();
        dbName = sc.next();

        try {
            con = new Connector2(hostname, username, password, dbName);
            System.out.println("Database connection established");
            mainMenu();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error while attempting to connect to database.");
            throw (e);
        }
    }

    private static void driverLandingMenu() throws Exception{
        String choice;
        //driverLandingMenu structure
        System.out.println("         You are logged in as a Driver         ");
        System.out.println("1. add or update car");
        System.out.println("2. remove car");
        System.out.println("3. add or update hours of operation");
        System.out.println("4. log out");
        System.out.println("please enter your choice:");

        while ((choice = in.readLine()) == null && choice.length() == 0) ;

        switch (choice){
            case "1":
                //TODO: add or update car menu
                break;
            case "2":
                //TODO: remove car menu
                break;
            case "3":
                //TODO: add/update hours of operation
                break;
            case "4":
                logOut();
                break;
            default:
                System.out.println("Invalid Selection...");
                driverLandingMenu();
                break;
        }
    }

    private static void userLandingMenu() throws Exception {
        String choice;
        //userLandingMenu structure
        System.out.println("         You are logged in as a User         ");
        System.out.println("1. find a ride now");
        System.out.println("2. reserve a ride for later");
        System.out.println("3. browse cars");
        System.out.println("4. declare a car as your favorite");
        System.out.println("5. leave a review for a car");
        System.out.println("6. score someone else's review");
        System.out.println("7. view most useful reviews");
        System.out.println("8. declare other users as (un)trustworthy");
        System.out.println("9. check 2 degrees of separation");
        System.out.println("10. view statistics menu");
        System.out.println("11. log out");
        System.out.println("please enter your choice:");

        while ((choice = in.readLine()) == null && choice.length() == 0) ;

        switch (choice){
            case "1":
                //TODO: find a ride menu w/confirmation
                break;
            case "2":
                //TODO: reserve a car menu w/ confirmation
                break;
            case "3":
                //TODO: browse cars menu
                browseCars();
                break;
            case "4":
                //TODO: favorite car menu
                break;
            case "5":
                //TODO: review a car menu
                break;
            case "6":
                //TODO: score someone's review menu
                break;
            case "7":
                //TODO: return most useful reviews
                userLandingMenu();
                break;
            case "8":
                //TODO: trust menu
                break;
            case "9":
                //TODO: return degrees of separation
                userLandingMenu();
                break;
            case "10":
                //TODO: statistics menu
                break;
            case "11":
                logOut();
                break;
            default:
                System.out.println("Invalid Selection...");
                userLandingMenu();
                break;
        }
    }

    private static void browseCars() throws Exception{
        DbUserService service = new DbUserService();

        String category = "invalid";
        String model = "invalid";
        String address = "invalid";

        System.out.println("          UberCar Browsing          ");
        while(category.equals("invalid")) {category = browseCarCategory(); }
        if(isCancelBrowseCars(category)) { return; }

        while(model.equals("invalid")) {model = browseCarModel(); }
        if(isCancelBrowseCars(model)) { return; }

        while(address.equals("invalid")) {address = browseCarAddress(); }
        if(isCancelBrowseCars(address)) { return; }

        browseCarAndOrExecute(category, model, address);


    }

    private static String browseCarCategory() throws Exception{
        String choice;
        System.out.println("Select a category:");
        System.out.println("1. economy");
        System.out.println("2. comfort");
        System.out.println("3. luxury");
        System.out.println("4. any");
        System.out.println("5. quit browsing");
        System.out.println("please enter your choice:");

        while ((choice = in.readLine()) == null && choice.length() == 0);

        switch (choice) {
            case "1":
                return "economy";
            case "2":
                return "comfort";
            case "3":
                return "luxury";
            case "4":
                return "";
            case "5":
                return "c";
            default:
                System.out.println("Invalid Selection...");
                browseCarCategory();
                return "invalid";
        }
    }

    private static void browseCarAndOrExecute(String category, String model, String address) throws Exception {
        String choice;
        String displayCategory = category.isEmpty() ? "any category" : category;
        String displayModel = model.isEmpty() ? "any model" : model;
        String displayAddress = address.isEmpty() ? "any city/state" : address;
        System.out.println("Select how to combine your filters:");
        System.out.println("1. " + displayCategory + " AND is a(n) " + displayModel + " AND is in " + displayAddress);
        System.out.println("2. " + displayCategory + " AND is a(n) " + displayModel + " OR is in " + displayAddress);
        System.out.println("3. " + displayCategory + " OR is a(n) " + displayModel + " OR is in " + displayAddress);
        System.out.println("4. " + displayCategory + " AND is in " + displayAddress + " OR is a(n)" + displayModel);
        System.out.println("5. quit browsing");
        System.out.println("please enter your choice:");

        while ((choice = in.readLine()) == null && choice.length() == 0);

        switch (choice) {
            case "1":
                break;
            case "2":
                break;
            case "3":
                break;
            case "4":
                break;
            case "5":
                userLandingMenu();
                return;
            default:
                System.out.println("Invalid Selection...");
                browseCarCategory();
        }
    }

    private static String browseCarModel() throws Exception {
        String choice;
        String model;
        System.out.println("Select a model:");
        System.out.println("1. enter model name");
        System.out.println("2. any");
        System.out.println("3. quit browsing");
        System.out.println("please enter your choice:");

        while ((choice = in.readLine()) == null && choice.length() == 0);

        switch (choice) {
            case "1":
                System.out.println("Enter Model Name:");
                while ((model = in.readLine()) == null && model.length() == 0);
                return model;
            case "2":
                return "";
            case "3":
                return "c";
            default:
                System.out.println("Invalid Selection...");
                browseCarModel();
                return "invalid";
        }

    }

    private static String browseCarAddress() throws Exception {
        String choice;
        String address;
        System.out.println("Select a City or State:");
        System.out.println("1. enter city or state");
        System.out.println("2. any");
        System.out.println("3. quit browsing");
        System.out.println("please enter your choice:");

        while ((choice = in.readLine()) == null && choice.length() == 0);

        switch (choice) {
            case "1":
                System.out.println("Enter City or State Name:");
                while ((address = in.readLine()) == null && address.length() == 0);
                return address;
            case "2":
                return "";
            case "3":
                return "c";
            default:
                System.out.println("Invalid Selection...");
                browseCarAddress();
                return "invalid";
        }
    }
    /**Helper method for when user wants to cancel while browsing cars*/
    private static boolean isCancelBrowseCars(String input) throws Exception{
        if(input.toLowerCase().trim().equals("c")) {
            System.out.println("Browsing cancelled...");
            userLandingMenu();
            return true;
        }
        return false;
    }
    private static void logOut() throws Exception{
        loggedInIsDriver = false;
        loggedInUsername = "";
        System.out.println("You have successfully logged out. See ya!");
        mainMenu();
    }
}
