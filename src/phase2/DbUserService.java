package phase2;

import javax.swing.plaf.nimbus.State;
import java.lang.*;
import java.sql.*;
import java.io.*;
//import javax.servlet.http.*;

public class DbUserService {
    public DbUserService() { }

    /*
    Table UberUser:
	login varchar(32) NOT NULL,
	password varchar(64) NOT NULL,
	name varchar(32) NOT NULL,
	address varchar(128) NOT NULL,
	phone varchar(32) NOT NULL,
	PRIMARY KEY (login)
     */

    /*
    Table UberDriver:
	login varchar(32) NOT NULL,
	password varchar(64) NOT NULL,
	name varchar(32) NOT NULL,
	address varchar(128) NOT NULL,
	phone varchar(32) NOT NULL,
	PRIMARY KEY (login)
     */

    private void createUberUser(Statement stmt, String login, String password, String name, String address, String phone) {
        this.createUser(stmt, login, password, name, address, phone, "UberUser");
    }

    private void createUberDriver(Statement stmt, String login, String password, String name, String address, String phone) {
        this.createUser(stmt, login, password, name, address, phone, "UberDriver");
    }

    public void createUser(Statement stmt, String login, String password, String name, String address, String phone, String userType) {
        String query;
        String table = userType;

        try {
            query = "INSERT INTO " + table + "(login, password, name, address, phone)" +
                    " VALUES ('" + login + "', '" + password + "', '" + name + "', '" + address + "', '" + phone + "')";

            stmt.execute(query);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println ("Could not create new user: " + e.getMessage());
        }
    }

    public boolean attemptToLogIn(Statement stmt, String login, String password, String table) throws Exception {
        ResultSet results = null;
        boolean noRecord;
        String query = "SELECT uu.login, uu.password FROM " + table + " uu WHERE uu.login='" + login + "' AND uu.password='" + password + "'";
        try {
            results = stmt.executeQuery(query);
            noRecord = results.isBeforeFirst();
            results.close();
            return noRecord;
        }
        catch(Exception e) {
            System.err.println("Unable to execute query:"+query+"\n");
            System.err.println(e.getMessage());
            throw(e);
        }
        finally {
            if (results != null && !results.isClosed())
                results.close();
        }
    }
    public boolean isLoginAvailable(Statement stmt, String login, String table) throws Exception {
        return checkNameAvailable(stmt, login, table);
    }
    public boolean isDriverLoginAvailable(Statement stmt, String login) throws Exception {
        return checkNameAvailable(stmt, login, "UberDriver");
    }

    public boolean isUserLoginAvailable(Statement stmt, String login) throws Exception {
        return checkNameAvailable(stmt, login, "UberUser");
    }

    private boolean checkNameAvailable(Statement stmt, String login, String table) throws Exception {
        // Look up user first and see if they already exist
        ResultSet results = null;
        boolean nameAvailable;
        String query = "SELECT uu.login FROM " + table + " uu WHERE uu.login='" + login + "'";
        try {
            results = stmt.executeQuery(query);
            nameAvailable = !results.isBeforeFirst();
            results.close();
            return nameAvailable;
        }
        catch(Exception e) {
            System.err.println("Unable to execute query:"+query+"\n");
            System.err.println(e.getMessage());
            throw(e);
        }
        finally {
            if (results != null && !results.isClosed())
                results.close();
        }
    }

    public boolean registerUberUser(Statement stmt, String login, String password, String name, String address, String phone) throws Exception {
        // Look up user first and see if they already exist
        ResultSet results;
        String query = "SELECT uu.login FROM UberUser uu WHERE uu='" + login + "'";
        try {
            results = stmt.executeQuery(query);
            if (!results.isBeforeFirst()) {
                return false;
            }
            this.createUberUser(stmt, login, password, name, address, phone);
            return true;
        }
        catch(Exception e) {
            System.err.println("Unable to execute query:"+query+"\n");
            System.err.println(e.getMessage());
            throw(e);
        }
    }

    public boolean registerUberDriver(Statement stmt, String login, String password, String name, String address, String phone) throws Exception {
        // Look up user first and see if they already exist
        ResultSet results;
        String query = "SELECT ud.login FROM UberDriver ud WHERE ud='" + login + "'";
        try {
            results = stmt.executeQuery(query);
            if (!results.isBeforeFirst()) {
                return false;
            }
            this.createUberDriver(stmt, login, password, name, address, phone);
            return true;
        }
        catch(Exception e) {
            System.err.println("Unable to execute query:"+query+"\n");
            System.err.println(e.getMessage());
            throw(e);
        }
    }
}