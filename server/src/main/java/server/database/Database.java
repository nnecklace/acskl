package server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import server.models.Model;

/**
 * Class acts as the gateway between the database intergration
 * and business logic. All database interactions are made with
 * this class
 */
public class Database {
    private static final String CONNECTION_STRING = "jdbc:sqlite::resource:database.db";
    private Connection db;
    private PreparedStatement statement;
    private ResultSet result;

    /**
     * The method establishes a connection to the database
     * and prints to std error if a connection cannot be made.
     * Method has a hard dependency to jdbc DriverManager
     * @return the current database instance
     */
    public Database establish() {
        try {
            db = DriverManager.getConnection(CONNECTION_STRING);
        } catch (SQLException exception) {
            System.err.println("Could not establish connection to database " + exception.getMessage());
        }

        return this;
    }

    /**
     * Method prepares a query to be called to the database.
     * Note, method does nothing if no connection has been established
     * @see server.database.Database#establish()
     * @param query the sql prepared query string
     * @return the current instance of the database class
     */
    public Database query(String query) {
        try {
            if (db != null) {
                statement = db.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            }
        } catch (SQLException exception) {
            System.err.println("Could not create query: " + query + " " + exception.getMessage());
        }

        return this;
    }

    /**
     * Method allows users to add parameters to the prepared query statement that
     * is about to be called. If no query has been prepared method will print error to std err.
     * Method allows for three generic value types to be added: String, Integer, and Long.
     * @param <T> the generic value type to be added
     * @param position the position of the added value. Positions start at 1.
     * @param value the actual value to be added.
     * @return the current instance of the database class.
     */
    public <T> Database addValue(int position, T value) {
        try {
            if (statement != null) {
                if (value instanceof String) {
                    statement.setString(position, (String) value);
                } else if (value instanceof Integer) {
                    statement.setInt(position, (Integer) value);
                }
            }
        } catch (SQLException exception) {
            System.err.println("Could not add value " + value + " " + exception.getCause());
        }

        return this;
    }

    /**
     * Method calls the current prepared sql query.
     * Note, method will print error to std error if no sql query has been created.
     * @return the id of the row that has recently been updated (deleted/inserted).
     */
    public int execute() {
        try {
            if (statement != null) {
                statement.execute();
                ResultSet result = statement.getGeneratedKeys();

                if (result == null) {
                    return 0;
                }

                result.next();

                return result.getInt(1);
            }
        } catch (SQLException exception) {
            System.err.println("Could not perform query: " + exception.getMessage());
        }
  
        return 0;
    }

    /**
     * Method calls the current query and adds all results of the query
     * to the result property of the class
     * Note, method will print error to std error if no sql query has been created.
     * @return the current instance of the database class
     */
    public Database executeReturning() {
        try {
            if (statement != null) {
                result = statement.executeQuery();
            }
        } catch (SQLException exception) {
            System.err.println("Could not perform query: " + exception.getMessage());
        }

        return this;
    }

    /**
     * Method returns the first element from the current result list.
     * This method is called when only one row has been returned by
     * The sql query.
     * @param <T> The type we want the value to be cast too. Type has to implement the Model interface
     * @param model The class type of the type we want to be returned
     * @return The type we specified in T
     */
    public <T extends Object & Model> T asValue(Class<T> model) {
        List<T> values = asList(model);

        if (values.isEmpty()) {
            return null;
        }

        return values.get(0);
    }

    /**
     * Method returns the rows returned by the sql query as a Java List.
     * If results is null, an empty list will be returned.
     * @param <T> The type we want the value to be cast too. Type has to implement the Model interface
     * @param model The class type of the type we want to be returned
     * @return List of the type we specified in T
     */
    public <T extends Object & Model> List<T> asList(Class<T> model) {
        if (result == null) {
            return new ArrayList<>();
        } 

        List<T> values = new ArrayList<>();

        try {
            ResultSetMetaData resultMetaData = result.getMetaData();
            Map<String, Object> entity = new HashMap<>();

            while (result.next()) {
                // This would be the correct way of casting with generics
                // However, jdbc sqlite does not support this feature...

                // T value = result.getObject(1, model); // throws SQLFeatureNotSupportedException
                // values.add(value);

                // So we use jacksons Object mapper instead..

                for (int i = 1; i <= resultMetaData.getColumnCount(); ++i) {
                    entity.put(resultMetaData.getColumnLabel(i), result.getObject(i));
                }
                final ObjectMapper mapper = new ObjectMapper();
                values.add(mapper.convertValue(entity, model));
            }
        } catch (SQLException exception) {
            System.err.println("Could not create list of entities: " + exception.getMessage());
        }

        return values;
    }

    /**
     * Method tries to gracefully close the connection to the database
     * making sure that nothing remains open that would cause a memory leak of any kind.
     */
    public void close() {
        try {
            if (result != null) {
                result.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (db != null) {
                db.close();
            }
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