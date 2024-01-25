package orchestra.auditor;

import java.io.*;
import java.net.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MulticastReceiver implements Runnable{
    private final int PORT;
    private final String IPADDRESS;

    MulticastReceiver(int port, String ipaddress){
        this.PORT = port;
        IPADDRESS = ipaddress;
    }

    public void run(){
        Auditor.UDPWorker worker = new Auditor.UDPWorker();
        try (MulticastSocket socket = new MulticastSocket(PORT)) {
            System.out.println("\033[0;34m" + "[INFO]" + "\033[0m"
                    + " UDP SERVER start listening on PORT:" + PORT + " HOST:" + IPADDRESS);
            var group_address = new InetSocketAddress(IPADDRESS, PORT);
            NetworkInterface netif = NetworkInterface.getByName("loopback_0"); //getByName("eth0");
            socket.joinGroup(group_address, netif);
            try {
                while (true) {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    socket.receive(packet);

                    String message = new String(packet.getData(), 0, packet.getLength(), UTF_8);
                    worker.process(message);
                }
            } catch (IOException e) {
                System.err.println("UDP client: " + e.getMessage());
            }finally {
                socket.leaveGroup(group_address, netif);
            }
        } catch (IOException e) {
            System.err.println("UDP server: " + e.getMessage());
        }
    }
}