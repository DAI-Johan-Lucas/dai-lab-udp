package org.Auditor;

import com.google.gson.Gson;
import org.example.Instrument;

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
        final String UDP_ADDRESS = "239.255.22.5";

        Thread treadTcp = new Thread(new TCPServer(TCP_PORT));
        treadTcp.start();

        Thread treadUdp = new Thread(new UDPServer(UDP_PORT, UDP_ADDRESS));
        treadUdp.start();
    }

    record TCPWorker(){
        public String process() {
            Gson gson = new Gson();
            return gson.toJson(new ArrayList<>(musicians.values()));
        }
    }

    private record UDPReceiverStruct(String uuid, String sound) {}
    record UDPWorker(){
        public void process(String message){
            Gson gson = new Gson();
            UDPReceiverStruct rcpt = gson.fromJson(message, UDPReceiverStruct.class);

            Musician musician = new Musician(rcpt.uuid(),
                    instruments.get(rcpt.sound));
            if(musicians.containsKey(rcpt.uuid())){
                musicians.get(rcpt.uuid()).setLastActivity(System.currentTimeMillis());
            }else {
                System.out.println("ADD " + musician.getUuid() + ":" + musician.getLastActivity());
                musicians.put(musician.getUuid(), musician);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        removeMusician(musician.getUuid());
                    }
                }, 5000);
            };
        }
    }

    public static void removeMusician(String uuid){
//        System.out.println("Clean function on : " + uuid);
        Musician musician = musicians.get(uuid);
        if(musician.getLastActivity() + 5000 < System.currentTimeMillis()){
            System.out.println("REMOVE " + uuid + ":" + musician.getLastActivity());
            musicians.remove(uuid);
        }else {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    removeMusician(musician.getUuid());
                }
            }, (5000 - (System.currentTimeMillis() - musician.getLastActivity())));
        }
    }
}

