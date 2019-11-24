package server.models;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UserTest {
    @Test
    public void testEmptyConstructor() {
        User u = new User();
        assertTrue("Empty Constructor should work ", u != null && u.getId() == 0 && u.getName() == null);
    }

    @Test
    public void testNormalConstructor() {
        User u = new User("James");
        assertTrue("Normal constructor should work ", u != null && u.getId() == 0 && u.getName().equals("James"));
    }
}