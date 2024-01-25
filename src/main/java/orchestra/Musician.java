package orchestra;

import java.util.Objects;
import java.util.UUID;

/**
 * Musician class that will contain the musician's information
 * @param uuid uuid of the musician
 * @param instrument instrument of the musician
 */
public record Musician(String uuid, Instrument instrument) {

    public Musician(Instrument instrument) {
        this(UUID.randomUUID().toString(), instrument);
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
