package server.database;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.ParameterMetaData;
import java.sql.SQLException;
import java.util.List;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import server.database.Database;
import server.services.UserService;
import server.models.User;

public class DatabaseTest {
    private Database db;
    private final PrintStream originalErr = System.err;
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    @Before
    public void setUp(){
        this.db = new Database();
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void tearDown() {
        db.close();
        System.setErr(originalErr);
    }

    @Test
    public void testsSuccessfulDbConnection() {
        Database k = db.establish();
        assertTrue(k.getDb() != null);

    }

    @Test
    public void testQueryWithSuccessfulConnection() throws SQLException {
        Database k = db.establish();
        k.query("SELECT * FROM users");
        assertTrue(k.getStatement() != null);
    }

    @Test
    public void testQueryWithUnSuccessfulConnection() throws SQLException {
        db.query("SELECT * FROM users");
        assertTrue(db.getStatement() == null);
    }

    @Test
    public void testAddingGenericValue() throws SQLException {
        Database k = db.establish();
        k.query("SELECT * FROM users WHERE name = ?");
        k.addValue(1, "James");
        ParameterMetaData data = k.getStatement().getParameterMetaData();
        int paramCount = data.getParameterCount();
        // this is the best we can do since sqlite hides all parameter values and their types in jdbc
        // calling getParameterType() or getParameterTypeName() will always resort in VARCHAR or String since weverything is hidden
        assertTrue("Parameter count was expected to be " + 1 + " but was " + paramCount, paramCount == 1);
    }

    @Test
    public void testAddingMultipleGenericValues() throws SQLException {
        Database k = db.establish();
        k.query("SELECT content,id,timestamp FROM messages WHERE content = ? AND id = ? AND timestamp = ?");
        k.addValue(1, "Lmao");
        k.addValue(2, 1);
        k.addValue(3, 1L);
        ParameterMetaData data = k.getStatement().getParameterMetaData();
        int paramCount = data.getParameterCount();
        int expectedCount = 3;

        assertTrue("Parameter count was expected to be " + expectedCount + " but was " + paramCount, paramCount == expectedCount);
    }

    @Test
    public void testAddValueDoNothingIfNoQueryExists() {
        Database k = db.establish().addValue(1, "james");
        assertTrue("No values should have been added", k.getStatement() == null);
    }

    @Test
    public void testExecuteDoesNothingIfNoQueryExists() {
        int no  = db.establish().execute();
        assertTrue("No queries should have been executed", no == 0);
    }

    @Test
    public void testExecuteReturningDoesNothingIfNoQueryExists() {
        Database k  = db.establish().executeReturning();
        assertTrue("No results should have been returned", k.getResult() == null);
    }

    @Test
    public void testQueryExecution() throws SQLException {
        // Note! JUnit executes tests in arbitrary order
        // ideally tests cases should be separeted but we need to execute these in precise order
        Database k = db.establish();

        int a = k.query("INSERT INTO users (name) VALUES (?)")
                        .addValue(1, UserService.RESERVED_USERNAME)
                        .execute();

        assertTrue("User was not created", a > 0);

        User r = k.query("SELECT * FROM users WHERE name = ?")
                            .addValue(1, UserService.RESERVED_USERNAME)
                            .executeReturning()
                            .asValue(User.class);

        if (r == null) {
            fail("User could not be retrieved");
        } else {
            String username = r.getName();
            assertTrue("Test user was not successfully created or could not be found" , UserService.RESERVED_USERNAME.equals(username));
        }

        int b = k.query("DELETE FROM users WHERE name = ?")
                        .addValue(1, UserService.RESERVED_USERNAME)
                        .execute();

        assertTrue("User not deleted", b > 0);

    }

    @Test
    public void testListingEntities() throws SQLException {
        Database k = db.establish();

        k.query("INSERT INTO users (name) VALUES (?)")
            .addValue(1, UserService.RESERVED_USERNAME)
            .execute();
        
        List<User> users = k.query("SELECT * FROM users")
                                .executeReturning()
                                .asList(User.class);
        
        User userInstance = k.query("SELECT * FROM users")
                            .executeReturning()
                            .asValue(User.class);

        k.query("DELETE FROM users WHERE name = ?")
            .addValue(1, UserService.RESERVED_USERNAME)
            .execute();
            
        User user = users.stream().filter(u -> {
            return UserService.RESERVED_USERNAME.equals(u.getName());
        }).findFirst().orElse(null);

        assertTrue("Users list should not be empty", !users.isEmpty());
        assertTrue("User instance should not be null", userInstance != null);
        assertTrue("Users list should contain test user entity", user != null);
    }

    @Test
    public void testListingShouldBeEmpty() throws SQLException {
        Database k = db.establish();
        
        List<User> users = k.query("SELECT * FROM users")
                                .asList(User.class);

        assertTrue("Users list should be empty", users.isEmpty());
    }

    @Test
    public void testFindOneEntity() throws SQLException {
        Database k = db.establish();

        User user = k.query("SELECT * FROM users")
                        .asValue(User.class);

        assertTrue("User should be null", user == null);
    }
}