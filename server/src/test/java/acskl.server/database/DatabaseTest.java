package acskl.server.database;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import acskl.server.database.Database;
import acskl.server.services.UserService;

public class DatabaseTest {
    private Database db;
    @Before
    public void setUp(){
        this.db = new Database();
    }

    @After
    public void tearDown() {
        db.close();
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
    public void testQueryExecution() throws SQLException {
        // Note! JUnit executes tests in arbitrary order
        // ideally tests cases should be separeted but we need to execute these in precise order
        Database k = db.establish();

        boolean a = k.query("INSERT INTO users (name) VALUES (?)")
                        .addValue(UserService.RESERVED_USERNAME)
                        .execute();

        assertTrue("User was not created", a);

        ResultSet r = k.query("SELECT * FROM users WHERE name = ?")
                            .addValue(UserService.RESERVED_USERNAME)
                            .executeReturning()
                            .getResult();

        if (r == null) {
            fail("User could not be retrieved");
        } else {
            r.next();
            String username = r.getString(2);
            assertTrue("Test user was not successfully created or could not be found" , UserService.RESERVED_USERNAME.equals(username));
        }

        boolean b = k.query("DELETE FROM users WHERE name = ?")
                        .addValue(UserService.RESERVED_USERNAME)
                        .execute();

        assertTrue("User not deleted", b);

    }
}