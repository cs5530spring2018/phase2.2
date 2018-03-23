package phase2;

import java.lang.*;
import java.sql.*;
import java.io.*;
//import javax.servlet.http.*;

public class CarService {
    public CarService() { }

    public void createUberCar(Statement stmt, String vin, String driver, String category, String make, String model, int year) {
        String query;
        ResultSet results;

        try {
            query = "INSERT INTO UberCar (vin, driver, category, make, model, year)" +
                    "VALUES (" + vin + ", " + driver + ", " + category + ", " + make + ", " + model + ", " + Integer.toString(year)  + ");";

            results = stmt.executeQuery(query);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println ("Could not create new user: " + e.getMessage());
        }
    }

}