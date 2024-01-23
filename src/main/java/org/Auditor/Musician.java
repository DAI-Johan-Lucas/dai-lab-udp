package org.Auditor;

import java.util.Objects;
import java.util.UUID;

public record Musician(String uuid, String instrument) {

    public Musician {
        // Utilisation d'un bloc d'initialisation pour valider ou effectuer d'autres actions si nécessaire
        Objects.requireNonNull(uuid, "UUID cannot be null");
        Objects.requireNonNull(instrument, "Instrument cannot be null");
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Musician musician = (Musician) obj;
        return Objects.equals(uuid, musician.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}