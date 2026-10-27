package com.prz.juego.utilidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Animacion {

    private TextureRegion regionReposo;
    private Animation<TextureRegion> reposo;
    private Animation<TextureRegion> caminar;
    private Animation<TextureRegion> ataque;
    private Animation<TextureRegion> actual;
    private float tiempo;

    public Animacion(Texture texturaReposo) {
        if (texturaReposo == null) {
            throw new IllegalArgumentException("La textura de reposo no puede ser null");
        }
        regionReposo = new TextureRegion(texturaReposo);
    }

    public void configurar(Texture spritesheet, int anchoFrame, int altoFrame,
                           int framesReposo, int framesCaminar, float duracionFrame) {
        configurar(
            spritesheet,
            anchoFrame,
            altoFrame,
            crearIndices(0, framesReposo),
            crearIndices(framesReposo, framesCaminar),
            null,
            duracionFrame
        );
    }

    public void configurar(Texture spritesheet, int anchoFrame, int altoFrame,
                           int[] indicesReposo, int[] indicesCaminar,
                           int[] indicesAtaque, float duracionFrame) {
        validarConfiguracion(spritesheet, anchoFrame, altoFrame, indicesCaminar, duracionFrame);

        TextureRegion[][] grilla = TextureRegion.split(spritesheet, anchoFrame, altoFrame);
        TextureRegion[] frames = aplanar(grilla);
        validarIndices(frames.length, indicesReposo, indicesCaminar, indicesAtaque);

        reposo = indicesReposo == null || indicesReposo.length == 0
            ? null
            : crearAnimacion(frames, indicesReposo, duracionFrame, Animation.PlayMode.LOOP);
        caminar = crearAnimacion(frames, indicesCaminar, duracionFrame, Animation.PlayMode.LOOP);
        ataque = indicesAtaque == null || indicesAtaque.length == 0
            ? null
            : crearAnimacion(frames, indicesAtaque, duracionFrame, Animation.PlayMode.NORMAL);

        actual = null;
        tiempo = 0f;
    }

    public void actualizar(Sprite sprite, float delta, boolean caminando, boolean atacando) {
        Animation<TextureRegion> siguiente;

        if (atacando && ataque != null) {
            siguiente = ataque;
        } else if (caminando) {
            siguiente = caminar;
        } else {
            siguiente = reposo;
        }

        if (siguiente == null) {
            if (!caminando && regionReposo != null) {
                sprite.setRegion(regionReposo);
            }
            return;
        }

        if (actual != siguiente) {
            actual = siguiente;
            tiempo = 0f;
        } else {
            tiempo += delta;
        }

        sprite.setRegion(actual.getKeyFrame(tiempo));
    }

    public void dispose() {
        regionReposo = null;
        reposo = null;
        caminar = null;
        ataque = null;
        actual = null;
    }

    private void validarConfiguracion(Texture spritesheet, int anchoFrame, int altoFrame,
                                      int[] indicesCaminar, float duracionFrame) {
        if (spritesheet == null || anchoFrame <= 0 || altoFrame <= 0 || duracionFrame <= 0
            || indicesCaminar == null || indicesCaminar.length == 0
            || spritesheet.getWidth() % anchoFrame != 0
            || spritesheet.getHeight() % altoFrame != 0) {
            throw new IllegalArgumentException("Spritesheet o configuracion de frames invalida");
        }
    }

    private Animation<TextureRegion> crearAnimacion(TextureRegion[] frames, int[] indices,
                                                     float duracionFrame, Animation.PlayMode modo) {
        TextureRegion[] regiones = new TextureRegion[indices.length];
        for (int i = 0; i < indices.length; i++) {
            regiones[i] = frames[indices[i]];
        }

        Animation<TextureRegion> animacion = new Animation<>(duracionFrame, regiones);
        animacion.setPlayMode(modo);
        return animacion;
    }

    private int[] crearIndices(int desde, int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad de frames no puede ser negativa");
        }

        int[] indices = new int[cantidad];
        for (int i = 0; i < cantidad; i++) {
            indices[i] = desde + i;
        }
        return indices;
    }

    private void validarIndices(int totalFrames, int[]... grupos) {
        for (int[] grupo : grupos) {
            if (grupo == null) {
                continue;
            }
            for (int indice : grupo) {
                if (indice < 0 || indice >= totalFrames) {
                    throw new IllegalArgumentException("El spritesheet no contiene el frame " + indice);
                }
            }
        }
    }

    private TextureRegion[] aplanar(TextureRegion[][] grilla) {
        int cantidad = 0;
        for (TextureRegion[] fila : grilla) {
            cantidad += fila.length;
        }

        TextureRegion[] frames = new TextureRegion[cantidad];
        int indice = 0;
        for (TextureRegion[] fila : grilla) {
            for (TextureRegion frame : fila) {
                frames[indice++] = frame;
            }
        }
        return frames;
    }
}
