package phase2;

import java.sql.ResultSet;
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

    public boolean favoriteExists(Statement stmt, String user, String car) {
        String query;
        ResultSet rs;
        boolean exists;

        try {
            query = "SELECT * FROM Favorites WHERE user='" + user + "' AND car='" + car + "'";
            rs = stmt.executeQuery(query);
            exists = rs.isBeforeFirst();
            rs.close();
            return exists;
        }
        catch(Exception e) {
            e.printStackTrace();
            System.err.println ("Could not query for favorite: " + e.getMessage());
            return true;
        }
    }

    public void createFavorite(Statement stmt, String user, String car) {
        String query;

        try {
            query = "INSERT INTO Favorites (user, car)" +
                    " VALUES ('" + user + "', '" + car + "')";

            stmt.execute(query);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println ("Could not create new Favorite: " + e.getMessage());
        }
    }
}
