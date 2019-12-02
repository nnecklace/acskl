package server.services;

import server.database.Database;
import server.models.User;

public class UserService {
    private Database database;
    // Username is reserved for testing purposes
    public static String RESERVED_USERNAME = "thundercougarfalconbird";

    public UserService(Database database) {
        this.database = database;
    }

    public boolean create(String username) {
        if (RESERVED_USERNAME.equals(username)) {
            return false;
        }

        boolean created = database.establish()
                            .query("INSERT INTO users (name) VALUES (?)")
                            .addValue(1, username)
                            .execute();
        database.close();

        return created;
    }

    public User findByUsername(String username) {
        User user = database.establish()
                            .query("SELECT * FROM users WHERE name = ?")
                            .addValue(1, username)
                            .executeReturning()
                            .asValue(User.class);

        database.close();

        return user;
    }

    public boolean login(String username) {
        User user = findByUsername(username);

        return user != null;
    }

}