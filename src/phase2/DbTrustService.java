package phase2;

import java.sql.Statement;

public class DbTrustService {
    public DbTrustService() {}
    /*
    Trusts table:

	reviewer varchar(32) NOT NULL,
	reviewee varchar(32) NOT NULL,
	trust_score int NOT NULL,
	PRIMARY KEY (reviewer, reviewee),
	FOREIGN KEY (reviewer) REFERENCES UberUser(login),
	FOREIGN KEY (reviewee) REFERENCES UberUser(login)
     */

    public void createTrust(Statement stmt, String reviewer, String reviewee, int score) {
        String query;
        // trust_score must be 0 or 1
        if (score < 0 || score > 1) {
            return;
        }

        try {
            query = "INSERT INTO Trusts (reviewer, reviewee, trust_score)" +
                    " VALUES ('" + reviewer + "', '" + reviewee + "', '" + Integer.toString(score) + "')" +
                    " ON DUPLICATE KEY UPDATE trust_score='" + Integer.toString(score) + "'";

            stmt.execute(query);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println ("Could not create new Trust: " + e.getMessage());
        }
    }
}

