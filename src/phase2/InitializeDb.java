package phase2;

import java.util.*;
import java.lang.*;
import java.sql.*;

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
            System.out.println ("Database connection established");
            System.out.println("Making users and drivers...");
            makeUberUsers(con.stmt, 100);
            makeUberDrivers(con.stmt, 100);
            System.out.println("Creating vehicles...");
            makeUberCars(con.stmt, 100);
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
            service.createUberUser(stmt, temp_user, pw, temp_name, address, phone);
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
            service.createUberDriver(stmt, temp_user, pw, temp_name, address, phone);
        }
    }

    private static void makeUberCars(Statement stmt, int numCars) {
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
}

