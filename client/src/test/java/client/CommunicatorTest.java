package client;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import client.models.Message;

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
                if ("MESSAGE".equals(splitted[0]) && "CREATE".equals(splitted[1])) {
                    String[] message = Arrays.copyOfRange(splitted, 2, splitted.length);
                    String newMessage = String.join("|", message).trim();

                    m.message = splitted[0]+":"+splitted[1]+":1|"+newMessage;
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
                if (m.message.contains("Sam")) {
                    return "S:" + m.message;
                }
                if (m.message.contains("MESSAGE:CREATE")) {
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
        Message expected = new Message(1, "Hello", 2151251, 1);
        boolean yes = communicator.sendMessage("MESSAGE:CREATE:Hello:2151251:1");
        Message message = communicator.getPayload(Message.class);
        if (!yes) fail("Message should have been created but failed!");
        boolean equal = expected.getContent().equals(message.getContent()) && 
                        expected.getId() == message.getId() && 
                        expected.getUserId() == message.getUserId() &&
                        expected.getTimestamp() == message.getTimestamp();
        assertTrue("expected " + expected.toString() + " but got " + message.toString(), equal);
    }
}