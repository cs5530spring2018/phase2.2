package phase2;

import java.sql.Statement;

public class DbFavoritesService {
    public DbFavoritesService() {}
    /*
    Favorites table:

	user varchar(32) NOT NULL,
	car varchar(32) NOT NULL,
	PRIMARY KEY (user, car),
	FOREIGN KEY (user) REFERENCES UberUser(login),
	FOREIGN KEY (car) REFERENCES UberCar(vin)
     */

    public void createFavorite(Statement stmt, String user, String car) {
        String query;

        try {
            query = "INSERT INTO Favorites (user, car)" +
                    " VALUES ('" + user + "', '" + car + "')";

            stmt.execute(query);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println ("Could not create new Trust: " + e.getMessage());
        }
    }
}
