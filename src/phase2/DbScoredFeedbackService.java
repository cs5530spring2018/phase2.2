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
                results += "reviewee: " + rs.getString(1) +
                        "    car: " + rs.getString(2) +
                        "    reviewer: " + rs.getString(3) +
                        "    usefulness_score: " + rs.getString(4) +
                        "\n";
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
        /*
        select fb.reviewer, fb.car, fb.rating, fb.comment, fb.date
        from (select * from (select car, reviewee, avg(usefulness_score) as avg
                from ScoredFB
                group by car, reviewee
                having car in
                  (select vin from UberCar where driver='driverUsername1')) as q1
                  order by q1.avg limit 5) as q2, CarFeedback fb
                  where q2.car=fb.car and fb.reviewer=q2.reviewee;
         */
        try {

            query = "        select fb.reviewer, fb.car, fb.rating, fb.comment, fb.date\n" +
                    "        from (select * from (select car, reviewee, avg(usefulness_score) as avg\n" +
                    "                from ScoredFB\n" +
                    "                group by car, reviewee\n" +
                    "                having car in\n" +
                    "                  (select vin from UberCar where driver='" + driver + "')) as q1\n" +
                    "                  order by q1.avg limit "+ numResults +"  ) as q2, CarFeedback fb\n" +
                    "                  where q2.car=fb.car and fb.reviewer=q2.reviewee";

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
