package acskl.server.database;

import static org.junit.Assert.assertTrue;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import acskl.server.database.Database;

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
}