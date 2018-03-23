package phase2;

import java.lang.*;
import java.sql.*;
import java.io.*;
//import javax.servlet.http.*;

public class DbUserService {
    public DbUserService() { }

    public void createUberUser(Statement stmt, String login, String password, String name, String address, String phone) {
        this.createUser(stmt, login, password, name, address, phone, "UberUser");
    }

    public void createUberDriver(Statement stmt, String login, String password, String name, String address, String phone) {
        this.createUser(stmt, login, password, name, address, phone, "UberDriver");
    }

    private void createUser(Statement stmt, String login, String password, String name, String address, String phone, String userType) {
        String query;
        String table = userType;

        try {
            query = "INSERT INTO " + table + "(login, password, name, address, phone)" +
                    "VALUES (" + login + ", " + password + ", " + name + ", " + address + ", " + phone + ");";

            stmt.execute(query);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println ("Could not create new user: " + e.getMessage());
        }
    }
}