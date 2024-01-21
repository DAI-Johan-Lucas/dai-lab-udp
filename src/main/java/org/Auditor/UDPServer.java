package org.Auditor;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class UDPServer implements Runnable{
    private final int port;
    private final int numThreads;

    UDPServer(int port, int numThreads){
        this.port = port;
        this.numThreads = numThreads;
    }

    public void run(){
        System.out.println("Start UDP server - Port " + port +
                " - Thread pool with " + numThreads + " threads");
        Auditor.UDPWorker worker = new Auditor.UDPWorker();
        try (var serverSocket = new DatagramSocket(port);
             ExecutorService executor = Executors.newFixedThreadPool(numThreads)) {
            while (true) {
                try {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    serverSocket.receive(packet);

//                    System.out.println("Received packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort());

                    var handler = new UDPHandler(packet, worker);
                    executor.execute(handler);
                } catch (IOException e) {
                    System.err.println("UDP Serveur client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("UDP Serveur server: " + e.getMessage());
        }
    }
}

class UDPHandler implements Runnable{
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