package phase2;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DbHoursOfOpService {
    public DbHoursOfOpService() {}

    /*
    Table HoursOfOp:
	driver varchar(32) NOT NULL,
	start datetime NOT NULL,
	finish datetime NOT NULL,
	day int NOT NULL,
	PRIMARY KEY (driver, start, finish, day),
	FOREIGN KEY (driver) REFERENCES UberDriver(login)
     */

    public void createHoursOfOp(Statement stmt, String driver, float start, float finish, int day) {
        String query;
        // Only 7 days in a week
        if (day < 0 || day > 6) {
            return;
        }

        try {
            query = "INSERT INTO HoursOfOp (driver, start, finish, day)" +
                    " VALUES ('" + driver + "', '" + start + "', '" + finish + "', '" + day + "')";

            stmt.execute(query);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println ("Could not create new HoursOfOp: " + e.getMessage());
        }
    }

    public void removeHoursOfOp(Statement stmt, String driver, float start, float finish, int day) throws Exception {
        String query;
        try {
            query = "DELETE FROM HoursOfOp WHERE driver='" + driver + "' AND start=" + start +" AND finish=" +
                    finish + " AND day=" + day ;
            System.out.println("Removing HoursOfOp for: " + driver);
            int success = stmt.executeUpdate(query);
            System.out.println(query);
            if(success > 0) {
                System.out.println("Successfully removed HoursOfOp for: " + driver);
            } else {
                System.out.println("HoursOfOp for: " + driver + " could not be found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not delete HoursOfOp for: " + driver);
        }
    }

    public ResultSet fetchHoursOfOp(Statement stmt, String driver) throws Exception{
        String query;
        try {
            query = "SELECT * FROM HoursOfOp WHERE driver='" + driver + "'";
            return stmt.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not fetch HoursOfOp: " + e.getMessage());
            throw(e);
        }
    }

    public String printableHoursOfOp(ResultSet results) throws Exception {
        String output = "";
        int cols;
        try {
            cols = results.getMetaData().getColumnCount();

            String[] labels = {"driver: ", "start: ", "finish: ", "day: "};
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
