package org.Auditor;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPServer implements Runnable {
    final static String IPADDRESS = "239.255.22.5";
    private final int port;
    private final int numThreads;

    UDPServer(int port, int numThreads) {
        this.port = port;
        this.numThreads = numThreads;
    }

    public void run() {
        System.out.println("Start UDP server - Port " + port + " - Thread pool with " + numThreads + " threads");
        Auditor.UDPWorker worker = new Auditor.UDPWorker();

        try (MulticastSocket socket = new MulticastSocket(port); ExecutorService executor = Executors.newFixedThreadPool(numThreads)) {
            var group_address = new InetSocketAddress(IPADDRESS, port);
            NetworkInterface netif = NetworkInterface.getByName("eth0");
            socket.joinGroup(group_address, netif);
            while (true) {
                try {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

//                    System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort());

                    var handler = new UDPHandler(packet, worker);
                    executor.execute(handler);
                } catch (IOException e) {
                    System.err.println("UDP Serveur client: " + e.getMessage());
                }
            }
            //socket.leaveGroup(group_address, netif);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    class UDPHandler implements Runnable {
        private final DatagramPacket packet;
        private final Auditor.UDPWorker worker;

        public UDPHandler(DatagramPacket packet, Auditor.UDPWorker worker) {
            this.packet = packet;
            this.worker = worker;
        }

        @Override
        public void run() {
            try {
                String message = new String(packet.getData(), 0, packet.getLength());
                worker.process(message);
            } catch (Exception e) {
                System.err.println("UDP Handler: " + e);
            }
        }
    }
}