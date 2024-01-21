package org.Auditor;

import java.util.Objects;
import java.util.UUID;

public class Musician {
    private final String uuid;
    private final String instrument;
    private long lastActivity;

    public Musician(String instrument) {
        this.uuid = UUID.randomUUID().toString();
        this.instrument = instrument;
        this.lastActivity = System.currentTimeMillis();
    }

    public Musician(String uuid, String instrument) {
        this.uuid = uuid;
        this.instrument = instrument;
        this.lastActivity = System.currentTimeMillis();
    }

    public long getLastActivity() {
        return lastActivity;
    }
    public void setLastActivity(long lastActivity) {
        this.lastActivity = lastActivity;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Musician){
            return this.uuid.equals(((Musician) obj).uuid);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}