package com.prz.juego.entidades;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.prz.juego.utilidades.Sonido;

public abstract class Entidad {

	protected Sprite sprite;
	protected float x, y;
	protected float ancho, alto;
	protected float xAnterior, yAnterior;
	protected int velocidadX;
	protected float velocidadY = 0;
	protected final float gravedad = 400;
	protected boolean enSuelo = true;
	protected boolean chocoPared = false;
	protected Rectangle bounds;
	protected double vida;
	protected double vidaMaxima;
	protected double danio;
	protected float tiempoInvencibilidad = 0;
	protected float duracionInvencibilidad = 2f;
	protected float tiempoParpadeo = 0;
	protected boolean spriteVisible = true;

	protected Entidad(float x, float y, float ancho, float alto, int velocidadX, double vida, double danio, Texture textura) {
		this.x = x;
		this.y = y;
		this.ancho = ancho;
		this.alto = alto;
		this.velocidadX = velocidadX;
		this.vida = vida;
		this.vidaMaxima = vida;
		this.danio = danio;
		xAnterior = x;
		yAnterior = y;
		bounds = new Rectangle(x, y, ancho, alto);
		sprite = new Sprite(textura);
		sprite.setPosition(x, y);
		sprite.setSize(ancho, alto);
	}

	public abstract void update(float delta);

	protected void actualizarFisica(float delta) {
		if (!enSuelo) {
			velocidadY -= gravedad * delta;
		}
		y += velocidadY * delta;
		bounds.setPosition(x, y);
	}

	protected void actualizarInvencibilidad(float delta) {
		if (tiempoInvencibilidad > 0) {
			tiempoInvencibilidad -= delta;
			tiempoParpadeo += delta;
			if (tiempoParpadeo >= 0.1f) {
				spriteVisible = !spriteVisible;
				tiempoParpadeo = 0;
			}
		} else {
			tiempoInvencibilidad = 0;
			tiempoParpadeo = 0;
			spriteVisible = true;
		}
	}

	public void restarVida(double cantidad) {
		if (tiempoInvencibilidad > 0) {
			return;
		}
		vida -= cantidad;
		tiempoInvencibilidad = duracionInvencibilidad;
		tiempoParpadeo = 0;
		spriteVisible = true;
		if (vida <= 0) {
			vida = 0;
			morir();
		}
	}

	protected void guardarPosicionAnterior() {
		xAnterior = x;
		yAnterior = y;
	}

	protected void orientarSprite(Sprite sprite, float direccion) {
		if (sprite == null || direccion == 0) {
			return;
		}

		boolean debeInvertirse = direccion < 0;
		if (sprite.isFlipX() != debeInvertirse) {
			sprite.flip(true, false);
		}
	}

    public void saltar() {
        velocidadY = 300;
        enSuelo = false;
        Sonido.SALTO.sonar();
    }

	protected void actualizarOrientacionSegunMovimiento() {
		orientarSprite(sprite, x - xAnterior);
	}

	protected abstract void morir();

	public abstract void dibujar();

	public void dibujarHitbox(ShapeRenderer shapeRenderer) {
		shapeRenderer.setColor(Color.GREEN);
		shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getXAnterior() {
		return xAnterior;
	}

	public float getYAnterior() {
		return yAnterior;
	}

	public float getAncho() {
		return ancho;
	}

	public float getAlto() {
		return alto;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public int getVelocidadX() {
		return velocidadX;
	}

	public float getVelocidadY() {
		return velocidadY;
	}

	public double getVida() {
		return vida;
	}

	public double getVidaMaxima() {
		return vidaMaxima;
	}

	public double getDanio() {
		return danio;
	}

	public boolean estaEnSuelo() {
		return enSuelo;
	}

	public boolean chocoPared() {
		return chocoPared;
	}

	public void setChocoPared(boolean valor) {
		chocoPared = valor;
	}

	public void setVelocidadX(int valor) {
		velocidadX = valor;
	}

	public void setVelocidadY(float valor) {
		velocidadY = valor;
	}

	public void setEnSuelo(boolean valor) {
		enSuelo = valor;
	}

	public void setX(float x) {
		this.x = x;
		bounds.setX(x);
		if (sprite != null) {
			sprite.setX(x);
		}
	}

	public void setY(float y) {
		this.y = y;
		bounds.setY(y);
		if (sprite != null) {
			sprite.setY(y);
		}
	}

	public void dispose() {
		if (sprite != null) {
			sprite.getTexture().dispose();
			sprite = null;
		}
	}
}
