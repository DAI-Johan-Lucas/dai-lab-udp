package org.example;

public enum Instrument {
    PIANO("ti-ta-ti"),
    TRUMPET("pouet"),
    FLUTE("trulu"),
    VIOLIN("gzi-gzi"),
    DRUM("boum-boum");

    private final String sound;

    // Constructeur privé pour assigner la valeur à l'attribut sound
    private Instrument(String sound) {
        this.sound = sound;
    }

    // Méthode pour obtenir le son de l'instrument
    public String sound() {
        return sound;
    }
}
