package com.prz.juego.entidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.prz.juego.sistemas.Hud;
import com.prz.juego.utilidades.Entrada;
import com.prz.juego.utilidades.Render;
import com.prz.juego.utilidades.Sonido;

public abstract class Jugador extends Entidad {

    protected Entrada entrada;
    private Texture texturaHud;
    protected Hud hud;
    protected boolean atacando;
    private float cooldownSalto = 0;

    protected Jugador(float x, float y, float ancho, float alto, int velocidadX, double vida, double danio, Texture textura, Texture texturaHud) {
        super(x, y, ancho, alto, velocidadX, vida, danio, textura);
        this.texturaHud = texturaHud;
    }

    public void setEntrada(Entrada entrada) {
        this.entrada = entrada;
    }

    @Override
    public void update(float delta) {
        guardarPosicionAnterior();

        if (cooldownSalto > 0) {
            cooldownSalto -= delta;
        }

        if (entrada.mueveArriba() && enSuelo && cooldownSalto <= 0) {
            saltar();
        }

        enSuelo = false;
        actualizarFisica(delta);

        if (y + alto < 0) {
            restarVida(1);
            setY(Gdx.graphics.getHeight() - alto);
            velocidadY = 0;
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

        actualizarAnimacion(delta, x != xAnterior, atacando);
        actualizarOrientacionSegunMovimiento();
        actualizarSpriteVisual();
        bounds.setPosition(x, y);

        actualizarInvencibilidad(delta);
    }

    public abstract void atacar();

    public void saltar() {
        velocidadY = 300;
        enSuelo = false;
        cooldownSalto = 0.5f;
        Sonido.SALTO.sonar();
    }

    public void dibujar() {
        if (spriteVisible && sprite != null) {
            sprite.draw(Render.batch);
        }
    }

    public Hud getHud() {
        return hud;
    }

    public Texture getTexturaHud() {
        return texturaHud;
    }

    public boolean estaAtacando() {
        return atacando;
    }
}
