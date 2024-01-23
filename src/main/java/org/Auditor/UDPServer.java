package org.Auditor;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class UDPServer implements Runnable{
    private final int PORT;
    private final String IPADDRESS;

    UDPServer(int port, String ipaddress){
        this.PORT = port;
        IPADDRESS = ipaddress;
    }

    public void run(){
        System.out.println("Start UDP server - Port " + PORT + " - IP " + IPADDRESS);
        Auditor.UDPWorker worker = new Auditor.UDPWorker();
        try (MulticastSocket socket = new MulticastSocket(PORT)) {
            var group_address = new InetSocketAddress(IPADDRESS, PORT);
            NetworkInterface netif = NetworkInterface.getByName("loopback_0");//getByName("eth0");
            while (true) {
                try {
                    socket.joinGroup(group_address, netif);
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    socket.receive(packet);

                    String message = new String(packet.getData(), 0, packet.getLength(), UTF_8);
                    worker.process(message);
                } catch (IOException e) {
                    System.err.println("UDP Serveur client: " + e.getMessage());
                }finally {
                    socket.leaveGroup(group_address, netif);
                }
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}