package com.prz.juego.utilidades;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class Entrada extends InputAdapter {

	private boolean arriba, derecha, izquierda, ataca, hitboxes;

	@Override
	public boolean keyDown(int keycode) {

		if (keycode == Input.Keys.W || keycode == Input.Keys.UP  || keycode == Input.Keys.Z) {
			arriba = true;
		}

		if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) {
			derecha = true;
		}

		if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT) {
			izquierda = true;
		}

		if (keycode == Input.Keys.X) {
			ataca = true;
		}

		if (keycode == Input.Keys.F1) {
			hitboxes = true;
		}

		return true;
	}

	@Override
	public boolean keyUp(int keycode) {

		if (keycode == Input.Keys.W || keycode == Input.Keys.UP || keycode == Input.Keys.Z) {
			arriba = false;
		}

		if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) {
			derecha = false;
		}

		if (keycode == Input.Keys.A || keycode == Input.Keys.LEFT) {
			izquierda = false;
		}

		return true;
	}

	public boolean mueveArriba() {
		return arriba;
	}

	public boolean mueveDerecha() {
		return derecha;
	}

	public boolean mueveIzquierda() {
		return izquierda;
	}

	public boolean ataca() {
		if (ataca) {
			ataca = false;
			return true;
		}
		return false;
	}

	public boolean mostrarHitboxes() {
		if (hitboxes) {
			hitboxes = false;
			return true;
		}
		return false;
	}
}