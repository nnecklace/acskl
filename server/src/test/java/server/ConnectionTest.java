package server;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.database.Database;
import server.models.Message;
import server.models.User;
import server.services.MessageService;
import server.services.UserService;
import server.utils.Interceptor;

public class ConnectionTest {
    private Connection connection;
    private final PrintStream originalErr = System.err;
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void setUp() throws IOException {
        System.setErr(new PrintStream(errContent));
        Socket s = new Socket() {
            private int counter;
            private boolean close;
            public InputStream getInputStream() {
                return new InputStream(){
                    @Override
                    public int read() throws IOException {
                        return 1;
                    }
                };
            }
            public OutputStream getOutputStream() {
                return new OutputStream(){
                    @Override
                    public void write(int b) throws IOException {
                        
                    }
                };
            }
            public void close() throws IOException {
                if (counter > 0) throw new IOException("Error");
                close = true;
                counter++;
            }
            public boolean isClosed() {
                return close;
            }
        };
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

        Interceptor i = new Interceptor(userServiceFake,messageServiceFake);
        this.connection = new Connection(s,i);

        Reader r = new Reader(){
            @Override
            public int read(char[] cbuf, int off, int len) throws IOException {
                // TODO Auto-generated method stub
                return 0;
            }
            @Override
            public void close() throws IOException {
                // TODO Auto-generated method stub
                
            }
        };

        BufferedReader reader = new BufferedReader(r) {
            int counter;
            public String readLine() throws IOException {
                if (counter > 1) throw new IOException("Error");
                if (counter > 0) {
                    counter++;
                    return null;
                }
                counter++;
                return "USER:LOGIN:William";
            }
        };

        DataOutput writer = new DataOutput() {
            public String output;
            public void writeBytes(String bytes) {
                this.output = bytes;
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
            public void writeLong(long v) throws IOException {

            }

            @Override
            public void writeFloat(float v) throws IOException {

            }

            @Override
            public void writeDouble(double v) throws IOException {

            }

            @Override
            public void writeChars(String s) throws IOException {

            }

            @Override
            public void writeUTF(String s) throws IOException {

            }

            @Override
            public String toString() {
                return output;
            }
        };

        this.connection.setReader(reader);
        this.connection.setWriter(writer);
    }

    @After
    public void restoreStreams() {
        System.setErr(originalErr);
    }

    @Test
    public void testRunWithBasicMessage() {
        String expected = "S:USER:LOGIN:William";
        this.connection.run();
        String actual = this.connection.getWriter().toString();
        assertTrue("Expected that " + expected + " would be returned but got instead " + actual, !expected.equals(actual));
    }

    @Test
    public void testRunExitsOnNull() {
        this.connection.run();
        assertTrue("Expected that connection would be closed on null message", this.connection.isClosed());
    }

    @Test
    public void testRunThrowsIOException() {
        this.connection.run();
        this.connection.run();
        assertTrue("Run should throw exception, but didn't", errContent.toString().contains("Could not get input stream from client: Error"));
    }

    @Test
    public void testShutDownThrowsIOException() {
        this.connection.run();
        this.connection.run();
        assertTrue("Shutdown should throw exception, but didn't", errContent.toString().contains("Could not close connection to client: Error"));
    }
}