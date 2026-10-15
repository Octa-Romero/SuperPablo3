package com.prz.juego.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.prz.juego.sistemas.Colisiones;
import com.prz.juego.sistemas.Hud;
import com.prz.juego.utilidades.Entrada;
import com.prz.juego.utilidades.Render;

public abstract class Jugador extends Entidad {

	protected Entrada entrada;
	private final Texture TEXTURA_HUD;
	protected Hud hud;
	protected boolean atacando;
    protected float respawnY = 1000;
    private Colisiones colisiones;
    private boolean trepando = false;
    private final float velocidadEscalera = 150f;

	protected Jugador(float x, float y, float ancho, float alto, int velocidadX, double vida, double danio, Texture textura, Texture TEXTURA_HUD) {
		super(x, y, ancho, alto, velocidadX, vida, danio, textura);
		this.TEXTURA_HUD = TEXTURA_HUD;
	}

	public void setEntrada(Entrada entrada) {
		this.entrada = entrada;
	}


    @Override
    public void update(float delta) {
        guardarPosicionAnterior();

        boolean dentroEscalera = colisiones != null
            && colisiones.estaEnEscalera(this);

        if (!dentroEscalera) {
            trepando = false;
        } else if (entrada.mueveArriba() || entrada.mueveAbajo()) {
            trepando = true;
        }

        if (trepando && dentroEscalera) {
            enSuelo = false;

            if (entrada.mueveArriba()) {
                velocidadY = velocidadEscalera;
                y += velocidadEscalera * delta;
            } else if (entrada.mueveAbajo()) {
                velocidadY = -velocidadEscalera;
                y -= velocidadEscalera * delta;
            } else {
                velocidadY = 0;
            }

            bounds.setPosition(x, y);

        } else {
            if (entrada.mueveArriba() && enSuelo) {
                saltar();
            }

            enSuelo = false;
            actualizarFisica(delta);
        }

        if (y + alto < 0) {
            restarVida(1);
            setY(respawnY);
            velocidadY = 0;
            trepando = false;
        }

        if (entrada.mueveIzquierda()) {
            x -= velocidadX * delta;
        }

        if (entrada.mueveDerecha()) {
            x += velocidadX * delta;
        }

        if (entrada.ataca()) {
            atacar();
        }

        actualizarAnimacion(delta,x != xAnterior || (trepando && y != yAnterior),
            atacando
        );

        actualizarOrientacionSegunMovimiento();
        actualizarSpriteVisual();

        bounds.setPosition(x, y);

        actualizarInvencibilidad(delta);
    }


    public abstract void atacar();


	public void dibujar() {
		if (spriteVisible && sprite != null) {
			sprite.draw(Render.batch);
		}
	}

	public Hud getHud() {
		return hud;
	}

	public Texture getTEXTURA_HUD() {
		return TEXTURA_HUD;
	}

	public boolean estaAtacando() {
		return atacando;
	}

    public void setColisiones(Colisiones colisiones) {
        this.colisiones = colisiones;
    }

    public boolean estaTrepando() {
        return trepando;
    }
}
