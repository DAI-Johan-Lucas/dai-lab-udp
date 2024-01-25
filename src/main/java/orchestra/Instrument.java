package orchestra;

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

    public static Instrument fromSound(String sound) {
        for (Instrument instrument : Instrument.values()) {
            if (instrument.getSound().equals(sound)) {
                return instrument;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}