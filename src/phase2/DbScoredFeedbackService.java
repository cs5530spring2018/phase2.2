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
        select avg(sfb.usefulness_score), fb.car
        from ScoredFB sfb, CarFeedback fb
        where sfb.reviewee=fb.reviewer
        group by fb.car
        having fb.car IN
        (select uc.vin
          from UberCar uc, UberDriver ud
            where uc.driver=ud.login and ud.login='driverUsername3');

         */
        try {
            driverQuery = "SELECT uc.vin FROM UberCar uc, UberDriver ud " +
                          "WHERE uc.driver=ud.login AND ud.login='" + driver + "'";
            rs = stmt.executeQuery(driverQuery);
            driverSet = attrSetToString(rs, "vin");
            rs.close();
            query = "SELECT AVG(sfb.usefulness_score), fb.car FROM " +
                    "ScoredFB sfb, Carfeedback fb WHERE sfb.reviewee=fb.reviewer " +
                    "GROUP BY fb.car HAVING fb.car IN " + driverSet;

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
