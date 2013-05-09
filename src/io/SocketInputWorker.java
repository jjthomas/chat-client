package io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public abstract class SocketInputWorker extends Thread {
    private BufferedReader in;
    protected static final String DISCONNECT = "disconnect";
    
    public SocketInputWorker(Socket s) throws IOException {
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    }
    
    @Override
    public void run() {
        String input = null;
        try {
            while ((input = in.readLine()) != null) {
                process(input);
            }
        } catch (IOException e) {
            // Thread will end, which is the only behavior
            // we want.
        }
        process(DISCONNECT);
    }
    
    public abstract void process(String input);
    
}
