package server.models;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MessageTest {
    @Test
    public void testEmptyConstructor() {
        Message m = new Message();
        assertTrue("Empty Constructor should work ", m != null && m.getId() == 0 && m.getContent() == null);
    }

    @Test
    public void testNormalConstructor() {
        Message m = new Message("Test", 1, 43);
        assertTrue("Normal constructor should work ", m != null && m.getId() == 0 && m.getContent().equals("Test"));
    }
}