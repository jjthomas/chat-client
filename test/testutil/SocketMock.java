package testutil;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketMock extends Socket {
    private InputStream is;
    private OutputStream os;
    public SocketMock(InputStream is, OutputStream os) {
        this.is = is;
        this.os = os;
    }
    
    @Override
    public InputStream getInputStream() {
        return is;
    }
    
    @Override
    public OutputStream getOutputStream() {
        return os;
    }
}
