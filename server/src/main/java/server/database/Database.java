package server.database; 

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private String CONNECTION_STRING = "jdbc:sqlite:database.db";
    private Connection db;
    private PreparedStatement statement;
    private ResultSet result;

    public Database establish() {
        try {
            db = DriverManager.getConnection(CONNECTION_STRING);
        } catch (SQLException exception) {
            System.err.println("Could not establish connection to database " + exception.getMessage());
        }

        return this;
    }

    public Database query(String query) {
        try {
            if (db != null) statement = db.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        } catch (SQLException exception) {
            System.err.println("Could not create query: " + query + " " + exception.getMessage());
        }

        return this;
    }

    public <T> Database addValue(int position, T value) {
        try {
            if (statement != null) {
                if (value instanceof String) {
                    statement.setString(position, (String)value);
                } else if (value instanceof Integer) {
                    statement.setInt(position, (Integer)value);
                } else if (value instanceof Long) {
                    statement.setLong(position, (Long)value);
                }
            }
        } catch(SQLException exception) {
            System.err.println("Could not add value " + value + " " + exception.getCause());
        }

        return this;
    }

    // TODO: This might not be necessary
    public boolean execute() {
        try {
            if (statement != null) {
                statement.execute();
                ResultSet result = statement.getGeneratedKeys();

                if (result == null) return false;

                return result.next();
            }
        } catch (SQLException exception) {
            System.err.println("Could not perform query: " + exception.getMessage());
        }
  
        return false;
    }

    public Database executeReturning() {
        try {
            if (statement != null) result = statement.executeQuery();
        } catch (SQLException exception) {
            System.err.println("Could not perform query: " + exception.getMessage());
        }

        return this;
    }

    public void close() {
        try {
            if (result != null) result.close();
            if (statement != null) statement.close();
            if (db != null) db.close();
        } catch (SQLException exception) {
            System.err.println("Could not close connection to database " + exception.getMessage());
        }
    }

    public PreparedStatement getStatement() {
        return statement;
    }

    public ResultSet getResult() {
        return result;
    }

    public Connection getDb() {
        return db;
    }
}