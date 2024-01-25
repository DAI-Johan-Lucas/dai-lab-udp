package orchestra;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static java.nio.charset.StandardCharsets.UTF_8;
import static orchestra.Logger.LogType.*;

/**
 * TCPReceiver class that will listen to the TCP port and process the messages
 */
public class TCPReceiver implements Runnable {
    private final int PORT;

    TCPReceiver(int port) {
        this.PORT = port;
    }

    /**
     * Run the TCP server
     */
    public void run() {
        Auditor.TCPWorker worker = new Auditor.TCPWorker();
        try (var serverSocket = new ServerSocket(PORT)) {
            Logger.log(INFO, "TCP SERVER start listening on PORT:" + PORT + " HOST:localhost");

            while (true) {
                Socket socket = serverSocket.accept();
                try (socket; var out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), UTF_8)); var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8))) {
                    Logger.log(INFO, "TCP SERVER accepted connection from " + socket.getInetAddress() + ":" + socket.getPort() + " on local port " + socket.getLocalPort());
                    out.write("Welcome to the Orchestra!\nWrite 'quit' to exit and 'refresh' to refresh the list\n");
                    out.flush();

                    boolean running = true;
                    while (running) {
                        out.write(worker.process());
                        out.flush();
                        String line = in.readLine();
                        if (line.equals("quit") || line.equals("q")) {
                            running = false;
                            Logger.log(INFO, "TCP SERVER closing connection with " + socket.getInetAddress() + ":" + socket.getPort() + " on local port " + socket.getLocalPort());
                        }
                    }
                } catch (IOException e) {
                    Logger.log(ERROR, "TCP client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            Logger.log(ERROR, "TCP server: " + e.getMessage());
        }
    }
}
