package phase2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.lang.*;
import java.sql.*;
import java.util.Date;

public class InitializeDb {

    /**
     * @param args
     */
    public static void main(String[] args) {

        String hostname;
        String username;
        String password;
        String dbName;
        Connector2 con = null;
        ResultSet results;

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
            //System.out.println ("Database connection established");
            //System.out.println("Making users and drivers...");
            //makeUberUsers(con.stmt, 100);
            //makeUberDrivers(con.stmt, 100);

            //System.out.println("Creating vehicles...");
            //makeUberCars(con.stmt, 100);

            //System.out.println("Creating car feedback...");
            //makeCarFeedback(con.stmt, 100);

            //System.out.println("Creating feedback scores...");
            //makeFeebackScores(con.stmt, 100);

            //System.out.println("Creating trust records...");
            //makeTrustRecords(con.stmt, 100);

            //System.out.println("Creating favorites...");
            //makeFavorites(con.stmt, 100);

            //System.out.println("Creating hours of ops...");
            //makeHoursOfOp(con.stmt, 100);

            //System.out.println("Creating reservation records");
            //makeReservations(con.stmt, 100);

            //System.out.println("Creating ride records");
            //makeRides(con.stmt, 100);

            //System.out.println("Testing upsert");
            DbCarService carService = new DbCarService();
            //carService.createUberCar(con.stmt, "abcd0", "driverUsername0", "Truck", "Ford", "F-150", 2018);
            //ResultSet rset = carService.availableCars(con.stmt, 12.15f, 3);
            //System.out.println(carService.printableAvailableCars(rset));
            ResultSet rset = carService.ucBrowser(con.stmt, "Truck", "OR", "Ford", "AND", "UT", "a");
            System.out.println(carService.sortedDataToString(rset));

            String sql;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
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
                    con.stmt.close();
                    con.closeConnection();
                    System.out.println ("Database connection terminated");
                }

