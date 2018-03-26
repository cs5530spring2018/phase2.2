package phase2;

import com.sun.rowset.CachedRowSetImpl;
import com.sun.rowset.JoinRowSetImpl;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.JoinRowSet;
import java.lang.*;
import java.sql.*;
import java.io.*;
//import javax.servlet.http.*;

public class DbCarService {
    public DbCarService() { }

    public boolean uberCarExists(Statement stmt, String vin) throws Exception {
        String query;
        ResultSet rs;
        boolean exists;

        try {
            query = "SELECT * FROM UberCar WHERE vin='" + vin + "' ";
            rs = stmt.executeQuery(query);
            exists = rs.isBeforeFirst();
            rs.close();
            return exists;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println ("Could not query for UberCar: " + e.getMessage());
            throw(e);
        }
    }

    public void createUberCar(Statement stmt, String vin, String driver, String category, String make, String model, int year) throws Exception {
        String query;

        try {
            query = "INSERT INTO UberCar (vin, driver, category, make, model, year)" +
                    " VALUES ('" + vin + "', '" + driver + "', '" + category + "', '" + make + "', '" + model + "', '" + Integer.toString(year)  + "')" +
                    " ON DUPLICATE KEY UPDATE driver='" + driver + "'," + " category='" + category + "'," +
                    " make='" + make + "'," + " model='" + model + "'," + " year='" + Integer.toString(year) + "'";
            stmt.execute(query);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println ("Could not create new UberCar: " + e.getMessage());
            throw(e);
        }
    }

