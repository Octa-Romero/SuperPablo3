package com.prz.juego.utilidades;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class Entrada extends InputAdapter {

    private boolean arriba, derecha, izquierda;

    @Override
    public boolean keyDown(int keycode)
    {
        if(keycode == Input.Keys.W)
        {
            arriba = true;
        }

        if(keycode == Input.Keys.D)
        {
            derecha = true;
        }

        if(keycode == Input.Keys.A)
        {
            izquierda = true;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        if(keycode == Input.Keys.W)
        {
            arriba = false;
        }

        if(keycode == Input.Keys.D)
        {
            derecha = false;
        }

        if(keycode == Input.Keys.A)
        {
            izquierda = false;
        }
        return true;
    }

    public boolean mueveArriba()
    {
        return this.arriba;
    }

    public boolean mueveDerecha()
    {
        return this.derecha;
    }

    public boolean mueveIzquierda()
    {
        return this.izquierda;
    }

}
