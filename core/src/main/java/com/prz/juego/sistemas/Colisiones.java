package com.prz.juego.sistemas;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;

public class Colisiones {

    private TiledMapTileLayer layer;
    private int anchoTile;
    private int altoTile;

    public Colisiones(TiledMapTileLayer layer)
    {
        this.layer = layer;
        this.anchoTile = layer.getTileWidth();
        this.altoTile = layer.getTileHeight();
    }

    public boolean colisiona(Rectangle boundsJugador)
    {
        float xJugador = boundsJugador.x, yJugador = boundsJugador.y, ancho = boundsJugador.width, alto = boundsJugador.height;
        int startX = (int) (boundsJugador.getX() / anchoTile);
        int startY = (int) (boundsJugador.getY() / altoTile);

        int endX = (int) (xJugador + ancho) / (anchoTile);
        int endY = (int) (yJugador + alto) / (altoTile);

        for(int x = startX ; x <= endX ; x++)
        {
            for(int y =  startY ; y <= endY ; y++)
            {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);

                if(cell != null && cell.getTile() != null)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public int getAltoTile() {
        return altoTile;
    }

    public int getAnchoTile() {
        return anchoTile;
    }

    public float getTileTop(Rectangle playerBounds) {

        int tileX = (int)(playerBounds.x / anchoTile);
        int tileY = (int)(playerBounds.y / altoTile);

        TiledMapTileLayer.Cell cell = layer.getCell(tileX, tileY);

        if (cell == null || cell.getTile() == null) {
            return 0;
        }

        return tileY * altoTile + altoTile;
    }
}
