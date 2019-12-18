package server.utils;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import server.database.Database;
import server.models.Message;
import server.models.User;
import server.services.MessageService;
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
            public User login(String username) {
                if (usernames.contains(username)) {
                    return new User(username);
                }
                return null;
            }
            public User create(String username) {
                if (!usernames.contains(username)) {
                    return new User(username);
                }
                return null;
            }
        }.start();

        MessageService messageServiceFake = new MessageService(database) {
            List<Message> messages;
            public Message create(String content, int timestamp, int userId) {
                if (userId == 0) return null;
                return new Message(content, timestamp, userId);
            }
            public List<Message> getAll() {
                messages = new ArrayList<>();
                messages.add(new Message("ayy lmao", 5555, 1));
                messages.add(new Message("lol", 123, 9));
                return messages;
            }
        };

        this.interceptor = new Interceptor(userServiceFake, messageServiceFake);
    }

    @Test
    public void testParseMessageDoesNothingOnNull() {
        String actual = interceptor.parse(null);
        assertTrue("Invalid message should be returned", "E:INVALID".equals(actual));
    }

    @Test
    public void testParseMessageDoesNothingOnEmptyString() {
        String actual = interceptor.parse("");
        assertTrue("Invalid message should be returned", "E:INVALID".equals(actual));
    }

    @Test
    public void testParseMessageDoesNothingOnInvalidCommand() {
        String actual = interceptor.parse("k:f:i:");
        assertTrue("Invalid message should be returned", "E:INVALID".equals(actual));
    }

    @Test
    public void testParseMessageDoesNothingOnEmptyAction() {
        String actual = interceptor.parse("USER:insert");
        assertTrue("Invalid message should be returned", "E:INVALID".equals(actual));
    }

    @Test
    public void testParseMessageCreateUser() {
        String actual = interceptor.parse("USER:CREATE:Samuel");
        assertTrue("Successful create user message should be returned", "S:USER:CREATE:0|Samuel".equals(actual));
    }

    @Test
    public void testParseMessageLoginUser() {
        String actual = interceptor.parse("USER:LOGIN:William");
        assertTrue("Successful login user message should be returned", "S:USER:LOGIN:0|William".equals(actual));
    }

    @Test
    public void testParseMessageCreateUserUnsuccessfully() {
        String actual = interceptor.parse("USER:CREATE:William");
        assertTrue("Successful create user message should be returned", "E:USER:CREATE".equals(actual));
    }

    @Test
    public void testParseMessageLoginUserUnsuccessfully() {
        String actual = interceptor.parse("USER:LOGIN:Samuel");
        assertTrue("Successful login user message should be returned", "E:USER:LOGIN".equals(actual));
    }

    @Test
    public void testParseMessageCreateMessageSuccessfully() {
        String expected = "S:MESSAGE:CREATE:0|ayy lmao|12444|null";
        String actual = interceptor.parse("MESSAGE:CREATE:ayy lmao:12444:4");
        assertTrue("Expected " + expected + " but got " + actual, expected.equals(actual));
    }

    @Test
    public void testParseMessageCreateUnsuccessfully() {
        String expected = "E:MESSAGE:CREATE";
        String actual = interceptor.parse("MESSAGE:CREATE:ayy lmao:12444:0");
        assertTrue("Expected " + expected + " but got " + actual, expected.equals(actual));
    }

    @Test
    public void testParseMessageListSuccessfully() {
        String expected = "S:MESSAGE:LIST:0|ayy lmao|5555|null:0|lol|123|null";
        String actual = interceptor.parse("MESSAGE:LIST");
        assertTrue("Expected " + expected + " but got " + actual, expected.equals(actual));
    }
}