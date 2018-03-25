package phase2;

import java.lang.*;
import java.sql.*;
import java.io.*;
//import javax.servlet.http.*;

public class DbCarService {
    public DbCarService() { }

    public void createUberCar(Statement stmt, String vin, String driver, String category, String make, String model, int year) throws Exception {
        String query;

        try {
            query = "INSERT INTO UberCar (vin, driver, category, make, model, year)" +
                    " VALUES ('" + vin + "', '" + driver + "', '" + category + "', '" + make + "', '" + model + "', '" + Integer.toString(year)  + "')" +
                    " ON DUPLICATE KEY UPDATE driver='" + driver + "'," + " category='" + category + "'," +
                    " make='" + make + "'," + " model='" + model + "'," + " year='" + Integer.toString(year) + "'";
            System.out.println(query);
            stmt.execute(query);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println ("Could not create new UberCar: " + e.getMessage());
            throw(e);
        }
    }

    /*
        ResultSet must have all car columns, please give it result of availableCars()
     */
    public String printableAvailableCars(ResultSet results) throws Exception {
        String output = "";
        try {

            while (results.next()) {
                output += results.getString("vin") + "   " + results.getString("driver") + "   " +
                        results.getString("category") + "   " + results.getString("make") + "   " +
                        results.getString("model") + "   " + results.getString("year") + "\n";

            }
            results.close();
            return output;
        }
        catch(Exception e) {
            System.err.println("Unable to print ResultSet");
            System.err.println(e.getMessage());
            throw(e);
        }
        finally {
            if (results != null && !results.isClosed())
                results.close();
        }
    }


    /*
        Returns ResultSet of UberCar columns available at given time and day
        Caller must close the ResultSet with rs.close() after returned from this method
     */
    public ResultSet availableCars(Statement stmt, float time, int day) throws Exception {
        /*
        SELECT uc.vin, uc.driver, uc.category, uc.make, uc.model, uc.year
        FROM UberCar c, HoursOfOp hoo, UberDriver ud WHERE
            uc.driver=ud.login AND ud.login=hoo.driver AND hoo.start > time
              AND hoo.finish < time AND hoo.day=day;
         */
        ResultSet results;
        String query = "SELECT uc.vin, uc.driver, uc.category, uc.make, uc.model, uc.year\n" +
                "        FROM UberCar uc, HoursOfOp hoo, UberDriver ud WHERE\n" +
                "            uc.driver=ud.login AND ud.login=hoo.driver AND hoo.start < " + Float.toString(time) +
                "              AND hoo.finish < " + Float.toString(time) + " AND hoo.day=" + Integer.toString(day);
        try {
            results = stmt.executeQuery(query);
            return results;
        }
        catch(Exception e) {
            System.err.println("Unable to execute query:"+query+"\n");
            System.err.println(e.getMessage());
            throw(e);
        }
    }

}