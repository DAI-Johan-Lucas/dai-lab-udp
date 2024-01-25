package orchestra;

import com.google.gson.annotations.SerializedName;

/**
 * Instrument enum that contains the instruments and their sounds
 */
public enum Instrument {
    @SerializedName("piano")
    PIANO("ti-ta-ti"),
    @SerializedName("trumpet")
    TRUMPET("pouet"),
    @SerializedName("flute")
    FLUTE("trulu"),
    @SerializedName("violin")
    VIOLIN("gzi-gzi"),
    @SerializedName("drum")
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

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}