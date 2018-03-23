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

    public void createScoredFeedback(Statement stmt, String reviewee, String car, String reviewer, int usefullness) {
        String query;
        // usefulness_score must be 0, 1, 2
        if (usefullness < 0 || usefullness > 2) {
            return;
        }

        try {
            query = "INSERT INTO ScoredFB (reviewee, car, reviewer, usefulness_score)" +
                    "VALUES (" + reviewee + ", " + car + ", " + reviewer + ", " + Integer.toString(usefullness) + ");";

            stmt.execute(query);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println ("Could not create new ScoredFeedback: " + e.getMessage());
        }
    }
}
