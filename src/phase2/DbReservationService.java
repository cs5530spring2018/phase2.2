package phase2;

import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DbReservationService {
    public DbReservationService() {}
    /*
    Table Reserve:
	user varchar(32) NOT NULL,
	car varchar(32) NOT NULL,
	time datetime NOT NULL,
	PRIMARY KEY (user, car, time),
	FOREIGN KEY (user) REFERENCES UberUser(login),
	FOREIGN KEY (car) REFERENCES UberCar(vin)
     */

    public void createReservation(Statement stmt, String user, String car, Date time) {
        String query;
        // Format the date so mysql will accept is as a datetime
        SimpleDateFormat sdf =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String formattedDate = sdf.format(time);

        try {
            query = "INSERT INTO Reserve (user, car, time)" +
                    " VALUES ('" + user + "', '" + car + "', '" + formattedDate + "')";

            stmt.execute(query);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println ("Could not create new Reservation: " + e.getMessage());
        }
    }
}
