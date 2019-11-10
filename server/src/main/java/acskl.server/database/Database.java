package acskl.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

public class Database {
    private String CONNECTION_STRING = "jdbc:sqlite:database.db";
    private Connection db;
    private PreparedStatement statement;

    public Database establish() {
        try {
            db = DriverManager.getConnection(CONNECTION_STRING);
        } catch (SQLException exception) {
            System.err.println("Could not establish connection to database");
        }

        return this;
    }

    public Database query(String query) throws SQLException {
        if (db != null) {
            statement = db.prepareStatement(query);
        }

        return this;
    }

    public Database addValue(String value) throws SQLException {
        if (statement != null) {
            statement.setString(1, value);
        }

        return this;
    }

    public void execute() throws SQLException {
        if (statement != null) {
            statement.execute();
        }
    }
}