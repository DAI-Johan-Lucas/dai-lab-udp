package orchestra.auditor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TCPReceiver implements Runnable {
    private final int PORT;

    TCPReceiver(int port) {
        this.PORT = port;
    }

    public void run() {
        Auditor.TCPWorker worker = new Auditor.TCPWorker();
        try (var serverSocket = new ServerSocket(PORT)) {
            System.out.println("\033[0;34m" + "[INFO]" + "\033[0m"
                    + " TCP SERVER start listening on PORT:" + PORT + " HOST:localhost");
            while (true) {
                Socket socket = serverSocket.accept();
                try (socket; var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8));
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
                    System.err.println("TCP client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("TCP server: " + e.getMessage());
        }
    }
}
