package server.services;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import server.database.Database;
import server.models.Model;
import server.models.User;

public class UserServiceTest {
    private UserService u;

    @Before
    public void setUp() {
        Database db = new Database() {
            private List<String> usernames = new ArrayList<>();
            private int willPass;
            private boolean login = false;
            private boolean fetching = false;
            private boolean inserting = false;
            public Database start() {
                usernames.add("Pekka");
                usernames.add("Gabriel");
                return this;
            }
            public Database establish() { return this; }
            public Database query(String q) {
                if (q.contains("SELECT * FROM") && !q.contains("id")) {
                    login = true;
                }
                if (q.contains("id")) fetching = true;
                if (q.contains("INSERT")) inserting = true;
                return this; 
            }
            public <T> Database addValue(int p, T v) {
                if (!fetching) {
                    if (login) {
                        willPass = usernames.contains(v) ? 1 : 0;
                    } else {
                        willPass = !usernames.contains(v) ? 1 : 0;
                    }
                }

                return this; 
            }
            public int execute() { return willPass; }
            public Database executeReturning() { return this; }
            public <T extends Object & Model> T asValue(Class<T> model) {
                if (willPass > 0) {
                    return (T) new User("Gabriel");
                }

                return null;
            }
            public void close() {}
        }.start();
        u = new UserService(db);
    }

    @Test
    public void testCreateUserWithNewName() {
        User a = u.create("Peter");

        assertTrue("Username could not be created", a != null);
    }

    @Test
    public void testCreateUserWithExistingName() {
        User a = u.create("Pekka");

        assertTrue("Username was created even though it existed before ", a == null);
    }

    @Test
    public void testUserShouldNotBeAbleToCreateUsernameThatIsReserved() {
        User a = u.create(UserService.RESERVED_USERNAME);

        assertTrue("Reserved username should not be able to be created", a == null);
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

    @Test
    public void testFindUserByUsername() {
        User user = u.findByUsername("Pekka");

        assertTrue("Valid user should have been found", user != null);
    }

    @Test
    public void testDontFindUserByUsername() {
        User user = u.findByUsername("Samuel");

        assertTrue("Valid user should not have been found", user == null);
    }
}