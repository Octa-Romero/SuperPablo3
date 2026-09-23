package com.prz.juego.utilidades;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GestorCinematica {

    private Cinematica cinematicaActual;


    public void iniciar(Cinematica cinematica){
        cinematicaActual = cinematica;
    }

    public void actualizar(float delta){
        if(cinematicaActual != null){
            cinematicaActual.actualizar(delta);
        }
    }

    public void dibujar(SpriteBatch batch) {
        if(cinematicaActual != null) {
            cinematicaActual.dibujar(batch);
        }
    }

    public boolean termino(){
        if(cinematicaActual != null) {
            return cinematicaActual.termino();
        }
        return true;
    }
}
