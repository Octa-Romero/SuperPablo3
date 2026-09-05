package com.prz.juego.utilidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public enum Sonido {
	SALTO("Sonidos/salto.WAV"),
	ESPADA("Sonidos/espada.WAV"),
    BOLA_FUEGO("Sonidos/bola_fuego.WAV"),
	CLICK("Sonidos/click.WAV");

	private final String nombreArchivo;
	private Sound sound;
	private static float volumenGlobal = 0.5f;

	private Sonido(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	public void sonar() {
		cargar();
		sound.play(volumenGlobal);
	}

	private void cargar() {
		if (sound == null) {
			sound = Gdx.audio.newSound(Gdx.files.internal(nombreArchivo));
		}
	}

    public static void setVolumen(float volumen) {
        volumenGlobal = Math.max(0f, Math.min(1f, volumen));
    }

	public static float getVolumen() {
		return volumenGlobal;
	}

	public static void dispose() {
		for(Sonido sonido : values()) {
			if(sonido.sound != null) {
				sonido.sound.dispose();
				sonido.sound = null;
			}
		}
	}
}
