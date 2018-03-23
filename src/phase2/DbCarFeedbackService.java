package phase2;

import java.sql.*;
import java.lang.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DbCarFeedbackService {
    public DbCarFeedbackService() {}

    /*
    reviewer varchar(32) NOT NULL,
	car varchar(32) NOT NULL,
	rating int NOT NULL,
	comment varchar(255),
	PRIMARY KEY (reviewer, car),
	FOREIGN KEY (reviewer) REFERENCES UberUser(login),
	FOREIGN KEY (car) REFERENCES UberCar(vin)
     */

    public void createCarFeedback(Statement stmt, String reviewer, String car, int rating, String comment, Date date) {
        String query;
        if (comment == null || comment.length() == 0) {
            comment = "null";
        }

        SimpleDateFormat sdf =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String formattedDate = sdf.format(date);

        try {
            query = "INSERT INTO Car Feedback(reviewer, car, rating, comment, date)" +
                    "VALUES (" + reviewer + ", " + car + ", " + rating + ", " + comment + ", " + formattedDate + ");";

            stmt.execute(query);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println ("Could not create new user: " + e.getMessage());
        }
    }
}
