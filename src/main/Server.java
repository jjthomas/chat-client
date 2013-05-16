package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import server.ServerInputProcessor;

/**
 * Chat server runner.
 */
public class Server {

    /**
     * Start a chat server. The first argument should be the desired
     * port number. The port number will be set to 5000 if there are no
     * arguments.
     */
    public static void main(String[] args) throws IOException {
        int port = 5000;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        new Server().run(port);
    }
    
    public void run(int port) throws IOException {
        ServerInputProcessor sip = new ServerInputProcessor();
        ServerSocket ss = new ServerSocket(port);   
        while(true) {
            Socket s = ss.accept();
            sip.addClient(s);
        }
    }
}
