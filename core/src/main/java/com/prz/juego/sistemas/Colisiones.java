package com.prz.juego.sistemas;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.prz.juego.entidades.Jugador;

import java.util.ArrayList;

public class Colisiones {

    private ArrayList<Rectangle> colisiones;

    public Colisiones(TiledMap tiledMap) {
        colisiones = new ArrayList<>();
        cargarColisiones(tiledMap);
    }

    private void cargarColisiones(TiledMap tiledMap){
        for(MapObject object : tiledMap.getLayers().get("colisiones").getObjects()){
            if(object instanceof RectangleMapObject){
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                colisiones.add(rect);
            }
        }
    }

    public void chequearColision(Jugador jugador) {
        for (Rectangle suelo : colisiones) {
            if (jugador.getBounds().overlaps(suelo)) {
                jugador.setY(suelo.y + suelo.height);
                jugador.setEnSuelo(true);
                break;
            }
        }
    }

    public ArrayList<Rectangle> getColisiones() {
        return colisiones;
    }
}
