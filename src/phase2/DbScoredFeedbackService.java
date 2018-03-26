package phase2;

import java.sql.*;
import java.lang.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DbScoredFeedbackService {
    public DbScoredFeedbackService() {}

    /*
	reviewee varchar(32) NOT NULL,
	car varchar(32) NOT NULL,
	reviewer varchar(32) NOT NULL,
	usefulness_score int NOT NULL,
	PRIMARY KEY (reviewee, car, reviewer),
	FOREIGN KEY (reviewee, car) REFERENCES CarFeedback(reviewer, car),
	FOREIGN KEY (reviewer) REFERENCES UberUser(login)
     */

    public String printUsefulFeedback(ResultSet rs) throws Exception {
        String results = "";
        try {
            while (rs.next()) {
                results += "avg_rating: " + rs.getString(1) +
                        "    car: " + rs.getString(2) + "\n";
            }
            return results;
        }
        catch(Exception e) {
            System.err.println("Unable to print ResultSet");
            System.err.println(e.getMessage());
            throw(e);
        }
    }

    public void createScoredFeedback(Statement stmt, String reviewee, String car, String reviewer, int usefullness) {
        String query;
        // usefulness_score must be 0, 1, 2
        if (usefullness < 0 || usefullness > 2) {
            return;
        }

        try {
            query = "INSERT INTO ScoredFB (reviewee, car, reviewer, usefulness_score)" +
                    " VALUES ('" + reviewee + "', '" + car + "', '" + reviewer + "', '" + Integer.toString(usefullness) + "')";

            stmt.execute(query);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println ("Could not create new ScoredFeedback: " + e.getMessage());
        }
    }

    public ResultSet usefulFeedbackByDriver(Statement stmt, String driver, int numResults) throws Exception {
        String query;
        String driverQuery;
        String driverSet;
        ResultSet rs;
        /*
        select car, avg(usefulness_score) as avg_score
        from ScoredFB group by car having car
        IN (select vin from UberCar where driver='driverUsername0')
        order by avg_score desc limit

        select *
        from select car, reviewee from ScoredFB group by car, reviewee
        having car IN (select vin from UberCar where driver='driver')
        order by
         */
        try {
            driverQuery = "SELECT vin from UberCar WHERE driver='" + driver + "' ";
            rs = stmt.executeQuery(driverQuery);
            driverSet = attrSetToString(rs, "vin");
            rs.close();

            query = "SELECT AVG(usefulness_score), car AS avg_score FROM " +
                    "ScoredFB GROUP BY car HAVING car IN " + driverSet +
                    "ORDER BY avg_score DESC LIMIT " + Integer.toString(numResults);

            return stmt.executeQuery(query);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println ("Could not create new ScoredFeedback: " + e.getMessage());
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
}
