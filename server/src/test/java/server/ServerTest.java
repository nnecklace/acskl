package server;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServerTest {
    private Server server;
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;

    @Before
    public void setup() throws IOException {
        System.setErr(new PrintStream(errContent));
        ServerSocket socket = new ServerSocket() {
            private int invoke;
            private int closeException;
            private boolean close;
            public Socket accept() throws IOException {
                if (invoke > 0) throw new IOException("");
                invoke++;
                return new Socket() {
                    public InputStream getInputStream() {
                        return new InputStream(){
                            @Override
                            public int read() throws IOException {
                                return 0;
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
                };
            }
            public void close() throws IOException {
                if (closeException > 0) throw new IOException("Error");
                closeException++;
                close = true;
            }
            public boolean isClosed() {
                return close;
            }
        };
        this.server = new Server(socket, null);
    }

    @After
    public void tearDown() {
        System.setErr(originalErr);
    }

    @Test
    public void testServerClosedOnIOException() {
        this.server.launch();
        assertTrue("Server should be closed, but is open", this.server.getServer().isClosed());
    }

    @Test
    public void testServerCloseThrowsIOException() {
        this.server.launch();
        this.server.launch();
        assertTrue("Shutdown should throw exception but didn't", errContent.toString().contains("Server could not shutdown: Error"));
    }

}