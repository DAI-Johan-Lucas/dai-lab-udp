package orchestra.musician;

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

        // Créer une instance de Musician avec l'instrument spécifié
        Musician musician = new Musician(args[0].toUpperCase());

        System.out.println("Musician with a " + args[0] + " created");
        System.out.println("Sounds sent :");

        // Créer un Timer pour planifier l'envoi périodique (toutes les secondes)
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try (DatagramSocket socket = new DatagramSocket()) {
                    // Construire le message json à envoyer contenant les informations du musicien
                    Gson gson = new Gson();
                    String musicianInfo =
                            gson.toJson(musician);
                    byte[] musicianData = musicianInfo.getBytes(UTF_8);

                    // Envoyer le message json à l'adresse IP de multicast et au port spécifié
                    var dest_address = new InetSocketAddress(IPADDR, PORT);
                    var packet = new DatagramPacket(musicianData, musicianData.length, dest_address);

                    socket.send(packet);
                    System.out.print("*");

                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }, 0, 1000);
    }
}