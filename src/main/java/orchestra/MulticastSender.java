package orchestra;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.*;

import static java.nio.charset.StandardCharsets.*;
import static orchestra.Logger.LogType.*;

/**
 * MulticastSender class that will send the sounds of a musician to the multicast group
 */
class MulticastSender {
    final static String IPADDR = "239.255.22.5";
    final static int PORT = 9904;

    /**
     * MulticastStruct class that will be used to send the musician's information
     * @param uuid  uuid of the musician
     * @param sound sound of the musician
     */
    public record MulticastStruct(String uuid, String sound) {
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            Logger.log(WARNING, "+ Usage: java MulticastSender <instrument>");
            System.exit(1);
        }

        try {
            // Create an instance of Musician with the specified instrument
            Musician musician = new Musician(Instrument.valueOf(args[0].toUpperCase()));

            Logger.log(SUCCESS, "Musician with a " + args[0] + " created");
            Logger.log(INFO, "Sounds sent :");

            // Create a Timer to schedule periodic sending (every second)
            final long[] soundCount = {0};
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try (DatagramSocket socket = new DatagramSocket()) {
                        // Construct the JSON to be sent, containing the musician's information
                        Gson gson = new Gson();
                        String musicianInfo = gson.toJson(new MulticastStruct(musician.uuid(), musician.instrument().getSound()));
                        byte[] musicianData = musicianInfo.getBytes(UTF_8);

                        // Send the JSON message to the multicast IP address and the specified port
                        var dest_address = new InetSocketAddress(IPADDR, PORT);
                        var packet = new DatagramPacket(musicianData, musicianData.length, dest_address);

                        socket.send(packet);

                        // Increment the counter and display the sound
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