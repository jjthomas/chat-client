package main;

import java.io.IOException;
import java.net.Socket;

import javax.swing.JOptionPane;

/**
 * GUI chat client runner.
 */
public class Client {

    /**
     * Start a GUI chat client.
     */
    public static void main(String[] args) throws IOException {
        String hostname = JOptionPane.showInputDialog(
                "Hostname/IP of server (include port number after colon at end): ");
        String[] components = hostname.split(":");
        int port = 5000;
        if (components.length > 1) {
            port = Integer.parseInt(components[1]);
        }
        Socket s = new Socket(components[0], port);
        Client
    }
}
