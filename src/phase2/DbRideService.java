package phase2;

import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DbRideService {
    public DbRideService() {}

    /*
    Table Ride:
	rid int NOT NULL AUTO_INCREMENT,
	rider varchar(32) NOT NULL,
	car varchar(32) NOT NULL,
	num_riders int NOT NULL DEFAULT 1,
	cost decimal(13, 2) NOT NULL DEFAULT 0.00,
	distance decimal(13, 2) NOT NULL DEFAULT 0.00,
	date datetime NOT NULL,
	to_address varchar(128) NOT NULL,
	from_address varchar(128) NOT NULL,
	PRIMARY KEY (rid),
	FOREIGN KEY (rider) REFERENCES UberUser(login),
	FOREIGN KEY (car) REFERENCES UberCar(vin)
     */

    public void createRide(Statement stmt, String rider, String car, int num_riders, double cost, double distance, Date date, String to_address, String from_address) {
        String query;

        // Format the date so mysql will accept is as a datetime
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String formattedDate = sdf.format(date);

        try {
            query = "INSERT INTO Ride (rider, car, num_riders, cost, distance, date, to_address, from_address)" +
                    " VALUES ('" + rider + "', '" + car + "', '" + Integer.toString(num_riders) + "', '" + Double.toString(cost) + "', '" + Double.toString(distance) + "', '" + formattedDate + "', '" + to_address + "', '" + from_address + "')";

            stmt.execute(query);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println ("Could not create new Ride: " + e.getMessage());
        }
    }
}
