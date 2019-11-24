package server.services;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import server.database.Database;

public class UserServiceTest {
    private UserService u;

    @Before
    public void setUp() {
        Database db = new Database() {
            private List<String> usernames = new ArrayList<>();
            private boolean willPass = false;
            private boolean login = false;
            public Database start() {
                usernames.add("Pekka");
                usernames.add("Gabriel");
                return this;
            }
            public Database establish() { return this; }
            public Database query(String q) {
                if (q.contains("SELECT name FROM")) login = true;
                return this; 
            }
            public <T> Database addValue(int p, T v) {
                if (login) {
                    willPass = usernames.contains(v);
                } else {
                    willPass = !usernames.contains(v);
                }

                return this; 
            }
            public boolean execute() { return willPass; }
            public void close() {}
        }.start();
        u = new UserService(db);
    }

    @Test
    public void testCreateUserWithNewName() {
        boolean a = u.create("Peter");

        assertTrue("Username could not be created", a);
    }

    @Test
    public void testCreateUserWithExistingName() {
        boolean a = u.create("Pekka");

        assertTrue("Username was created even though it existed before ", !a);
    }

    @Test
    public void testUserShouldNotBeAbleToCreateUsernameThatIsReserved() {
        boolean a = u.create(UserService.RESERVED_USERNAME);

        assertTrue("Reserved username should not be able to be created", !a);
    }

    @Test
    public void testLoginWithExistingName() {
        boolean a = u.login("Gabriel");

        assertTrue("User could not login with valid username", a);
    }

    @Test
    public void testLoginWithNonExistingName() {
        boolean a = u.login("Simon");

        assertTrue("User could login with invalid username", !a);
    }
}