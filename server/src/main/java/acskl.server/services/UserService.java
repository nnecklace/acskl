package acskl.server.services;

import acskl.server.models.User;
import java.sql.SQLException;
import acskl.server.database.Database;

public class UserService {
    private Database database;

    public UserService(Database database) {
        this.database = database;
    }

    public void create(User user) {
        try {
            database.establish()
                .query("INSERT INTO users VALUES (?)")
                .addValue(user.getName())
                .execute();
        } catch(SQLException exception) {
            System.err.println("Could not create user " + exception.getMessage());
        }
    }
}