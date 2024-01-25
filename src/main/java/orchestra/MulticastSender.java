package orchestra;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.*;

import static java.nio.charset.StandardCharsets.*;
import static orchestra.Logger.LogType.*;

class MulticastSender {
    final static String IPADDR = "239.255.22.5";
    final static int PORT = 9904;

    public record MulticastStruct(String uuid, String sound) {
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            Logger.log(WARNING, "+ Usage: java MulticastSender <instrument>");
            System.exit(1);
        }

        try {
            // Créer une instance de Musician avec l'instrument spécifié
            Musician musician = new Musician(Instrument.valueOf(args[0].toUpperCase()));

            Logger.log(SUCCESS, "Musician with a " + args[0] + " created");
            Logger.log(INFO, "Sounds sent :");

            // Créer un Timer pour planifier l'envoi périodique (toutes les secondes)
            final long[] soundCount = {0};
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try (DatagramSocket socket = new DatagramSocket()) {
                        // Construction du json à envoyer, contenant les informations du musicien
                        Gson gson = new Gson();
                        //String musicianInfo = "{\"uuid\":\"" + musician.uuid() + "\",\"sound\":\"" + musician.instrument().getSound() + "\"}";
                        String musicianInfo = gson.toJson(new MulticastStruct(musician.uuid(), musician.instrument().getSound()));
                        byte[] musicianData = musicianInfo.getBytes(UTF_8);

                        // Envoyer le message json à l'adresse IP de multicast et au port spécifié
                        var dest_address = new InetSocketAddress(IPADDR, PORT);
                        var packet = new DatagramPacket(musicianData, musicianData.length, dest_address);

                        socket.send(packet);

                        // Incrémenter le compteur et afficher le son
                        System.out.println(++soundCount[0] + ": " + musician.instrument().getSound());

                    } catch (IOException e) {
                        Logger.log(ERROR, "UDP client: " + e.getMessage());
                    }
                }
            }, 0, 1000);
        } catch (Exception e) {
            Logger.log(ERROR, "Musician creation: " + e.getMessage());
        }
    }
}