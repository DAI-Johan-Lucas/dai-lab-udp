package org.Auditor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TCPServer implements Runnable {
    private final int port;

    TCPServer(int port) {
        this.port = port;
    }

    public void run() {
        System.out.println("Start TCP server - Port " + port);
        Auditor.TCPWorker worker = new Auditor.TCPWorker();
        try (var serverSocket = new ServerSocket(port)) {
             while (true) {
                 Socket socket = serverSocket.accept();
                 try (socket;
                      var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8));
                      var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8))) {
                        boolean running = true;
                        while (running) {
                            out.write(worker.process());
                            out.flush();
                            String line = in.readLine();
                            if (line.equals("quit") || line.equals("q")) {
                                running = false;
                            }
                        }
                 } catch (IOException e) {
                     System.err.println("TCP Serveur client: " + e.getMessage());
                 }
             }
        } catch (IOException e) {
            System.out.println("TCP Serveur server: " + e.getMessage());
        }
    }
}
