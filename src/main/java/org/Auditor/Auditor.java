package org.Auditor;

import com.google.gson.Gson;

import java.util.*;

public class Auditor {
    static HashMap<String, Musician> musicians = new HashMap<>();

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

        Thread treadTcp = new Thread(new TCPServer(TCP_PORT, 10));
        treadTcp.start();

        Thread treadUdp = new Thread(new UDPServer(UDP_PORT, 10));
        treadUdp.start();
    }

    public static class TCPWorker{
        public String process() {
            Gson gson = new Gson();
            return gson.toJson(new ArrayList<>(musicians.values()));
        }
    }

    public static class UDPWorker{

        private record UDPReceiverStruc(String uuid, String sound) {

        }
        public void process(String message){
            Gson gson = new Gson();
            UDPReceiverStruc rcpt = gson.fromJson(message, UDPReceiverStruc.class);

            Musician musician = new Musician(rcpt.uuid(),
                    instruments.get(rcpt.sound));
            if(musicians.containsKey(rcpt.uuid())){
                musicians.get(rcpt.uuid()).setLastActivity(System.currentTimeMillis());
            }else {
                System.out.print("ADD " + musician.getUuid() + ":" + musician.getLastActivity());
                musicians.put(musician.getUuid(), musician);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        cleanMusicians(musician.getUuid());
                    }
                }, 5000);
            };

        }
    }

    public static void cleanMusicians(String uuid){
//        System.out.println("Clean function on : " + uuid);
        Musician musician = musicians.get(uuid);
        if(musician.getLastActivity() + 5000 < System.currentTimeMillis()){
            System.out.println("\tREMOVE " + uuid);
            musicians.remove(uuid);
        }else {
            Timer timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    cleanMusicians(musician.getUuid());
                }
            }, (5000 - (System.currentTimeMillis() - musician.getLastActivity())));
        }
    }
}

