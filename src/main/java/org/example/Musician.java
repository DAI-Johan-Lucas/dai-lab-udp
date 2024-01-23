package org.example;

import java.util.UUID;

public class Musician {
    private final String uuid;
    private final Instrument instrument;
    private long lastActivity;

    public Musician(Instrument instrument) {
        this.uuid = UUID.randomUUID().toString();
        this.instrument = instrument;
        this.lastActivity = System.currentTimeMillis();
    }

    public void setLastActivity(long lastActivity) {
        this.lastActivity = lastActivity;
    }

    public String getUuid() {
        return uuid;
    }

    public String getSound() {
        return instrument.sound();
    }
}