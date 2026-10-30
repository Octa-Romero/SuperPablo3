package com.prz.juego.sistemas;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.prz.juego.entidades.jugadores.Jugador;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.recursos.Imagen;

import java.util.ArrayList;

public class Hud {

    private final Jugador JUGADOR;
    private final Stage STAGE;
    private final FitViewport VIEWPORT = new FitViewport(1280, 720, new OrthographicCamera());

    private final Texture CORAZON_LLENO = GestorRecursos.obtenerTextura("Hud/corazon_lleno.png");
    private final Texture CORAZON_MITAD = GestorRecursos.obtenerTextura("Hud/corazon_mitad.png");
    private final Texture CORAZON_VACIO = GestorRecursos.obtenerTextura("Hud/corazon_vacio.png");

    private final TextureRegionDrawable LLENO_DRAWABLE;
    private final TextureRegionDrawable MITAD_DRAWABLE;
    private final TextureRegionDrawable VACIO_DRAWABLE;

    private final ArrayList<Imagen> CORAZONES = new ArrayList<>();

    private Table tablaPrincipal;
    private Table contenedorCorazones;

    private final float TAMANO_CARA = 120;
    private final float TAMANO_CORAZON = 50;
    private final float ESPACIO_CORAZONES = 5;

    private final Imagen imagenReliquia;
    private boolean tieneReliquia = false;;

    private double ultimaVida = -1;

    public Hud(Jugador JUGADOR, SpriteBatch batch) {
        this.JUGADOR = JUGADOR;

        STAGE = new Stage(VIEWPORT, batch);

        // cache de drawables (se crea una sola vez para ser reutilizado)
        LLENO_DRAWABLE = new TextureRegionDrawable(new TextureRegion(CORAZON_LLENO));
        MITAD_DRAWABLE = new TextureRegionDrawable(new TextureRegion(CORAZON_MITAD));
        VACIO_DRAWABLE = new TextureRegionDrawable(new TextureRegion(CORAZON_VACIO));

        tablaPrincipal = new Table();
        tablaPrincipal.top().left();
        tablaPrincipal.setFillParent(true);
        tablaPrincipal.pad(20);

        Imagen imagenCara = new Imagen(JUGADOR.getTEXTURA_HUD(), true);

        imagenReliquia = new Imagen(
            GestorRecursos.obtenerTextura("Reliquia/reliquia.png"),
            true
        );
        imagenReliquia.setVisible(false);

        contenedorCorazones = new Table();

        tablaPrincipal.add(imagenCara).size(TAMANO_CARA, TAMANO_CARA);
        tablaPrincipal.add(contenedorCorazones).padLeft(10).center();

        tablaPrincipal.add(imagenReliquia)
            .size(48, 48)
            .padLeft(15);

        STAGE.addActor(tablaPrincipal);

        crearCorazones();
        actualizarCorazones();
    }

    private void crearCorazones() {
        int max = (int) Math.ceil(JUGADOR.getVidaMaxima());

        for (int i = 0; i < max; i++) {
            Imagen corazon = new Imagen(VACIO_DRAWABLE, true);

            CORAZONES.add(corazon);

            contenedorCorazones
                    .add(corazon)
                    .size(TAMANO_CORAZON, TAMANO_CORAZON)
                    .padRight(ESPACIO_CORAZONES);
        }
    }

    private void actualizarCorazones() {

        double vida = JUGADOR.getVida();

        for (int i = 0; i < CORAZONES.size(); i++) {

            double vidaRestante = vida - i;

            if (vidaRestante >= 1.0) {
                CORAZONES.get(i).setDrawable(LLENO_DRAWABLE);
            } else if (vidaRestante >= 0.5) {
                CORAZONES.get(i).setDrawable(MITAD_DRAWABLE);
            } else {
                CORAZONES.get(i).setDrawable(VACIO_DRAWABLE);
            }
        }
    }

    public void dibujar() {

        double vidaActual = JUGADOR.getVida();

        if (vidaActual != ultimaVida) {
            actualizarCorazones();
            ultimaVida = vidaActual;
        }

        STAGE.act();
        STAGE.draw();
    }

    public void actualizarTamano(int width, int height) {
        VIEWPORT.update(width, height, true);
    }

    public void dispose() {
        STAGE.dispose();
    }

    public void setTieneReliquia(boolean valor) {
        if (tieneReliquia == valor) {
            return;
        }

        tieneReliquia = valor;
        imagenReliquia.setVisible(valor);
    }
}
