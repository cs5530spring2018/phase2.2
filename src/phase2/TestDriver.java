package phase2;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        System.out.println("1. your UberCar menu");
        System.out.println("2. your HoursOfOp menu");
        System.out.println("3. log out");
        System.out.println("please enter your choice:");

        while ((choice = in.readLine()) == null && choice.length() == 0) ;

        switch (choice){
            case "1":
                driverUberCarMenu();
                break;
            case "2":
                driverHoursOfOpMenu();
                break;
            case "3":
                logOut();
                break;
            default:
                System.out.println("Invalid Selection...");
                driverLandingMenu();
                break;
        }
    }

    private static void driverHoursOfOpMenu() throws Exception {
        DbHoursOfOpService service = new DbHoursOfOpService();
        String choice;
        String startHour;
        String startMinutes;
        String finishHour;
        String finishMinutes;
        String day = "invalid";

        System.out.println("          Hours Of Operation Menu:          ");
        System.out.println("1. view your hours of operation");
        System.out.println("2. add hours of operation");
        System.out.println("3. remove hours of operation");
        System.out.println("4. leave Hours Of Operation Menu");
        System.out.println("please enter your choice:");

        while ((choice = in.readLine()) == null && choice.length() == 0);

        switch (choice) {
            case "1":
                System.out.println("          Hours of Operation for: " + loggedInUsername + "          ");
                System.out.println(service.printableHoursOfOp(service.fetchHoursOfOp(con.stmt, loggedInUsername)));
                driverHoursOfOpMenu();
                break;
            case "2":
                System.out.println("         New Hours Of Operation          ");
                System.out.println("Enter in the following Hours Of Operation Info");
                while(day.equals("invalid")) {day = selectDay(); }
                if(isCancelHoursOfOp(day)) { return; }

                System.out.println("Enter the starting hour (0-23) (or type '!c' to cancel):");
                while ((startHour = in.readLine()) == null && startHour.length() == 0);
                if(isCancelHoursOfOp(startHour)) { return; }

                System.out.println("Enter the starting minutes (0-59) (or type '!c' to cancel):");
                while ((startMinutes = in.readLine()) == null && startMinutes.length() == 0);
                if(isCancelHoursOfOp(startMinutes)) { return; }

                System.out.println("Enter the finishing hour (0-23) (or type '!c' to cancel):");
                while ((finishHour = in.readLine()) == null && finishHour.length() == 0);
                if(isCancelHoursOfOp(finishHour)) { return; }

                System.out.println("Enter the finishing minutes (0-59) (or type '!c' to cancel):");
                while ((finishMinutes = in.readLine()) == null && finishMinutes.length() == 0);
                if(isCancelHoursOfOp(finishMinutes)) { return; }

                service.createHoursOfOp(con.stmt, loggedInUsername, convertTime(startHour, startMinutes), convertTime( finishHour, finishMinutes), Integer.parseInt(day));
                driverHoursOfOpMenu();
                break;
            case "3":
                System.out.println("         Remove Hours Of Operation          ");
                System.out.println("Enter in the following Hours Of Operation Info");
                while(day.equals("invalid")) {day = selectDay(); }
                if(isCancelHoursOfOp(day)) { return; }

                System.out.println("Enter the starting hour (0-23) (or type '!c' to cancel):");
                while ((startHour = in.readLine()) == null && startHour.length() == 0);
                if(isCancelHoursOfOp(startHour)) { return; }

                System.out.println("Enter the starting minutes (0-59) (or type '!c' to cancel):");
                while ((startMinutes = in.readLine()) == null && startMinutes.length() == 0);
                if(isCancelHoursOfOp(startMinutes)) { return; }

                System.out.println("Enter the finishing hour (0-23) (or type '!c' to cancel):");
                while ((finishHour = in.readLine()) == null && finishHour.length() == 0);
                if(isCancelHoursOfOp(finishHour)) { return; }

                System.out.println("Enter the finishing minutes (0-59) (or type '!c' to cancel):");
                while ((finishMinutes = in.readLine()) == null && finishMinutes.length() == 0);
                if(isCancelHoursOfOp(finishMinutes)) { return; }

                service.removeHoursOfOp(con.stmt, loggedInUsername, convertTime(startHour, startMinutes), convertTime( finishHour, finishMinutes), Integer.parseInt(day));
                driverHoursOfOpMenu();
                break;
            case "4":
                driverLandingMenu();
                break;
            default:
                System.out.println("Invalid Selection...");
                driverHoursOfOpMenu();
        }
    }

    private static float convertTime(String hour, String minute) {
        float floatHour, floatMinute;
        try{
            floatHour = Float.parseFloat(hour);
            floatMinute = Float.parseFloat(minute);
        } catch (Exception e) {
            System.err.println("Error converting time to float");
            return -1f;
        }
        return round(floatHour + (floatMinute/60f), 2);
    }

    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    private static String selectDay() throws Exception {
        String choice;
        System.out.println("Select a day of the week:");
        System.out.println("1. sunday");
        System.out.println("2. monday");
        System.out.println("3. tuesday");
        System.out.println("4. wednesday");
        System.out.println("5. thursday");
        System.out.println("6. friday");
        System.out.println("7. saturday");
        System.out.println("8. quit");
        System.out.println("please enter your choice:");

        while ((choice = in.readLine()) == null && choice.length() == 0);

        switch (choice) {
            case "1":
                return "0";
            case "2":
                return "1";
            case "3":
                return "2";
            case "4":
                return "3";
            case "5":
                return "4";
            case "6":
                return "5";
            case "7":
                return "6";
            case "8":
                return "!c";
            default:
                System.out.println("Invalid Selection...");
                return "invalid";
        }
    }

    private static boolean isCancelHoursOfOp(String input) throws Exception{
        if(input.toLowerCase().trim().equals("!c")) {
            System.out.println("Browsing cancelled...");
            driverHoursOfOpMenu();
            return true;
        }
        return false;
    }

    private static void driverUberCarMenu() throws Exception {
        DbCarService service = new DbCarService();
        String choice;
        String vin;
        System.out.println("          UberCar Menu:          ");
        System.out.println("1. view your cars");
        System.out.println("2. add/update a car");
        System.out.println("3. remove existing car");
        System.out.println("4. leave UberCar Menu");
        System.out.println("please enter your choice:");

        while ((choice = in.readLine()) == null && choice.length() == 0);

        switch (choice) {
            case "1":
                System.out.println(service.printableCars(service.fetchUberCarsForDriver(con.stmt, loggedInUsername)));
                driverUberCarMenu();
                break;
            case "2":
                upsertCar();
                break;
            case "3":
                System.out.println("Enter Vin # of the car you want to remove (or type '!c' to cancel):");
                while ((vin = in.readLine()) == null && vin.length() == 0);
                if(isCancelCarMenu(vin)) { return; }
                service.removeUberCar(con.stmt, vin);
                driverUberCarMenu();
                break;
            case "4":
                driverLandingMenu();
                break;
            default:
                System.out.println("Invalid Selection...");
                driverUberCarMenu();
        }
    }

    private static void upsertCar() throws Exception {
        DbCarService service = new DbCarService();

        String vin;
        String category = "invalid";
        String make;
        String model;
        String year ;
        int yearConverted = 0;

        System.out.println("          Add/Update UberCar          ");

        System.out.println("Enter Vin # of your car (or type '!c' to cancel):");
        while ((vin = in.readLine()) == null && vin.length() == 0);
        if(isCancelCarMenu(vin)) { return; }

        while(category.equals("invalid")) {category = selectCategory(); }
        if(isCancelCarMenu(category)) { return; }

        System.out.println("Enter the make of your car (or type '!c' to cancel):");
        while ((make = in.readLine()) == null && make.length() == 0);
        if(isCancelCarMenu(make)) { return; }

        System.out.println("Enter the model of your car (or type '!c' to cancel):");
        while ((model = in.readLine()) == null && model.length() == 0);
        if(isCancelCarMenu(model)) { return; }

        System.out.println("Enter the year of your car (or type '!c' to cancel):");
        while ((year = in.readLine()) == null && year.length() == 0);
        if(isCancelCarMenu(year)) { return; }

        try{
            yearConverted = Integer.parseInt(year);
        } catch (Exception e) {
            System.out.println("Could not parse year to int.");
        }

        service.createUberCar(con.stmt, vin, loggedInUsername, category, make, model, yearConverted);
        driverUberCarMenu();
    }

    private static String selectCategory() throws Exception {
        String choice;
        System.out.println("Select a category:");
        System.out.println("1. economy");
        System.out.println("2. comfort");
        System.out.println("3. luxury");
        System.out.println("4. quit adding car");
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
                return "!c";
            default:
                System.out.println("Invalid Selection...");
                return "invalid";
        }
    }

    private static boolean isCancelCarMenu(String input) throws Exception{
        if(input.toLowerCase().trim().equals("!c")) {
            System.out.println("Cancelled...");
            driverUberCarMenu();
            return true;
        }
        return false;
    }

    private static void userLandingMenu() throws Exception {
        String choice;
        //userLandingMenu structure
        System.out.println("         You are logged in as a User         ");
        System.out.println("1. find a ride now");
        System.out.println("2. reserve a ride for later");
        System.out.println("3. browse cars");
        System.out.println("4. declare a car as your favorite");
        System.out.println("5. open reviews menu");
        System.out.println("6. score someone else's review");
        System.out.println("7. view most useful reviews");
        System.out.println("8. declare other users as (un)trustworthy");
        System.out.println("9. check degrees of separation");
        System.out.println("10. view statistics menu");
        System.out.println("11. log out");
        System.out.println("please enter your choice:");

        while ((choice = in.readLine()) == null && choice.length() == 0) ;

        switch (choice){
            case "1":
                //find a ride menu w/confirmation
                findRide(false);
                break;
            case "2":
                //reserve a car menu w/ confirmation
                findRide(true);
                break;
            case "3":
                //browse cars menu
                browseCars();
                break;
            case "4":
                favoritesMenu();
                userLandingMenu();
                break;
            case "5":
                //TODO: review a car menu
                reviewMenu();
                break;
            case "6":
                //TODO: score someone's review menu
                break;
            case "7":
                //TODO: return most useful reviews
                userLandingMenu();
                break;
            case "8":
                trustMenu();
                userLandingMenu();
                break;
            case "9":
                degreeMenu();
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

    private static void reviewMenu() throws Exception {
        DbCarFeedbackService carFeedbackService = new DbCarFeedbackService();
        String choice;
        String vin;
        System.out.println("          Review Menu:          ");
        System.out.println("1. view a car's reviews");
        System.out.println("2. leave a review for a car");
        System.out.println("3. score someone's review");
        System.out.println("4. view most useful reviews");
        System.out.println("5. leave Review Menu");
        System.out.println("please enter your choice:");

        while ((choice = in.readLine()) == null && choice.length() == 0);

        switch (choice) {
            case "1":
                System.out.println("Enter Vin # of the car whose reviews you want to see (or type '!c' to cancel):");
                while ((vin = in.readLine()) == null && vin.length() == 0);
                if(isCancelReview(vin)) { return; }
                System.out.println(carFeedbackService.printFeedBack(carFeedbackService.fetchFeedbackForCar(con.stmt, vin)));
                reviewMenu();
                break;
            case "2":
                reviewCar();
                break;
            case "3":
                scoreCarReview();
                break;
            case "4":
                break;
            case "5":
                userLandingMenu();
                break;
            default:
                System.out.println("Invalid Selection...");
                reviewMenu();
        }
    }

    private static void topNReviewsForDriver(){
        DbCarFeedbackService service = new DbCarFeedbackService();
        String driver;
        String numResults;
        System.out.println("Get the top N reviews ");
    }
    private static void reviewCar() throws Exception{
        DbCarFeedbackService service = new DbCarFeedbackService();
        String vin;
        String rating;
        String comment;
        System.out.println("Review a car:");
        System.out.println("Enter Vin # of the car you want to review (or type '!c' to cancel):");
        while ((vin = in.readLine()) == null && vin.length() == 0);
        if(isCancelReview(vin)) { return; }

        System.out.println("Enter a rating (0-10 where 0 is terrible, 10 is excellent) (or type '!c' to cancel):");
        while ((rating = in.readLine()) == null && rating.length() == 0);
        if(isCancelReview(rating)) { return; }

        System.out.println("[Optional] leave a comment (max 255 characters) or leave blank (or type '!c' to cancel):");
        while ((comment = in.readLine()) == null && comment.length() == 0);
        if(isCancelReview(comment)) { return; }

        try{
            service.createCarFeedback(con.stmt, loggedInUsername, vin, Integer.parseInt(rating), comment, LocalDateTime.now());
        } catch (Exception e) {
            System.err.println("Invalid input for review!");
            reviewMenu();
        }
        reviewMenu();
    }

    private static void scoreCarReview() throws  Exception {
        DbScoredFeedbackService service = new DbScoredFeedbackService();
        String vin;
        String reviewee;
        String score;
        System.out.println("Score a review:");
        System.out.println("Enter Vin # of the car in the review you want to score (or type '!c' to cancel):");
        while ((vin = in.readLine()) == null && vin.length() == 0);
        if(isCancelReview(vin)) { return; }

        System.out.println("Enter the name of user who left the review you want to score (or type '!c' to cancel):");
        while ((reviewee = in.readLine()) == null && reviewee.length() == 0);
        if(isCancelReview(reviewee)) { return; }
        if(reviewee.equals(loggedInUsername)){
            System.out.println("You cannot score your own review!");
            reviewMenu();
            return;
        }

        System.out.println("Enter the score for the review (0, 1, or 2 where 0 is 'useless', 1 is 'useful', 2 is 'very useful') (or type '!c' to cancel):");
        while ((score = in.readLine()) == null && score.length() == 0);
        if(isCancelReview(score)) { return; }

        try {
            service.createScoredFeedback(con.stmt, reviewee, vin, loggedInUsername, Integer.parseInt(score));
        } catch (Exception e) {
            System.err.println("Invalid input for a review score!");
            reviewMenu();
            return;
        }
        reviewMenu();
    }
    private static boolean isCancelReview(String input) throws Exception {
        if(input.toLowerCase().trim().equals("!c")) {
            System.out.println("Cancelled...");
            reviewMenu();
            return true;
        }
        return false;
    }
    private static LocalDateTime chooseDate() throws Exception{
        String year, month, dayOfMonth, hour, minute;
        LocalDateTime date = null;
        System.out.println("Choose the date for your reservation: ");
        System.out.println("What year (YYYY) is the reservation for? (type '!c' to cancel)");
        while ((year = in.readLine()) == null && year.length() == 0);
        if(isCancelRide(year)) { return null; }

        System.out.println("What month (1-12) is the reservation for? (type '!c' to cancel)");
        while ((month = in.readLine()) == null && month.length() == 0);
        if(isCancelRide(month)) { return null; }

        System.out.println("What day (1-31) is the reservation for? (type '!c' to cancel)");
        while ((dayOfMonth = in.readLine()) == null && dayOfMonth.length() == 0);
        if(isCancelRide(dayOfMonth)) { return null; }

        System.out.println("What hour (0-23) is the reservation for? (type '!c' to cancel)");
        while ((hour = in.readLine()) == null && hour.length() == 0);
        if(isCancelRide(hour)) { return null; }

        System.out.println("What minute (0-59) is the reservation for? (type '!c' to cancel)");
        while ((minute = in.readLine()) == null && minute.length() == 0);
        if(isCancelRide(minute)) { return null; }

        try {
            date = LocalDateTime.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(dayOfMonth), Integer.parseInt(hour), Integer.parseInt(minute));
        } catch (Exception e) {
            System.err.println("Your Date input was invalid!");
            return null;
        }
        return date;
    }

    private static void findRide(boolean reserve) throws Exception{
        DbCarService carService = new DbCarService();
        DbRideService rideService = new DbRideService();
        DbReservationService reservationService = new DbReservationService();
        String vin;
        String num_riders;
        String distance;
        String to;
        String from;
        String confirm;
        LocalDateTime date = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        System.out.println("          Available Rides         ");
        if(reserve) {
            date = chooseDate();
            if(date == null) {
                return;
            }
            String availableCars = carService.printableCars(carService.availableCars(con.stmt, convertTime(Integer.toString(date.getHour()), Integer.toString(date.getMinute())),
                    dayOfTheWeekAdjuster(date.getDayOfWeek().getValue())));
            if(availableCars.trim().length() == 0) {
                String formattedDate = formatter.format(date);
                System.out.println("No available cars on: " + formattedDate);
                userLandingMenu();
                return;
            } else {
                System.out.println(availableCars);
            }
        } else {
            String availableCars = carService.printableCars(carService.availableCars(con.stmt, getNowTimeAsFloat(),
                    dayOfTheWeekAdjuster(LocalDateTime.now().getDayOfWeek().getValue())));
            if(availableCars.trim().length() == 0) {
                System.out.println("Sorry! No cars are available at the moment!");
                userLandingMenu();
                return;
            } else {
                System.out.println(availableCars);
            }
        }

        System.out.println("Select An Available Ride From Above by Entering its vin (type '!c' to cancel):");
        while ((vin = in.readLine()) == null && vin.length() == 0);
        if(isCancelRide(vin)) { return; }

        System.out.println("How many riders including you? (type '!c' to cancel):");
        while ((num_riders = in.readLine()) == null && num_riders.length() == 0);
        if(isCancelRide(num_riders)) { return; }

        System.out.println("How far are you going (in miles)? (type '!c' to cancel):");
        while ((distance = in.readLine()) == null && distance.length() == 0);
        if(isCancelRide(distance)) { return; }

        System.out.println("What's the address of where you are going? (type '!c' to cancel):");
        while ((to = in.readLine()) == null && to.length() == 0);
        if(isCancelRide(to)) { return; }

        System.out.println("Where are you going to be picked up? (type '!c' to cancel):");
        while ((from = in.readLine()) == null && from.length() == 0);
        if(isCancelRide(from)) { return; }

        if(reserve){
            String formattedDate = formatter.format(date);
            System.out.println("Confirm your Reservation with: ");
            System.out.println(carService.printableCars(carService.fetchUberCarDetails(con.stmt, vin)));
            System.out.println("On: " + formattedDate);
        } else {
            System.out.println("Confirm your Ride with: ");
            System.out.println(carService.printableCars(carService.fetchUberCarDetails(con.stmt, vin)));
            System.out.println("You are going from " + from + " to " + to + ". Distance of " + distance + " miles.");
            System.out.println("The total number of people in this ride is: " + num_riders);
            System.out.println("The total cost will be $" + distance);
        }
        System.out.println("1. Yes");
        System.out.println("2. No");
        System.out.println("please enter your choice:");
        while((confirm = in.readLine()) == null && confirm.length() == 0);
        switch (confirm){
            case "1":
                if(reserve){
                    try {
                        System.out.println("Adding Reservation...");
                        reservationService.createReservation(con.stmt, loggedInUsername, vin, date);
                    } catch (Exception e) {
                        System.err.println("Some of your input was invalid!");
                        userLandingMenu();
                        return;
                    }
                } else {
                    System.out.println("Adding Ride...");
                    try {
                        rideService.createRide(con.stmt, loggedInUsername, vin, Integer.parseInt(num_riders),
                                Double.parseDouble(distance), Double.parseDouble(distance), LocalDateTime.now(), to, from);
                    } catch (Exception e) {
                        System.err.println("Some of your input was invalid!");
                        userLandingMenu();
                        return;
                    }
                }
                break;
            case "2":
                System.out.println("Cancelling...");
                userLandingMenu();
                return;
        }
        userLandingMenu();
    }

    private static boolean isCancelRide(String input) throws Exception{
        if(input.toLowerCase().trim().equals("!c")) {
            System.out.println("Ride cancelled...");
            userLandingMenu();
            return true;
        }
        return false;
    }

    /**Our DB entries have dates 0 (Sun) - 6 (Sat)
     * LocalDateTime has dates 1 (Mon) - 7 (Sun)
     * This adjusts the date so Sun uses 0 instead of 7*/
    private static int dayOfTheWeekAdjuster(int dayOfTheWeek){
        if(dayOfTheWeek == 7) { return 0; }
        return dayOfTheWeek;
    }

    private static Float getNowTimeAsFloat() {
        LocalDateTime now = LocalDateTime.now();
        return convertTime(Integer.toString(now.getHour()), Integer.toString(now.getMinute()));
    }

    private static void browseCars() throws Exception{
        DbCarService service = new DbCarService();

        String category = "invalid";
        String model = "invalid";
        String address = "invalid";
        String andOrs = "invalid";
        String sort = "invalid";

        System.out.println("          UberCar Browsing          ");
        while(category.equals("invalid")) {category = browseCarCategory(); }
        if(isCancelBrowseCars(category)) { return; }

        while(model.equals("invalid")) {model = browseCarModel(); }
        if(isCancelBrowseCars(model)) { return; }

        while(address.equals("invalid")) {address = browseCarAddress(); }
        if(isCancelBrowseCars(address)) { return; }

        while(andOrs.equals("invalid")) { andOrs = browseCarAndOr(category, model, address); }
        if(isCancelBrowseCars(andOrs)) { return; }

        String[] andOrsSplit = andOrs.split("/");

        while(sort.equals("invalid")) { sort = browseCarSort(); }
        if(isCancelBrowseCars(sort)) { return; }

        System.out.println("          Browsing Results         ");
        System.out.println(service.printableCars(service.ucBrowser(con.stmt, category, andOrsSplit[0], model, andOrsSplit[1], address, sort)));
        userLandingMenu();
    }

    private static String browseCarSort() throws Exception {
        String choice;
        System.out.println("Select sorting method:");
        System.out.println("1. sort by average score of reviews");
        System.out.println("2. sort by average score of reviews left by trusted users");
        System.out.println("3. do not sort");
        System.out.println("4. quit browsing");

        while ((choice = in.readLine()) == null && choice.length() == 0);

        switch (choice) {
            case "1":
                return "a";
            case "2":
                return "b";
            case "3":
                return "";
            case "4":
                return "!c";
            default:
                System.out.println("Invalid Selection...");
                return "invalid";
        }

    }

    private static String browseCarCategory() throws Exception {
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
                return "!c";
            default:
                System.out.println("Invalid Selection...");
                return "invalid";
        }
    }

    private static String browseCarAndOr(String category, String model, String address) throws Exception {
        String choice;
        String displayCategory = category.isEmpty() ? "any category" : category;
        String displayModel = model.isEmpty() ? "any model" : model;
        String displayAddress = address.isEmpty() ? "any city/state" : address;
        System.out.println("Select how to combine your filters:");
        System.out.println("1. " + displayCategory + " AND is a(n) " + displayModel + " AND is in " + displayAddress);
        System.out.println("2. " + displayCategory + " AND is a(n) " + displayModel + " OR is in " + displayAddress);
        System.out.println("3. " + displayCategory + " OR is a(n) " + displayModel + " AND is in " + displayAddress);
        System.out.println("4. " + displayCategory + " OR is a(n) " + displayModel + " OR is in " + displayAddress);
        System.out.println("5. quit browsing");
        System.out.println("please enter your choice:");

        while ((choice = in.readLine()) == null && choice.length() == 0);

        switch (choice) {
            case "1":
                return "and/and";
            case "2":
                return "and/or";
            case "3":
                return "or/and";
            case "4":
                return "or/or";
            case "5":
                return "!c";
            default:
                System.out.println("Invalid Selection...");
                return "invalid";
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
                return "!c";
            default:
                System.out.println("Invalid Selection...");
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
                return "!c";
            default:
                System.out.println("Invalid Selection...");
                return "invalid";
        }
    }
    /**Helper method for when user wants to cancel while browsing cars*/
    private static boolean isCancelBrowseCars(String input) throws Exception{
        if(input.toLowerCase().trim().equals("!c")) {
            System.out.println("Browsing cancelled...");
            userLandingMenu();
            return true;
        }
        return false;
    }

    private static void favoritesMenu() throws Exception {
        String vin;
        DbFavoritesService fService = new DbFavoritesService();
        DbCarService cService = new DbCarService();
        System.out.println("Favorite a Car");
        System.out.println("Type 'back' to go back");
        System.out.println("Please enter a vin #: ");
        while ((vin = in.readLine()) == null && vin.length() == 0);
        try {
            if (vin.equals("back"))
                userLandingMenu();
            if (!cService.uberCarExists(con.stmt, vin)) {
                System.out.println("Sorry, car does not exists");
                favoritesMenu();
            }
            if (!fService.favoriteExists(con.stmt, loggedInUsername, vin))
                fService.createFavorite(con.stmt, loggedInUsername, vin);
        }
        catch(Exception e) {
            System.out.println("That car may not exist, please try again");
            favoritesMenu();
        }
    }

    private static void trustMenu() throws Exception {
        String username;
        String choice;
        DbTrustService tService = new DbTrustService();
        DbUserService uService = new DbUserService();
        System.out.println("Trust or Distrust a User");
        System.out.println("Type 'back' to go back");
        System.out.println("Please enter a username: ");
        while ((username = in.readLine()) == null && username.length() == 0);
        try {
            if (username.equals("back"))
                userLandingMenu();
            if (uService.isLoginAvailable(con.stmt, username, "UberUser")) {
                System.out.println("Sorry, user does not exists");
                trustMenu();
            }
            if (username.equals(loggedInUsername)) {
                System.out.println("Can't give yourself a rating!");
                trustMenu();
            }
            System.out.println("1. Trust User");
            System.out.println("2. Distrust User");
            System.out.println("Please enter you choice: ");
            while ((choice = in.readLine()) == null && choice.length() == 0);

            switch (choice) {
                case "1":
                    tService.createTrust(con.stmt, loggedInUsername, username, 1);
                    break;
                case "2":
                    tService.createTrust(con.stmt, loggedInUsername, username, 0);
                    break;
                default:
                    System.out.println("Invalid choice");
                    trustMenu();
                    break;
            }
        }
        catch(Exception e) {
            System.out.println("That car may not exist, please try again");
            favoritesMenu();
        }
    }

    private static void degreeMenu() {
        String username1 = "";
        String username2 = "";
        String choice;
        DbUserService uService = new DbUserService();
        DbStatisticsService sService = new DbStatisticsService();
        try {
            System.out.println("Degrees of Separation");
            System.out.println("Type 'back' to go back");
            System.out.println("1. Check you degree of separation with another user");
            System.out.println("2. Check 2 user's degree of separation");
            System.out.println("Please enter your choice");
            while ((choice = in.readLine()) == null && choice.length() == 0);

            switch (choice) {
                case "1":
                    username1 = loggedInUsername;
                    System.out.println("Enter the name of a user: ");
                    while ((username2 = in.readLine()) == null && username2.length() == 0);
                    break;
                case "2":
                    System.out.println("Enter the name of a user: ");
                    while ((username1 = in.readLine()) == null && username1.length() == 0);
                    System.out.println("Enter the name of a user: ");
                    while ((username2 = in.readLine()) == null && username2.length() == 0);
                    break;
                default:
                    System.out.println("Invalid choice");
                    degreeMenu();
                    break;
            }
            if (uService.isLoginAvailable(con.stmt, username1, "UberUser") ||
                    uService.isLoginAvailable(con.stmt, username2, "UberUser")) {
                System.out.println("One or more of those usernames does not exist, please try again");
                degreeMenu();
            }
            if (sService.twoDegreesOfSeparation(con.stmt, username1, username2))
                System.out.println("Two degrees of separation");
            else if (sService.oneDegreeOfSeparation(con.stmt, username1, username2))
                System.out.println("One degree of separation");
            else
                System.out.println("No degree of separation (1 or 2) was detected");
            System.out.println("Try different users?");
            System.out.println("1. yes");
            System.out.println("2. no");
            while ((choice = in.readLine()) == null && choice.length() == 0);
            if (choice.equals("1"))
                degreeMenu();
            userLandingMenu();
        }
        catch(Exception e) {
            System.out.println("There was an error querying for results");
            degreeMenu();
        }

    }

    private static void logOut() throws Exception{
        loggedInIsDriver = false;
        loggedInUsername = "";
        System.out.println("You have successfully logged out. See ya!");
        mainMenu();
    }
}
