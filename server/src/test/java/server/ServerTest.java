package server;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Before;
import org.junit.Test;

public class ServerTest {
    private Server server;

    @Before
    public void setup() throws IOException {
        ServerSocket socket = new ServerSocket() {
            private int invoke;
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
        };
        this.server = new Server(socket, null);
    }

    @Test
    public void testServerClosedOnIOException() {
        this.server.launch();
        assertTrue("Server should be closed, but is open", this.server.getServer().isClosed());
    }
}