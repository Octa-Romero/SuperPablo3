package com.prz.juego.utilidades;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.prz.juego.recursos.Imagen;

public class EscenaCinematica {

    private float duracion;
    private Imagen imagen;

    public EscenaCinematica(Imagen imagen, float duracion) {
        this.imagen = imagen;
        this.duracion = duracion;
    }


    public void dibujar(SpriteBatch batch){
        imagen.dibujar(batch);
    }

    public float getDuracion() {
        return duracion;
    }

    public Imagen getImagen() {
        return imagen;
    }
}
