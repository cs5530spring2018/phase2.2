package phase2;

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

    public void createHoursOfOp(Statement stmt, String driver, Date start, Date finish, int day) {
        String query;
        // Only 7 days in a week
        if (day < 0 || day > 6) {
            return;
        }
        // Format the date so mysql will accept is as a datetime
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String formattedStart = sdf.format(start);
        String formattedFinish = sdf.format(finish);

        try {
            query = "INSERT INTO HoursOfOp (driver, start, finish, day)" +
                    " VALUES ('" + driver + "', '" + formattedStart + "', '" + formattedFinish + "', '" + Integer.toString(day) + "')";

            stmt.execute(query);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println ("Could not create new HoursOfOp: " + e.getMessage());
        }
    }
}
