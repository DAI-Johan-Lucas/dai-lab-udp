package org.example;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import static java.nio.charset.StandardCharsets.*;

class MulticastSender {
    final static String IPADDR = "239.255.22.5";
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

        try (DatagramSocket socket = new DatagramSocket()) {
            // Mettre à jour l'activité du musicien
            musician.setLastActivity(System.currentTimeMillis());

            // Construire le message json à envoyer contenant les informations du musicien
            Gson gson = new Gson();
            String musicianInfo = gson.toJson(musician);
            byte[] musicianData = musicianInfo.getBytes(UTF_8);

            var dest_address = new InetSocketAddress(IPADDR, PORT);
            var packet = new DatagramPacket(musicianData, musicianData.length, dest_address);
            socket.send(packet);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}