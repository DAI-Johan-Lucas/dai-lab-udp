package org.example;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;

import static java.nio.charset.StandardCharsets.*;

class MulticastSender {
    final static String IPADDR = "localhost";
    final static int PORT = 9904;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java MulticastSender <instrument>");
            System.exit(1);
        }

        // Convertir la chaîne d'instrument en une instance de l'énumération Instrument
        Instrument instrument = Instrument.valueOf(args[0].toUpperCase());
        // Créer une instance de Musician avec l'instrument spécifié
        Musician musician = new Musician(instrument);

        // Créer un Timer pour planifier l'envoi périodique (toutes les secondes)
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try (DatagramSocket socket = new DatagramSocket()) {
                    // Mettre à jour l'activité du musicien
                    musician.setLastActivity(System.currentTimeMillis());

                    // Construire le message json à envoyer contenant les informations du musicien
                    Gson gson = new Gson();
                    String musicianInfo =
                            gson.toJson(new UDPSendStruct(musician));
                    byte[] musicianData = musicianInfo.getBytes(UTF_8);

                    // Envoyer le message json à l'adresse IP de multicast et au port spécifié
                    var dest_address = new InetSocketAddress(IPADDR, PORT);
                    var packet = new DatagramPacket(musicianData, musicianData.length, dest_address);
                    socket.send(packet);
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }, 0, 1000);
    }
}
record UDPSendStruct(String uuid, String sound) {
    public UDPSendStruct(Musician musician) {
        this(musician.getUuid(), musician.getSound());
    }
}

record Musician2(String uuid, Instrument instrument, long lastActivity) {
    public String getSound() {
        return instrument.sound();
    }
}