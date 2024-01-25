package orchestra;

import com.google.gson.Gson;

import java.util.*;

import static orchestra.Logger.LogType.*;

/**
 * orchestra.Auditor class that will listen to the musicians and print the active ones
 */
public class Auditor {
    /**
     * HashMap containing all the currently active musicians with their last activity
     */
    static HashMap<String, MusicianData> musicians = new HashMap<>();

    public static void main(String[] args) {
        final int TCP_PORT = 2205;
        final int UDP_PORT = 9904;
        final String UDP_ADDRESS = "239.255.22.5";

        // Start listening to the clients on TCP
        Thread treadTcp = new Thread(new TCPReceiver(TCP_PORT));
        treadTcp.start();

        // Start listening to the musicians on UDP (multicast)
        Thread treadUdp = new Thread(new MulticastReceiver(UDP_PORT, UDP_ADDRESS));
        treadUdp.start();
    }

    /**
     * TCPWorker class that will process the TCP requests
     */
    record TCPWorker() {
        public String process() {
            Gson gson = new Gson();

            return gson.toJson(musicians.values().stream().toList());
        }
    }

    /**
     * UDPWorker class that will process the UDP requests by adding the musicians to the HashMap and scheduling their removal
     */
    record UDPWorker() {
        public void process(String message) {
            Gson gson = new Gson();
            MulticastSender.MulticastStruct rcpt = gson.fromJson(message, MulticastSender.MulticastStruct.class);

            MusicianData musicianData = new MusicianData(rcpt.uuid(), Instrument.fromSound(rcpt.sound()), System.currentTimeMillis());
            //musicianData = new MusicianData(rcpt.uuid(), Instrument.fromSound(rcpt.sound()), System.currentTimeMillis());

            if(musicians.containsKey(musicianData.uuid)){
                musicians.get(musicianData.uuid).setLastActivity(System.currentTimeMillis());
            } else {
                Logger.log(INFO, "ADD musician: " + musicianData.getUuid());
                musicians.put(musicianData.uuid, musicianData);
            }

            // Schedule removal after 5 seconds
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    cleanMusicians(musicianData.getUuid());
                }
            }, 5000);
        }
    }

    /**
     * Clean the musicians HashMap by removing the inactive ones
     *
     * @param uuid The uuid of the musician to remove
     */
    public static void cleanMusicians(String uuid) {
        MusicianData musicianData = musicians.get(uuid);
        if (musicianData != null && musicianData.getLastActivity() + 5000 < System.currentTimeMillis()) {
            Logger.log(INFO, "\tREMOVE musician: " + uuid);
            musicians.remove(uuid);
        } else if (musicianData != null) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    cleanMusicians(musicianData.getUuid());
                }
            }, (5000 - (System.currentTimeMillis() - musicianData.getLastActivity())));
        }
    }

    /**
     * MusicianData class that contains the musician and its last activity
     */
    private static class MusicianData {
        private final String uuid;
        private final Instrument instrument;
        private long lastActivity;

        public MusicianData(String uuid, Instrument instrument, long lastActivity) {
            this.uuid = uuid;
            this.instrument = instrument;
            this.lastActivity = lastActivity;
        }

        public String getUuid() {
            return uuid;
        }

        public Instrument getInstrument() {
            return instrument;
        }

        public long getLastActivity() {
            return lastActivity;
        }

        public void setLastActivity(long lastActivity) {
            this.lastActivity = lastActivity;
        }

    }
}