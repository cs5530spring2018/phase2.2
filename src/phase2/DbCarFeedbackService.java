package phase2;

import com.sun.org.apache.regexp.internal.RE;

import java.sql.*;
import java.lang.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public void createCarFeedback(Statement stmt, String reviewer, String car, int rating, String comment, LocalDateTime date) {
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = formatter.format(date);

        try {
            query = "INSERT INTO CarFeedback(reviewer, car, rating, comment, date)" +
                    " VALUES ('" + reviewer + "', '" + car + "', '" + Integer.toString(rating) + "', '" + comment + "', '" + formattedDate + "')";

            stmt.execute(query);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println ("Could not create new CarFeedback: " + e.getMessage());
        }
    }

    public ResultSet fetchFeedbackForCar(Statement stmt, String car) throws Exception{
        String query;
        try{
            query = "SELECT * FROM CarFeedback WHERE car='" + car + "'";
            return stmt.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not fetch Feedback for vin: " + car);
            throw(e);
        }
    }

    public String printFeedBack(ResultSet results) throws Exception{
        String output = "";
        int cols;
        try {
            cols = results.getMetaData().getColumnCount();

            String[] labels = {"reviewer: ", "vin: ", "rating: ", "comment: ", "date: "};
            while (results.next()) {
                for (int i = 1; i <= cols; i++) {
                    output += labels[i - 1] + results.getString(i) + "      ";
                }
                output += "\n";
            }
            results.close();
            return output;
        } catch (Exception e) {
            System.err.println("Unable to print ResultSet");
            System.err.println(e.getMessage());
            throw (e);
        }
    }
}
