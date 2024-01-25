package orchestra;

import com.google.gson.Gson;

import java.util.*;

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
            List<MusicianData> activeMusicians = new ArrayList<>();

            // Filter musicians based on lastActivity within the last 5 seconds
            long currentTime = System.currentTimeMillis();
            for (Map.Entry<String, MusicianData> entry : musicians.entrySet()) {
                MusicianData musicianData = entry.getValue();
                // If the musician is active, add it to the list of active musicians
                if (musicianData.getLastActivity() + 5000 >= currentTime) {
                    activeMusicians.add(musicianData);
                }
            }

            return gson.toJson(activeMusicians);
        }
    }

    record UDPWorker() {
        public void process(String message) {
            Gson gson = new Gson();
            MulticastSender.MulticastStruct rcpt = gson.fromJson(message, MulticastSender.MulticastStruct.class);

            MusicianData musicianData = musicians.get(rcpt.uuid());
            if (musicianData != null) {
                musicianData.setLastActivity(System.currentTimeMillis());
            } else {
                Musician musician = new Musician(rcpt.uuid(), Instrument.fromSound(rcpt.sound())); // TODO CHANGE

                musicianData = new MusicianData(rcpt.uuid(), Instrument.fromSound(rcpt.sound()), System.currentTimeMillis());
                musicians.put(musicianData.getUuid(), musicianData);

                System.out.println("ADD " + musicianData.getUuid());

                // Schedule removal after 5 seconds
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        cleanMusicians(musician.uuid());
                    }
                }, 5000);
            }
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
            System.out.println("\tREMOVE " + uuid);
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