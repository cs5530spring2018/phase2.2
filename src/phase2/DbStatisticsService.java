package phase2;

import java.sql.ResultSet;
import java.sql.Statement;

public class DbStatisticsService {
    public DbStatisticsService() {}

    public String printableStatistics(ResultSet rs, String colName) throws Exception {
        /*
        colName should be avg_cost, avg_rating, or total_rides as named in queries below.
        However, you can really pass in anything you want since its just a label and this uses col index
         */
        String results = "";
        int col = rs.getMetaData().getColumnCount();
        try {
            while (rs.next()) {
                if (col==2) {
                    results += colName + ": " + rs.getString(1)
                            + "    user: " + rs.getString(2);
                }
                else if (col==3) {
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

    public ResultSet mostUsefulUsers(Statement stmt, int numResults) throws Exception {
        String query;
        try {
            query = "SELECT reviewee, AVG(usefulness_score) AS avg_score " +
                    "FROM ScoredFB GROUP BY reviewee ORDER BY avg_score DESC "  +
                    "LIMIT " + Integer.toString (numResults);

            return stmt.executeQuery(query);
        }
        catch(Exception e) {
            System.err.println("Unable to print ResultSet");
            System.err.println(e.getMessage());
            throw(e);
        }
    }

    public boolean oneDegreeOfSeparation(Statement stmt, String user1, String user2) throws Exception {
        String query;
        ResultSet rs = null;
        boolean oneDegree = false;
        try {
            query = "SELECT * FROM Favorites f1, Favorites f2 WHERE " +
                    "f1.user='" + user1 + "' AND f2.user='" + user2 + "' " +
                    "AND f1.user!=f2.user AND f1.car=f2.car";
            rs = stmt.executeQuery(query);
            oneDegree = rs.isBeforeFirst();
            if (rs != null && !rs.isClosed())
                rs.close();
            return oneDegree;
        }
        catch(Exception e) {
            System.err.println("Unable to print ResultSet");
            System.err.println(e.getMessage());
            throw(e);
        }
    }

    public boolean twoDegreesOfSeparation(Statement stmt, String user1, String user2) throws Exception {
        String query;
        ResultSet rs = null;
        String user1Set;
        String user2Set;
        boolean twoDegree;
        try {
            query = "SELECT f1.user FROM Favorites f1, Favorites f2 WHERE " +
                    "f1.user='" + user1 + "' " + "AND f1.user!=f2.user AND f1.car=f2.car";
            rs = stmt.executeQuery(query);
            user1Set = attrSetToString(rs, "user");

            query = "SELECT f1.user FROM Favorites f1, Favorites f2 WHERE " +
                    "f1.user='" + user2 + "' " + "AND f1.user!=f2.user AND f1.car=f2.car";
            rs.close();
            rs = stmt.executeQuery(query);
            user2Set = attrSetToString(rs, "user");

            query = "SELECT * FROM Favorites f1, Favorites f2 WHERE " +
                    "f1.user IN " + user2Set + " OR f2.user IN " + user1Set;
            rs.close();
            rs = stmt.executeQuery(query);
            twoDegree = rs.isBeforeFirst();
            if (rs != null && !rs.isClosed())
                rs.close();
            return (twoDegree && !oneDegreeOfSeparation(stmt, user1, user2));
        }
        catch(Exception e) {
            System.err.println("Unable to print ResultSet");
            System.err.println(e.getMessage());
            throw(e);
        }
    }

    private String attrSetToString(ResultSet rs, String attr) throws Exception {
        String results = "(";
        while (rs.next()) {
            results += "'" + rs.getString(attr) + "'" + ", ";
        }
        if (results.endsWith(", ")) {
            results = results.substring(0, results.length()-2);
        }
        results += ")";
        if (results.equals("()"))
            results = "('')";
        return results;
    }
}
