package phase2;

import java.sql.ResultSet;
import java.sql.Statement;

public class DbStatisticsService {
    public DbStatisticsService() {}

    public String printablePopularRides(ResultSet rs, String colName) throws Exception {
        /*
        colName should be avg_cost, avg_rating, or total_rides as named in queries below.
        However, you can really pass in anything you want since its just a label and this uses col index
         */
        String results = "";
        int col = rs.getMetaData().getColumnCount();
        try {
            while (rs.next()) {
                if (col==3) {
                    results += colName + ": " + rs.getString(1)
                            + "    vin: " + rs.getString(2)
                            + "    category: " + rs.getString(3);
                }
                else if (col == 4) {
                    results += colName + ": " + rs.getString(1)
                            + "    vin: " + rs.getString(2)
                            + "    driver: " + rs.getString(3)
                            + "    category: " + rs.getString(4);
                }
                else {
                    System.out.println("INVALID NUMBER OF COLUMNS");
                }
                results += "\n";
            }
            return results;
        }
        catch(Exception e) {
            System.err.println("Unable to print ResultSet");
            System.err.println(e.getMessage());
            throw(e);
        }

    }

    // Pass in total_rides to print function
    public ResultSet mostPopularUcByRide(Statement stmt, int numResults, String category) throws Exception {
        String query;
        try {
            query = "SELECT COUNT(r.car) AS total_rides, uc.vin, uc.category FROM " +
                    "Ride r, UberCar uc WHERE r.car=uc.vin AND uc.category='" + category + "'" +
                    " GROUP BY r.car ORDER BY total_rides DESC LIMIT " + Integer.toString(numResults);

            return stmt.executeQuery(query);
        }
        catch(Exception e) {
            System.err.println("Unable to print ResultSet");
            System.err.println(e.getMessage());
            throw(e);
        }
    }

    // Pass in avg_cost to print function
    public ResultSet mostExpensiveUcByCategory(Statement stmt, int numResults, String category) throws Exception {
        String query;
        try {

            query = "SELECT AVG(r.cost) as avg_cost, uc.vin, uc.category FROM " +
                    "Ride r, UberCar uc WHERE r.car=uc.vin AND uc.category='" + category +"'" +
                    " GROUP BY uc.vin ORDER BY avg_cost DESC LIMIT " + Integer.toString(numResults);

            return stmt.executeQuery(query);
        }
        catch(Exception e) {
            System.err.println("Unable to print ResultSet");
            System.err.println(e.getMessage());
            throw(e);
        }
    }

    // When printing, pass 'avg_rating' to the print function
    public ResultSet highestRatedUdByCategory(Statement stmt, int numResults, String category) throws Exception {
        String query;
        try {
            query = "SELECT AVG(cf.rating) as avg_rating, uc.vin, uc.driver, uc.category FROM " +
                    "CarFeedback cf, UberCar uc, UberDriver ud " +
                    "WHERE cf.car=uc.vin AND uc.driver=ud.login AND uc.category='" + category + "'" +
                    " GROUP BY uc.vin ORDER BY avg_rating LIMIT " + Integer.toString(numResults);

            return stmt.executeQuery(query);
        }
        catch(Exception e) {
            System.err.println("Unable to print ResultSet");
            System.err.println(e.getMessage());
            throw(e);
        }
    }
}