    public void removeUberCar(Statement stmt, String vin) throws Exception {
        String query;
        try {
            query = "DELETE FROM UberCar WHERE vin='" + vin + "'";
            int success = stmt.executeUpdate(query);
            if(success > 0) {
                System.out.println("Successfully removed car with vin: " + vin);
            } else {
                System.out.println("Car with vin: " + vin + " could not be found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not delete UberCar with vin: " + vin);
        }
    }

    public ResultSet fetchUberCarsForDriver(Statement stmt, String driver) throws Exception{
        String query;
        try {
            query = "SELECT * FROM UberCar WHERE driver='" + driver + "'";
            return stmt.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not fetch UberCars for user: " + driver);
            throw (e);
        }
    }

    public ResultSet fetchUberCarDetails(Statement stmt, String vin) throws Exception{
        String query;
        try {
            query = "SELECT * FROM UberCar WHERE vin='" + vin + "'";
            return stmt.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not fetch car with vin: " + vin);
            throw (e);
        }
    }
    /*
        ResultSet must have all car columns, please give it result of availableCars()
     */
    public String printableCars(ResultSet results) throws Exception {
        String output = "";
        int cols;
        try {
            cols = results.getMetaData().getColumnCount();
            if (cols == 6) {
                while (results.next()) {
                    output += "vin: " + results.getString("vin") + "    driver: " + results.getString("driver") +
                            "    category: " + results.getString("category") + "    make: " + results.getString("make") +
                            "    model: " + results.getString("model") + "    year: " + results.getString("year") + "\n";
                }
            } else {
                String[] labels = { "vin: ", "average: ", "driver: ", "category: ", "make: ", "model: ", "year: "};
                while (results.next()) {
                    for (int i=1; i<=cols; i++) {
                        output += labels[i-1] + results.getString(i) + "      ";
                    }
                    output += "\n";
                }
            }

            results.close();
            return output;
        }
        catch(Exception e) {
            System.err.println("Unable to print ResultSet");
            System.err.println(e.getMessage());
            throw(e);
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

    private String categoryQuery(Statement stmt, String category) throws Exception {
        String query = "SELECT vin FROM UberCar";
        ResultSet rs;
        String vinSet = "";
        try {
            if (category.length() != 0) {
                query += " WHERE category='" + category + "'";
            }
            rs = stmt.executeQuery(query);
            vinSet = attrSetToString(rs, "vin");
            rs.close();
            return vinSet;
        }
        catch(Exception e) {
            System.err.println("Unable to execute query:"+query+"\n");
            System.err.println(e.getMessage());
            throw(e);
        }
    }

    private String modelAggregateQuery(Statement stmt, String catResults, String model, String andor) throws Exception {
        ResultSet modelResults;
        String modelQuery = "SELECT vin FROM UberCar WHERE";
        String modelResultsStr = "";

        try {
            if (model.length() == 0) {
                return catResults;
            }
            // Empty cat results
            if (catResults.equals("('')"))
                modelQuery = "SELECT vin FROM UberCar";
            else
                modelQuery += " model LIKE '%" + model + "%'" + " OR" +
                        " make LIKE '%" + model + "%'";
            if (andor.length() != 0) {
                modelQuery += " " + andor + " vin IN " + catResults;

            }

            modelResults = stmt.executeQuery(modelQuery);
            modelResultsStr = attrSetToString(modelResults, "vin");
            modelResults.close();
            return modelResultsStr;
        }
        catch(Exception e) {
            System.err.println("Unable to execute query:"+modelQuery+"\n");
            System.err.println(e.getMessage());
            throw(e);
        }
    }

    private String addressAggregateQuery(Statement stmt, String modelResults, String address, String andor) throws Exception {
        ResultSet addressResults;
        String addressResultsStr;

        if (andor.length() == 0)
            andor = "OR";
        String addressQuery = "SELECT uc.vin AS vin FROM UberCar uc, UberDriver ud WHERE uc.driver=ud.login AND ud.address LIKE '%" + address + "%'";
        try {
            if (address.length() == 0) {
                return modelResults;
            }
            addressQuery += " " + andor + " uc.vin IN " + modelResults;

            addressResults = stmt.executeQuery(addressQuery);
            addressResultsStr = attrSetToString(addressResults, "vin");
            addressResults.close();
            return addressResultsStr;
        }
        catch(Exception e) {
            System.err.println("Unable to execute query:"+addressQuery+"\n");
            System.err.println(e.getMessage());
            throw(e);
        }
    }

    private ResultSet sortAggregatedVinResults(Statement stmt, String allVins, String sort) throws Exception {
        ResultSet allCarResults;
        ResultSet sortedResults;
        ResultSet trustedUserResults;
        ResultSet trustedFeedbackResults;
        JoinRowSet joinedRows = new JoinRowSetImpl();
        CachedRowSet allCars = new CachedRowSetImpl();
        CachedRowSet matchedCars = new CachedRowSetImpl();

        String revieweeSet;
        String feedbackSet;
        String allQuery = "";
        String sortQuery = "";
        String tuQuery = "";
        String tfQuery = "";

        try {
            if (sort.length() == 0) {
                // No sort, return all car data
                sortQuery = "SELECT * FROM UberCar WHERE vin IN " + allVins;
                return stmt.executeQuery(sortQuery);
            }

            if (sort.equals("a")) {
                sortQuery = "SELECT car, AVG(rating) AS average FROM CarFeedback WHERE car IN " + allVins + " GROUP BY car ORDER BY average ASC";
                sortedResults = stmt.executeQuery(sortQuery);
            }
            else if (sort.equals("b")) {
                tuQuery = "SELECT reviewee FROM Trusts WHERE trust_score=1";
                trustedUserResults = stmt.executeQuery(tuQuery);
                revieweeSet = attrSetToString(trustedUserResults, "reviewee");
                tfQuery = "SELECT car FROM CarFeedback WHERE reviewer IN " + revieweeSet + " AND car IN " + allVins;
                trustedFeedbackResults = stmt.executeQuery(tfQuery);
                feedbackSet = attrSetToString(trustedFeedbackResults, "car");
                // Note that the join operation on the cache sets reverses, so don't do DESC order
                sortQuery = "SELECT car, AVG(rating) AS average FROM CarFeedback WHERE car IN " + feedbackSet + " GROUP BY car ORDER BY average ASC";
                sortedResults = stmt.executeQuery(sortQuery);
            }
            else {
                Exception e = new Exception();
                System.err.println("Sort type invalid: " + sort);
                throw(e);
            }
            // Join the results with all cars
            if (!sortedResults.isBeforeFirst())
                return sortedResults;

            matchedCars.populate(sortedResults);
            matchedCars.setMatchColumn(1);
            //System.out.println(sortedDataToString(matchedCars));
            joinedRows.addRowSet(matchedCars);

            allQuery = "SELECT uc.vin as car, uc.driver, uc.category, uc.make, uc.model, uc.year FROM UberCar uc";
            allCarResults = stmt.executeQuery(allQuery);
            allCars.populate(allCarResults);
            allCars.setMatchColumn(1);
            joinedRows.addRowSet(allCars);

            return joinedRows;
        }
        catch(Exception e) {
            System.err.println("Unable to execute query:"+sortQuery+"\n");
            System.err.println(e.getMessage());
            throw(e);
        }
    }

    public ResultSet ucBrowser(Statement stmt, String category, String andor1, String model, String andor2, String address, String sort) throws Exception {
        String categoryResults = categoryQuery(stmt, category);
        String modelResults = modelAggregateQuery(stmt, categoryResults, model, andor1);
        String addrResults = addressAggregateQuery(stmt, modelResults, address, andor2);

        ResultSet sortedResults = sortAggregatedVinResults(stmt, addrResults, sort);
        return sortedResults;
    }

    public ResultSet vinsToCars(Statement stmt, ResultSet vins) throws Exception {
        ResultSet cars;
        String vinSet = "";
        String query = "Select * FROM UberCar WHERE vin IN ";
        try {
            vinSet = attrSetToString(vins, "car");
            query += vinSet;
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
        if (results.equals("()"))
            results = "('')";
        return results;
    }

    public ResultSet recommendedCars(Statement stmt, String driver) throws Exception {
        String query = "";
        ResultSet vins;
        ResultSet allData;
        JoinRowSet joinedRows = new JoinRowSetImpl();
        CachedRowSet allCars = new CachedRowSetImpl();
        CachedRowSet filteredCars = new CachedRowSetImpl();
        /*
        select count(r2.car) as c, r2.car
        from Ride r2,
            (select r.rider from Ride r where r.car='abcd0')
            as q1
         where r2.rider=q1.rider
         group by r2.car
         order by c desc;
         */

        try {
            // Normally would be DESC but cache set join operation reverses it
            query = "        select count(r2.car) as c, r2.car \n" +
                    "        from Ride r2, \n" +
                    "            (select r.rider from Ride r where r.car='" + driver + "') \n" +
                    "            as q1 \n" +
                    "         where r2.rider=q1.rider \n" +
                    "         group by r2.car \n" +
                    "         order by c";
            vins = stmt.executeQuery(query);
            filteredCars.populate(vins);
            filteredCars.setMatchColumn(2);
            allData = vinsToCars(stmt, filteredCars);
            allCars.populate(allData);
            allCars.setMatchColumn(1);
            if (filteredCars.isBeforeFirst()) {
                joinedRows.addRowSet(filteredCars);
                if (allCars.isBeforeFirst())
                    joinedRows.addRowSet(allCars);
                return joinedRows;
            }
            else
                return filteredCars;
        }
        catch(Exception e) {
            System.err.println("Unable to execute query:"+query+"\n");
            System.err.println(e.getMessage());
            throw(e);
        }
    }

    public String printableRecommendedCars(ResultSet rs) {
        String output = "";
        try {
            if (!rs.isBeforeFirst())
                return output;
            while (rs.next()) {
                output += "ride_count: " + rs.getString(1) + "    " +
                        "vin: " + rs.getString(2) + "    " +
                        "driver: " + rs.getString(3) + "    " +
                        "category: " + rs.getString(4) + "    " +
                        "make: " + rs.getString(5) + "    " +
                        "model: " + rs.getString(6) + "    " +
                        "year: " + rs.getString(7) + "\n";
            }
            return output;
        }
        catch(Exception e) {
            System.err.println("Unable to print\n");
            System.err.println(e.getMessage());
            return output;
        }
    }

}