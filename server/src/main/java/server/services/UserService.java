package server.services;

import server.database.Database;
import server.models.User;

/**
 * Class is incharge of handling all user table related calls to the database.
 * Everythinig that retrives something from the user table should be handled with this class
 */
public class UserService {
    private Database database;
    /**
     * Username is reserved for testing purposes
     */
    public static final String RESERVED_USERNAME = "thundercougarfalconbird";

    public UserService(Database database) {
        this.database = database;
    }

    /**
     * Method creates a new user with the give username.
     * Method does not allow locked username to be given
     * @param username the username for the new user
     * @return the newly created User class or null if user could not be created
     */
    public User create(String username) {
        if (RESERVED_USERNAME.equals(username)) {
            return null;
        }

        int userId = database.establish()
                             .query("INSERT INTO users (name) VALUES (?)")
                             .addValue(1, username)
                             .execute();

        if (userId == 0) {
            database.close();
            return null;
        }

        User user = database.establish()
                            .query("SELECT * FROM users WHERE id = ?")
                            .addValue(1, userId)
                            .executeReturning()
                            .asValue(User.class);

        return user;
    }

    /**
     * Method asks the database to find a user with the given username
     * @param username the username we are looking for
     * @return the User class for the given username
     */
    public User findByUsername(String username) {
        User user = database.establish()
                            .query("SELECT * FROM users WHERE name = ?")
                            .addValue(1, username)
                            .executeReturning()
                            .asValue(User.class);

        database.close();

        return user;
    }

    /**
     * Method tries to log the user with the current username in to the system.
     * @param username the username that is trying to login
     * @return true if a user with the given username is found, otherwise false.
     */
    public boolean login(String username) {
        User user = findByUsername(username);

        return user != null;
    }

}