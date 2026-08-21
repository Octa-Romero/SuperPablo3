package com.prz.juego.utilidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public enum Musica {

    MENU("musica_menu.mp3"),
    BATALLA("musica_batalla.mp3"),
    VICTORIA("musica_victoria.mp3");

    private final String nombreArchivo;
    private Music music;

    private static Musica actual;
    private static float volumenGlobal = 0.5f;

    private Musica(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public static void sonar(Musica nueva) {
        // Volver si ya está sonando esta música o si es null
        if (actual == nueva && nueva.music != null && nueva.music.isPlaying()) {
            return;
        }
        parar();
        // Cargar si todavía no fue cargada
        if (nueva.music == null) {
            nueva.music = Gdx.audio.newMusic(Gdx.files.internal(nueva.nombreArchivo));
        }
        nueva.music.setLooping(true);
        nueva.music.setVolume(volumenGlobal);
        nueva.music.play();
        actual = nueva;
    }

    public static void parar() {
        if (actual != null && actual.music != null) {
            actual.music.stop();
            actual = null;
        }
    }

    public static void setVolumen(float volumen) {
        volumenGlobal = Math.max(0f, Math.min(1f, volumen));
        if (actual != null && actual.music != null) {
            actual.music.setVolume(volumenGlobal);
        }
    }

    public static float getVolumen() {
        return volumenGlobal;
    }

    public static void dispose() {
        for (Musica musica : values()) {
            if (musica.music != null) {
                musica.music.dispose();
                musica.music = null;
            }
        }
        actual = null;
    }
}