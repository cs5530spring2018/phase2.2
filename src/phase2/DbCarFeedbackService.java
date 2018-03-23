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
        // Comments can be null
        if (comment == null || comment.length() == 0) {
            comment = "null";
        }
        // Rating must be between 0-10
        if (rating < 0 || rating > 10) {
            return;
        }

        // Format the date so mysql will accept is as a datetime
        SimpleDateFormat sdf =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String formattedDate = sdf.format(date);

        try {
            query = "INSERT INTO CarFeedback(reviewer, car, rating, comment, date)" +
                    "VALUES (" + reviewer + ", " + car + ", " + Integer.toString(rating) + ", " + comment + ", " + formattedDate + ");";

            stmt.execute(query);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println ("Could not create new CarFeedback: " + e.getMessage());
        }
    }
}
