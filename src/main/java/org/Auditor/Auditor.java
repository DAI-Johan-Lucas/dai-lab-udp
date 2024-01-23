package org.Auditor;

import com.google.gson.Gson;

import java.util.*;

/**
 * Auditor class that will listen to the musicians and print the active ones
 */
public class Auditor {
    /**
     * HashMap containing all the currently active musicians with their last activity
     */
    static HashMap<String, MusicianData> musicians = new HashMap<>();

    /**
     * HashMap containing the instruments and their sound
     */
    static Map<String, String> instruments = new HashMap<>(){{
        put("ti-ta-ti", "piano");
        put("pouet", "trumpet");
        put("trulu", "flute");
        put("gzi-gzi", "violin");
        put("boum-boum", "drum");
    }};

    public static void main(String[] args){
        final int TCP_PORT = 2205;
        final int UDP_PORT = 9904;
        final String UDP_ADDRESS = "239.255.22.5";

        // Start listening to the clients on TCP
        Thread treadTcp = new Thread(new TCPServer(TCP_PORT));
        treadTcp.start();

        // Start listening to the musicians on UDP (multicast)
        Thread treadUdp = new Thread(new UDPServer(UDP_PORT, UDP_ADDRESS));
        treadUdp.start();
    }

    /**
     * TCPWorker class that will process the TCP requests
     */
    record TCPWorker(){
        public String process() {
            Gson gson = new Gson();
            List<Musician> activeMusicians = new ArrayList<>();

            // Filter musicians based on lastActivity within the last 5 seconds
            long currentTime = System.currentTimeMillis();
            for (Map.Entry<String, MusicianData> entry : musicians.entrySet()) {
                MusicianData musicianData = entry.getValue();
                if (musicianData.getLastActivity() + 5000 >= currentTime) {
                    activeMusicians.add(musicianData.getMusician());
                }
            }

            return gson.toJson(activeMusicians);
        }
    }

    private record UDPReceiverStruct(String uuid, String sound) {}
    record UDPWorker(){
        public void process(String message){
            Gson gson = new Gson();
            UDPReceiverStruct rcpt = gson.fromJson(message, UDPReceiverStruct.class);

            MusicianData musicianData = musicians.get(rcpt.uuid());
            if (musicianData != null) {
                musicianData.setLastActivity(System.currentTimeMillis());
            } else {
                Musician musician = new Musician(rcpt.uuid(), instruments.get(rcpt.sound));
                musicianData = new MusicianData(musician, System.currentTimeMillis());
                System.out.println("ADD " + musician.getUuid() + ":" + musicianData.getLastActivity());
                musicians.put(musician.getUuid(), musicianData);

                // Schedule removal after 5 seconds
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        cleanMusicians(musician.getUuid());
                    }
                }, 5000);
            }
        }
    }

    /**
     * Clean the musicians HashMap by removing the inactive ones
     * @param uuid The uuid of the musician to remove
     */
    public static void cleanMusicians(String uuid) {
        MusicianData musicianData = musicians.get(uuid);
        if (musicianData != null && musicianData.getLastActivity() + 5000 < System.currentTimeMillis()) {
            System.out.println("\tREMOVE " + uuid);
            musicians.remove(uuid);
        } else if (musicianData != null) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    cleanMusicians(musicianData.getMusician().getUuid());
                }
            }, (5000 - (System.currentTimeMillis() - musicianData.getLastActivity())));
        }
    }

    /**
     * MusicianData class that contains the musician and its last activity
     */
    private static class MusicianData {
        private final Musician musician;
        private long lastActivity;

        public MusicianData(Musician musician, long lastActivity) {
            this.musician = musician;
            this.lastActivity = lastActivity;
        }

        public Musician getMusician() {
            return musician;
        }

        public long getLastActivity() {
            return lastActivity;
        }

        public void setLastActivity(long lastActivity) {
            this.lastActivity = lastActivity;
        }
    }
}