                catch (Exception e) { /* ignore close errors */ }
            }
        }
    }

    private static void makeUberUsers(Statement stmt, int numUsers) {
        DbUserService service = new DbUserService();
        String login = "username";
        String temp_user = login;
        String pw = "hunter2";
        String name = "UberUser";
        String temp_name = name;
        String address = "1234 Fake Street, Salt Lake City, UT 84105";
        String phone = "0123456789";
        for (int i=0; i<numUsers; i++) {
            temp_user = login + Integer.toString(i);
            temp_name = name + Integer.toString(i);
            service.createUser(stmt, temp_user, pw, temp_name, address, phone, "UberUser");
        }
    }

    private static void makeUberDrivers(Statement stmt, int numUsers) {
        DbUserService service = new DbUserService();
        String login = "driverUsername";
        String temp_user = login;
        String pw = "hunter2";
        String name = "UberDriver";
        String temp_name = name;
        String address = "1234 Fake Street, Salt Lake City, UT 84105";
        String phone = "0123456789";
        for (int i=0; i<numUsers; i++) {
            temp_user = login + Integer.toString(i);
            temp_name = name + Integer.toString(i);
            service.createUser(stmt, temp_user, pw, temp_name, address, phone, "UberDriver");
        }
    }

    private static void makeUberCars(Statement stmt, int numCars) throws Exception {
        String query;
        ResultSet results;
        String vin = "abcd";
        String vin2 = "efgh";
        String vin3 = "ijkl";
        String temp_vin = vin;
        String driver = "driverUsername";
        String temp_driver = driver;
        String[] categories = {"Sedan", "SUV", "Truck", "Coup"};
        String[] makes = {"Ford", "Toyota", "Subaru", "Tesla"};
        String[] models = {"F-150", "RAV-4", "Forester", "Model-T"};
        String category;
        String make;
        String model;
        int year = 2018;
        int index;
        DbCarService service = new DbCarService();
        for (int i=0; i<numCars; i++) {
            temp_vin = vin + Integer.toString(i);
            temp_driver = driver + Integer.toString(i);
            index = i % 4;
            category = categories[index];
            make = makes[index];
            model = models[index];
            service.createUberCar(stmt, temp_vin, temp_driver, category, make, model, year);
        }
        for (int i=0; i<numCars/2; i++) {
            temp_vin = vin2 + Integer.toString(i);
            temp_driver = driver + Integer.toString(i);
            index = i % 4;
            category = categories[index];
            make = makes[index];
            model = models[index];
            service.createUberCar(stmt, temp_vin, temp_driver, category, make, model, year);
        }
        for (int i=0; i<numCars/4; i++) {
            temp_vin = vin3 + Integer.toString(i);
            temp_driver = driver + Integer.toString(i);
            index = i % 4;
            category = categories[index];
            make = makes[index];
            model = models[index];
            service.createUberCar(stmt, temp_vin, temp_driver, category, make, model, year);
        }
    }

    private static void makeCarFeedback(Statement stmt, int numFeedback) {
        DbCarFeedbackService service = new DbCarFeedbackService();
        String reviewer = "username";
        String temp_reviewer;
        String car = "abcd";
        String temp_car;
        int rating;
        String comment;
        String[] comments = {"Good Driver", "Bad Driver", "Okay", "One of the best cars"};
        Date date = new Date();

        for (int i=0; i<numFeedback; i++) {
            temp_reviewer = reviewer + Integer.toString(i);
            temp_car = car + Integer.toString(i);
            rating = i % 10;
            comment = comments[i%4];
            if (i%5==0)
                comment = null;

            service.createCarFeedback(stmt, temp_reviewer, temp_car, rating, comment, date);
        }
    }

    private static void makeFeebackScores(Statement stmt, int numScores) {
        DbScoredFeedbackService service = new DbScoredFeedbackService();
        String reviewee = "username";
        String temp_reviewee;
        String car = "abcd";
        String temp_car;
        String temp_reviewer;
        int usefulness;
        
        for (int i=0; i<numScores/2-1; i++) {
            temp_reviewee = reviewee + Integer.toString(i);
            temp_car = car + Integer.toString(i);
            temp_reviewer = reviewee + Integer.toString(numScores-i-1);
            usefulness = i % 3;
            service.createScoredFeedback(stmt, temp_reviewee, temp_car, temp_reviewer, usefulness);
        }
    }

    private static void makeTrustRecords(Statement stmt, int numRecords) {
        DbTrustService service = new DbTrustService();
        String user = "username";
        String reviewer;
        String reviewee;
        int trust_score;

        for (int i=0; i<numRecords-1; i++) {
            reviewer = user + Integer.toString(i);
            reviewee = user + Integer.toString(numRecords-i-1);
            trust_score = i % 2;
            if (i != (numRecords-i-1))
                service.createTrust(stmt, reviewer, reviewee, trust_score);
        }
    }

    private static void makeFavorites(Statement stmt, int numRecords) {
        DbFavoritesService service = new DbFavoritesService();
        String user = "username";
        String temp_user1;
        String temp_user2;
        String car = "abcd";
        String temp_car;

        for (int i=0; i<numRecords-1; i++) {
            temp_user1 = user + Integer.toString(i);
            temp_user2 = user + Integer.toString(numRecords-i-1);
            temp_car = car + Integer.toString(i);
            if (i != (numRecords-i-1)) {
                service.createFavorite(stmt, temp_user1, temp_car);
                service.createFavorite(stmt, temp_user2, temp_car);
            }
        }
    }

    private static void makeHoursOfOp(Statement stmt, int numRecords) {
        DbHoursOfOpService service = new DbHoursOfOpService();
        String driver = "driverUsername";
        String temp_driver;
        float start = 12.00f;
        float finish;

        int day;

        for (int i=0; i<numRecords; i++) {
            day = i % 7;
            finish = day + 1.15f;
            temp_driver = driver + Integer.toString(i);
            service.createHoursOfOp(stmt, temp_driver, start, finish, day);
        }
    }

    private static void makeReservations(Statement stmt, int numRecords) {
        DbReservationService service = new DbReservationService();
        String user = "username";
        String temp_user;
        String car = "abcd";
        String temp_car;
        Date time = new Date();
        long ms = time.getTime();

        for (int i=0; i<numRecords; i++) {
            temp_user = user + Integer.toString(i);
            temp_car = car + Integer.toString(i);
            ms += 1000000;
            time.setTime(ms);
            service.createReservation(stmt, temp_user, temp_car, time);
        }
    }

    private static void makeRides(Statement stmt, int numRecords) {
        DbRideService service = new DbRideService();
        String rider = "username";
        String temp_rider;
        String car = "abcd";
        String temp_car;
        int num_riders;
        double cost = 5.55;
        double distance = 1.75;
        Date date = new Date();
        String to_address = "The bar";
        String from_address = "My house";

        for (int i=0; i<numRecords; i++) {
            temp_rider = rider + Integer.toString(i);
            temp_car = car + Integer.toString(i);
            num_riders = i % 2 + 1;
            service.createRide(stmt, temp_rider, temp_car, num_riders, cost, distance, date, to_address, from_address);
        }
    }
}

