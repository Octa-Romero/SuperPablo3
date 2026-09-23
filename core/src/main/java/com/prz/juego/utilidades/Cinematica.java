package com.prz.juego.utilidades;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class Cinematica {

    private ArrayList<EscenaCinematica> escenas;
    private int indiceActual = 0;
    private boolean terminada = false;
    private float tiempoTranscurrido;

    public Cinematica(ArrayList<EscenaCinematica> escenas) {
        this.escenas = escenas;
    }

    public void pasarEscena(){
        if(indiceActual + 1 < escenas.size()){
            indiceActual++;
        }
        else {
            terminada = true;
        }
    }

    public void actualizar(float delta){
        tiempoTranscurrido += delta;
        if(terminada){
            return;
        }
        if(tiempoTranscurrido > escenas.get(indiceActual).getDuracion()){
            pasarEscena();
            tiempoTranscurrido = 0;
        }
    }

    public void dibujar(SpriteBatch batch){
        escenas.get(indiceActual).dibujar(batch);
    }

    public boolean termino() {
        return terminada;
    }
}
