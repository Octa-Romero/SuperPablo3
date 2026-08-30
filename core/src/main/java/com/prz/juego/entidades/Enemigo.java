package com.prz.juego.entidades;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Enemigo {
	private float x;
	private float y;
	private final float ancho = 50;
	private final float alto = 120;

	private final float anchoAtaque = 100;
	private final float altoAtaque = 10;

	private final float velocidad = 100;
	private final float distanciaDeteccion = 500;
	private final float distanciaAtaque = 15;

	// 1 = derecha, -1 = izquierda
	private float direccion = 1;

	private float tiempoPatrulla = 0;
	private boolean caminandoPatrulla = false;

	private float tiempoEstado = 0;

	private boolean esperandoAtaque = false;
	private boolean atacando = false;
	private boolean cooldownAtaque = false;
	private boolean yaDanio = false;

	private Jugador jugador;

	public Enemigo(float x, float y, Jugador jugador) {
		this.x = x;
		this.y = y;
		this.jugador = jugador;
	}

	public void actualizar(float delta) {
		Rectangle boundsJugador = jugador.getBounds();
		float distancia = calcularDistanciaHorizontal(boundsJugador);

		if (atacando) {
			actualizarAtaque(delta);
			return;
		}

		if (esperandoAtaque) {
			tiempoEstado += delta;
			if (tiempoEstado >= 0.5f) {
				esperandoAtaque = false;
				atacando = true;
				tiempoEstado = 0;
				yaDanio = false;
			}
			return;
		}

		if (cooldownAtaque) {
			tiempoEstado += delta;
			if (tiempoEstado >= 2f) {
				cooldownAtaque = false;
				tiempoEstado = 0;
			}
			if (distancia <= distanciaDeteccion) {
				perseguir(delta, boundsJugador);
			} else {
				patrullar(delta);
			}
			return;
		}

		if (distancia <= distanciaAtaque) {
			actualizarDireccion(boundsJugador);
			esperandoAtaque = true;
			tiempoEstado = 0;
			return;
		}

		if (distancia <= distanciaDeteccion) {
			perseguir(delta, boundsJugador);
			return;
		}

		patrullar(delta);
	}

	private float calcularDistanciaHorizontal(Rectangle boundsJugador) {
		// Jugador a la derecha
		if (boundsJugador.x > x + ancho) {
			return boundsJugador.x - (x + ancho);
		}

		// Jugador a la izquierda
		if (x > boundsJugador.x + boundsJugador.width) {

			return x - (boundsJugador.x + boundsJugador.width);
		}

		// Se están superponiendo
		return 0;
	}

	private void actualizarDireccion(Rectangle boundsJugador) {
		float centroJugador = boundsJugador.x + boundsJugador.width / 2f;
		float centroEnemigo = x + ancho / 2f;

		if (centroJugador > centroEnemigo) {
			direccion = 1;
		} else if (centroJugador < centroEnemigo) {
			direccion = -1;
		}
	}

	private void perseguir(float delta, Rectangle boundsJugador) {
		float distancia = calcularDistanciaHorizontal(boundsJugador);

		if (distancia <= distanciaAtaque) {
			return;
		}

		float centroJugador = boundsJugador.x + boundsJugador.width / 2f;
		float centroEnemigo = x + ancho / 2f;
		float movimiento = velocidad * delta;

		// Jugador a la derecha
		if (centroJugador > centroEnemigo) {
			direccion = 1;
			if (distancia - movimiento < distanciaAtaque) {
				movimiento = distancia - distanciaAtaque;
			}
			x += movimiento;
		}

		// Jugador a la izquierda
		else if (centroJugador < centroEnemigo) {
			direccion = -1;
			if (distancia - movimiento < distanciaAtaque) {
				movimiento = distancia - distanciaAtaque;
			}
			x -= movimiento;
		}
	}

	private void patrullar(float delta) {
		tiempoPatrulla += delta;

		if (caminandoPatrulla) {
			x += direccion * velocidad * delta;
			if (tiempoPatrulla >= 2f) {
				caminandoPatrulla = false;
				tiempoPatrulla = 0;
			}
			return;
		}

		if (tiempoPatrulla >= 5f) {
			tiempoPatrulla = 0;
			if (Math.random() < 0.70) {
				direccion *= -1;
			}
			caminandoPatrulla = true;
		}
	}

	private void actualizarAtaque(float delta) {
		tiempoEstado += delta;

		if (!yaDanio && ataqueTocaJugador()) {
			jugador.restarVida();
			yaDanio = true;
		}

		if (tiempoEstado >= 1f) {
			atacando = false;
			cooldownAtaque = true;
			tiempoEstado = 0;
		}
	}

	private Rectangle getBoundsAtaque() {
		float ataqueX;
		float ataqueY = y + (alto / 2f) - (altoAtaque / 2f);;
		float centroEnemigo = x + ancho / 2f;

		if (direccion > 0) {
			// Hacia la derecha
			ataqueX = centroEnemigo;
		} else {
			// Hacia la izquierda
			ataqueX = centroEnemigo - anchoAtaque;
		}

		return new Rectangle(ataqueX, ataqueY, anchoAtaque, altoAtaque);
	}

	private boolean ataqueTocaJugador() {
		Rectangle ataque = getBoundsAtaque();

		Rectangle boundsJugador = jugador.getBounds();

		return ataque.overlaps(boundsJugador);
	}

	public void dibujar(ShapeRenderer shapeRenderer) {
		shapeRenderer.setColor(Color.BLUE);
		shapeRenderer.rect(x, y, ancho, alto);

		if (atacando) {
			Rectangle ataque = getBoundsAtaque();
			shapeRenderer.setColor(Color.RED);
			shapeRenderer.rect(ataque.x, ataque.y, ataque.width, ataque.height);
		}
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getAncho() {
		return ancho;
	}

	public float getAlto() {
		return alto;
	}
}