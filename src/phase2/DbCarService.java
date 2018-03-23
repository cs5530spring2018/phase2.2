package phase2;

import java.lang.*;
import java.sql.*;
import java.io.*;
//import javax.servlet.http.*;

public class DbCarService {
    public DbCarService() { }

    public void createUberCar(Statement stmt, String vin, String driver, String category, String make, String model, int year) {
        String query;

        try {
            query = "INSERT INTO UberCar (vin, driver, category, make, model, year)" +
                    " VALUES ('" + vin + "', '" + driver + "', '" + category + "', '" + make + "', '" + model + "', '" + Integer.toString(year)  + "')";

            stmt.execute(query);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println ("Could not create new UberCar: " + e.getMessage());
        }
    }

}