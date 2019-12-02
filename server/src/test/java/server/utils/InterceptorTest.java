package server.utils;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import server.database.Database;
import server.services.UserService;

public class InterceptorTest {
    private Interceptor interceptor;

    @Before
    public void setUp() {
        Database database = new Database() {};
        UserService userServiceFake = new UserService(database) {
            List<String> usernames;
            public UserService start() {
                usernames = new ArrayList<>();
                usernames.add("William");
                return this;
            }
            public boolean login(String username) {
                if (usernames.contains(username)) {
                    return true;
                }
                return false;
            }
            public boolean create(String username) {
                if (!usernames.contains(username)) {
                    return true;
                }
                return false;
            }
        }.start();

        this.interceptor = new Interceptor(userServiceFake);
    }

    @Test
    public void testParseMessageDoesNothingOnNull() {
        String expected = interceptor.parse(null);
        assertTrue("Invalid message should be returned", "E:INVALID".equals(expected));
    }

    @Test
    public void testParseMessageDoesNothingOnEmptyString() {
        String expected = interceptor.parse("");
        assertTrue("Invalid message should be returned", "E:INVALID".equals(expected));
    }

    @Test
    public void testParseMessageDoesNothingOnInvalidCommand() {
        String expected = interceptor.parse("k:f:i:");
        assertTrue("Invalid message should be returned", "E:INVALID".equals(expected));
    }

    @Test
    public void testParseMessageDoesNothingOnEmptyAction() {
        String expected = interceptor.parse("USER:insert");
        assertTrue("Invalid message should be returned", "E:INVALID".equals(expected));
    }

    @Test
    public void testParseMessageCreateUser() {
        String expected = interceptor.parse("USER:CREATE:Samuel");
        assertTrue("Successful create user message should be returned", "S:USER:CREATE:Samuel".equals(expected));
    }

    @Test
    public void testParseMessageLoginUser() {
        String expected = interceptor.parse("USER:LOGIN:William");
        assertTrue("Successful login user message should be returned", "S:USER:LOGIN:William".equals(expected));
    }

    @Test
    public void testParseMessageCreateUserUnsuccessfully() {
        String expected = interceptor.parse("USER:CREATE:William");
        assertTrue("Successful create user message should be returned", "E:USER:CREATE:William".equals(expected));
    }

    @Test
    public void testParseMessageLoginUserUnsuccessfully() {
        String expected = interceptor.parse("USER:LOGIN:Samuel");
        assertTrue("Successful login user message should be returned", "E:USER:LOGIN:Samuel".equals(expected));
    }
}