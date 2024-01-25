package orchestra;

/**
 * Instrument enum that contains the instruments and their sounds
 */
public enum Instrument {
    PIANO("ti-ta-ti"),
    TRUMPET("pouet"),
    FLUTE("trulu"),
    VIOLIN("gzi-gzi"),
    DRUM("boum-boum");

    private final String sound;

    Instrument(String sound) {
        this.sound = sound;
    }

    public String getSound() {
        return sound;
    }

    /**
     * Get the instrument from the sound of it
     * @param sound sound of the instrument
     * @return the instrument
     */
    public static Instrument fromSound(String sound) {
        for (Instrument instrument : Instrument.values()) {
            if (instrument.getSound().equals(sound)) {
                return instrument;
            }
        }
        return null;
    }
}