package client;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import client.models.Message;
import client.models.User;

public class CommunicatorTest {
    Communicator communicator;

    class M {
        String message;
    }

    @Before
    public void setUp() {
        M m = new M() {
        };
        DataOutput output = new DataOutput() {
            public void writeBytes(String s) {
                String[] splitted = s.split(":");
                if ("MESSAGE".equals(splitted[0])) {
                    if ("CREATE".equals(splitted[1])) {
                        String[] message = Arrays.copyOfRange(splitted, 2, splitted.length);
                        String newMessage = String.join("|", message).trim();

                        m.message = splitted[0]+":"+splitted[1]+":1|"+newMessage;
                    }

                    if ("LIST".equals(splitted[1].trim())) {
                        m.message = splitted[0]+":"+splitted[1]+":2|Hello!|2151251|1:3|World!|3555552|2:3|Fudge!|3523525|3"; 
                    }
                } else {
                    m.message = s;
                }
            }
            public void writeShort() {
            }
            public void writeByte() {
            }
            @Override
            public void write(int b) throws IOException {
            }
            @Override
            public void write(byte[] b) throws IOException {
            }
            @Override
            public void write(byte[] b, int off, int len) throws IOException {
            }
            @Override
            public void writeBoolean(boolean v) throws IOException {
            }
            @Override
            public void writeByte(int v) throws IOException {
            }
            @Override
            public void writeShort(int v) throws IOException {
            }
            @Override
            public void writeChar(int v) throws IOException {
            }
            @Override
            public void writeInt(int v) throws IOException {
            }
            @Override
            public void writeLong(long v) throws IOException {}
            @Override
            public void writeFloat(float v) throws IOException {}
            @Override
            public void writeDouble(double v) throws IOException {}
            @Override
            public void writeChars(String s) throws IOException {}
            @Override
            public void writeUTF(String s) throws IOException {}
        };
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in)){
            public String readLine() {
                if (m.message.contains("MESSAGE:CREATE") || m.message.contains("MESSAGE:LIST")) {
                    return "S:" + m.message;
                }

                if (m.message.contains("Sam")) {
                    if (m.message.contains("LOGIN")) {
                        String begin = m.message.substring(0,11);
                        String end = m.message.substring(11);
                        m.message = begin + "1|" + end;
                    }
                    return "S:" + m.message;
                }

                return "E:" + m.message; 
            }
        };
        communicator = new Communicator(output, input);
    }

    @Test
    public void testSuccessfulMessage() {
        boolean expected = communicator.sendMessage("USER:CREATE:Sam");
        assertTrue("Message should have been successful", expected);
    }

    @Test
    public void testUnSuccessfulMessage() {
        boolean expected = communicator.sendMessage("USER:CREATE:William");
        assertTrue("Message should have been unsuccessful", !expected);
    }

    @Test
    public void testSuccessfulCreateMessage() {
        Message expected = new Message(1, "Hello", 2151251, "Simon");
        boolean yes = communicator.sendMessage("MESSAGE:CREATE:Hello:2151251:Simon");
        Message message = communicator.getPayload(Message.class);
        if (!yes) fail("Message should have been created but failed!");
        boolean equal = expected.getContent().equals(message.getContent()) && 
                        expected.getId() == message.getId() && 
                        expected.getAuthor().equals(message.getAuthor()) &&
                        expected.getTimestamp() == message.getTimestamp();
        assertTrue("expected " + expected.toString() + " but got " + message.toString(), equal);
    }

    @Test
    public void testSuccessfulListMessage() {
        Message m1 = new Message(2, "Hello!", 5325223, "Simon");
        Message m2 = new Message(3, "World!", 3555552, "Sam");
        Message m3 = new Message(3, "Fudge!", 3523525, "William");
        List<Message> messages = new ArrayList<>();
        messages.add(m1);
        messages.add(m2);
        messages.add(m3);

        boolean yes = communicator.sendMessage("MESSAGE:LIST");

        if (!yes) fail("MESSAGE:LIST shouldn't fail but did!"); 

        List<Object> response = communicator.getPayload(List.class);

        List<Message> actualMessages = new ArrayList<>();
        
        for (Object o : response) {
            actualMessages.add((Message) o);
        }

        boolean same = true;

        for (int i = 0; i < 3; ++i) {
            Message expectedM = messages.get(i);
            Message actualM = messages.get(i);

            if (!expectedM.getContent().equals(actualM.getContent()) ||
                expectedM.getId() == actualM.getId() ||
                expectedM.getTimestamp() == actualM.getTimestamp() ||
                expectedM.getAuthor().equals(actualM.getAuthor())) {
                    same = false;
                    break;
                }
        }

        assertTrue("Messages did not match with expected message", !same);
    }

    @Test
    public void testSuccessfulLogin() {
        User expected = new User(1, "Sam");
        boolean yes = communicator.sendMessage("USER:LOGIN:Sam");

        if (!yes) fail("Login failed but shouldn't have!");

        User actual = communicator.getPayload(User.class);

        assertTrue("Login user did not match with expected! ", (!expected.getName().equals(actual.getName()) || expected.getId() != actual.getId()));
    }
}