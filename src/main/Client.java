package main;

import java.io.IOException;

import client.ClientInputProcessor;
import client.ui.MainWindow;

/**
 * GUI chat client runner.
 */
public class Client {

    /**
     * Start a GUI chat client. The server runs on port 5000
     * by default.
     */
    public static void main(String[] args) throws IOException {
        MainWindow mw = new MainWindow();
        mw.setController(new ClientInputProcessor());
        mw.start(MainWindow.HOSTNAME_INTRO);
    }
}
