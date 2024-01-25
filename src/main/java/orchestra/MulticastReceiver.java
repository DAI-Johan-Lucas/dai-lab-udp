package orchestra;

import java.io.*;
import java.net.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static orchestra.Logger.LogType.*;

public class MulticastReceiver implements Runnable {
    private final int PORT;
    private final String IPADDRESS;

    MulticastReceiver(int port, String ipaddress) {
        this.PORT = port;
        IPADDRESS = ipaddress;
    }

    public void run() {
        Auditor.UDPWorker worker = new Auditor.UDPWorker();
        try (MulticastSocket socket = new MulticastSocket(PORT)) {
            Logger.log(INFO, "UDP SERVER start listening on PORT:" + PORT + " HOST:" + IPADDRESS);

            var group_address = new InetSocketAddress(IPADDRESS, PORT);
            NetworkInterface netif = NetworkInterface.getByName("loopback_0"); //getByName("eth0");

            // L'auditor rejoint le groupe de multicast pour y recevoir les sons des musiciens
            socket.joinGroup(group_address, netif);
            Logger.log(SUCCESS, "UDP SERVER joined multicast group: " + IPADDRESS + ":" + PORT + " on interface: " + netif.getName());

            try {
                while (true) {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    socket.receive(packet);

                    String message = new String(packet.getData(), 0, packet.getLength(), UTF_8);
                    worker.process(message);
                }
            } catch (IOException e) {
                Logger.log(ERROR, "UDP client: " + e.getMessage());
            } finally {
                socket.leaveGroup(group_address, netif);
                Logger.log(INFO, "UDP SERVER left multicast group: " + IPADDRESS + ":" + PORT);
            }
        } catch (IOException e) {
            Logger.log(ERROR, "UDP server: " + e.getMessage());
        }
    }
}