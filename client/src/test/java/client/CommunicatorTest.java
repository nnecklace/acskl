package client;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Before;
import org.junit.Test;

public class CommunicatorTest {
    Communicator communicator;

    class Message {
        String message;

        public Message(String message) {
            this.message = message;
        }
    }

    @Before
    public void setUp() {
        Message m = new Message("");
        DataOutput output = new DataOutput() {
            public void writeBytes(String s) {
                m.message = s;
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

                return "E:" + m.message; 
            }
        };
        communicator = new Communicator(output, input);
    }

    @Test
    public void testSuccessfullMessage() {
        boolean expected = communicator.sendMessage("USER:CREATE:Sam");
        assertTrue("Message should have been successful", expected);
    }

    @Test
    public void testUnSuccessfullMessage() {
        boolean expected = communicator.sendMessage("USER:CREATE:William");
        assertTrue("Message should have been unsuccessful", !expected);
    }
}