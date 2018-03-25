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
            uc.driver=ud.login AND ud.login=hoo.driver AND hoo.start < time
              AND hoo.finish > time AND hoo.day=day;
         */
        ResultSet results;
        String query = "SELECT uc.vin, uc.driver, uc.category, uc.make, uc.model, uc.year\n" +
                "        FROM UberCar uc, HoursOfOp hoo, UberDriver ud WHERE\n" +
                "            uc.driver=ud.login AND ud.login=hoo.driver AND hoo.start < " + Float.toString(time) +
                "              AND hoo.finish > " + Float.toString(time) + " AND hoo.day=" + Integer.toString(day);
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

    private ResultSet categoryQuery(Statement stmt, String category) throws Exception {
        ResultSet results;
        String query = "SELECT vin FROM UberCar";
        try {
            if (category.length() != 0)
                query += " WHERE category='" + category + "'";
            results = stmt.executeQuery(query);
            return results;
        }
        catch(Exception e) {
            System.err.println("Unable to execute query:"+query+"\n");
            System.err.println(e.getMessage());
            throw(e);
        }
        /*
            SELECT uc.vin FROM UberCar uc, UberDriver ud WHERE
            uc.vin=uc.car AND/OR ud.address LIKE "%addr%" AND/OR
            uc.vin in vinUcQuery;
         */
    }

    private ResultSet modelAggregateQuery(Statement stmt, ResultSet catResults, String model, String andor) throws Exception {
        ResultSet modelResults;
        String modelQuery = "SELECT vin FROM UberCar WHERE";
        String vinSet = attrSetToString(catResults, "vin");

        try {
            if (catResults.isBeforeFirst() && model.length() == 0) {
                return catResults;
            }
            if (model.length() != 0)
                modelQuery += " model='" + model + "'";
            if (andor.length() != 0) {
                modelQuery += " " + andor + " vin IN " + vinSet;

            }
            if (!catResults.isClosed())
                catResults.close();
            modelResults = stmt.executeQuery(modelQuery);
            return modelResults;
        }
        catch(Exception e) {
            System.err.println("Unable to execute query:"+modelQuery+"\n");
            System.err.println(e.getMessage());
            throw(e);
        }
    }

    private ResultSet addressAggregateQuery(Statement stmt, ResultSet modelResults, String address, String andor) throws Exception {
        ResultSet addressResults;
        String vinSet = attrSetToString(modelResults, "vin");
        String addressQuery = "SELECT uc.vin AS vin FROM UberCar uc, UberDriver ud WHERE uc.driver=ud.login AND ud.address LIKE '%" + address + "%'";
        try {
            if (address.length() == 0)
                return modelResults;
            addressQuery += " " + andor + " uc.vin IN " + vinSet;

            if (!modelResults.isClosed())
                modelResults.close();
            addressResults = stmt.executeQuery(addressQuery);
            return addressResults;
        }
        catch(Exception e) {
            System.err.println("Unable to execute query:"+addressQuery+"\n");
            System.err.println(e.getMessage());
            throw(e);
        }
    }

    private ResultSet sortAggregatedVinResults(Statement stmt, ResultSet allVins, String sort) throws Exception {
        /*
        select car, avg(rating) from CarFeedback
            group by car
                having car in ('abcd0', 'abcd1', 'abcd77');
         */
        ResultSet sortedResults;
        ResultSet trustedUserResults;
        ResultSet trustedFeedbackResults;
        String vinSet = attrSetToString(allVins, "vin");
        String revieweeSet;
        String feedbackSet;
        String sortQuery = "";
        String tuQuery = "";
        String tfQuery = "";
        if (sort.length() == 0)
            return allVins;
        try {
            if (sort.equals("a")) {
                sortQuery = "SELECT car AS vin, AVG(rating) AS average FROM CarFeedback GROUP BY car HAVING car IN " +
                             vinSet + " ORDER BY AVG(rating) DESC";
                sortedResults = stmt.executeQuery(sortQuery);
                return sortedResults;
            }
            else if (sort.equals("b")) {
                tuQuery = "SELECT reviewee FROM Trusts WHERE trust_score=1";
                trustedUserResults = stmt.executeQuery(tuQuery);
                revieweeSet = attrSetToString(trustedUserResults, "reviewee");
                tfQuery = "SELECT car FROM CarFeedback WHERE reviewer IN " + revieweeSet;
                trustedFeedbackResults = stmt.executeQuery(tfQuery);
                feedbackSet = attrSetToString(trustedFeedbackResults, "car");
                sortQuery = "SELECT car AS vin, AVG(rating) AS average FROM CarFeedback GROUP BY car HAVING car IN " +
                        feedbackSet + " ORDER BY AVG(rating) DESC";
                sortedResults = stmt.executeQuery(sortQuery);
                return sortedResults;
            }
            else {
                Exception e = new Exception();
                System.err.println("Sort type invalid: " + sort);
                throw(e);
            }
        }
        catch(Exception e) {
            System.err.println("Unable to execute query:"+sortQuery+"\n");
            System.err.println(e.getMessage());
            throw(e);
        }
    }

    public String sortedDataToString(ResultSet rs) throws Exception {
        String output = "";
        try {
            while (rs.next()) {
                output += rs.getString("vin") + "   " + rs.getString("average") + "\n";
            }
        }
        catch(Exception e) {
            System.err.println("Unable to print data");
            System.err.println(e.getMessage());
            throw(e);
        }
        return output;
    }

    public ResultSet ucBrowser(Statement stmt, String category, String andor1, String model, String andor2, String address, String sort) throws Exception {
        ResultSet categoryResults = categoryQuery(stmt, category);
        ResultSet modelResults = modelAggregateQuery(stmt, categoryResults, model, andor1);
        ResultSet addrResults = addressAggregateQuery(stmt, modelResults, address, andor2);
        if (!addrResults.isBeforeFirst()) {
            return addrResults;
        }
        ResultSet sortedResults = sortAggregatedVinResults(stmt, addrResults, sort);
        return sortedResults;
    }

    public ResultSet vinsToCars(Statement stmt, ResultSet vins) throws Exception {
        ResultSet cars;
        String query = "Select * FROM UberCar WHERE vin IN (";
        try {
            while (vins.next()) {
                query += vins.getString("vin") + ", ";
            }
            query += ")";
            cars = stmt.executeQuery(query);
            return cars;
        }
        catch(Exception e) {
            System.err.println("Unable to execute query:"+query+"\n");
            System.err.println(e.getMessage());
            throw(e);
        }
    }

    private String attrSetToString(ResultSet rs, String attr) throws Exception {
        String results = "(";
        while (rs.next()) {
            results += "'" + rs.getString(attr) + "'" + ", ";
        }
        if (results.endsWith(", ")) {
            results = results.substring(0, results.length()-2);
        }
        results += ")";
        return results;
    }

}