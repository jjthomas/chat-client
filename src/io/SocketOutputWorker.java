package io;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SocketOutputWorker extends Thread {
    private PrintWriter out;
    private BlockingQueue<String> bq = new LinkedBlockingQueue<String>();
    
    public SocketOutputWorker(Socket s) throws IOException {
        out = new PrintWriter(s.getOutputStream(), true);
    }
    
    // add null to terminate this worker
    public void add(String toWrite) {
        bq.add(toWrite);
    }
    
    @Override
    public void run() {
        String toWrite = null;
        try {
            while (!out.checkError() && (toWrite = bq.take()) != null) {
                out.write(toWrite + "\n"); // TODO check (DEBUG only)
            }
        } catch (InterruptedException impossible) {}
    }
}
