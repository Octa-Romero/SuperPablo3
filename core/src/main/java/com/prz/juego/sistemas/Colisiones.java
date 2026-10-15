package com.prz.juego.sistemas;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;

import com.prz.juego.entidades.Entidad;
import com.prz.juego.entidades.Enemigo;
import com.prz.juego.entidades.Jugador;

import java.util.ArrayList;

public class Colisiones {

	private final ArrayList<Rectangle> colisiones;
    private final ArrayList<Rectangle> escaleras = new ArrayList<>();
    private final ArrayList<Polygon> pendientes = new ArrayList<>();;

	public Colisiones(TiledMap tiledMap) {
		colisiones = new ArrayList<>();
		cargarColisiones(tiledMap);
	}

	private void cargarColisiones(TiledMap tiledMap) {
		for (MapObject object : tiledMap.getLayers().get("colisiones").getObjects()) {
			if (object instanceof RectangleMapObject) {
				Rectangle rect = ((RectangleMapObject) object).getRectangle();
				colisiones.add(rect);
			}
		}
        MapLayer capaPendientes = tiledMap.getLayers().get("pendiente");

        if (capaPendientes != null) {
            for (MapObject object : capaPendientes.getObjects()) {
                if (object instanceof PolygonMapObject) {
                    Polygon poligono = ((PolygonMapObject) object).getPolygon();
                    pendientes.add(poligono);
                }
            }
        }

        MapLayer capaEscaleras = tiledMap.getLayers().get("escaleras");

        if (capaEscaleras != null) {
            for (MapObject object : capaEscaleras.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    escaleras.add(rect);
                }
            }
        }

	}

    public boolean estaEnEscalera(Jugador jugador) {
        for (Rectangle escalera : escaleras) {
            if (jugador.getBounds().overlaps(escalera)) {
                return true;
            }
        }

        return false;
    }

	public void chequearColision(Entidad entidad) {
		boolean estabaEnSuelo = entidad.estaEnSuelo();
		entidad.setEnSuelo(false);
		boolean chocoPared = false;

		for (Rectangle suelo : colisiones) {
			if (!entidad.getBounds().overlaps(suelo)) {
				continue;
			}

			// Cayendo sobre el suelo
			if (entidad.getYAnterior() >= suelo.y + suelo.height && entidad.getVelocidadY() <= 0) {
				entidad.setY(suelo.y + suelo.height);
				entidad.setVelocidadY(0);
				entidad.setEnSuelo(true);
				continue;
			}

            // Saltando y golpeando el techo
            if (entidad.getYAnterior() + entidad.getAlto() <= suelo.y
                && entidad.getVelocidadY() > 0) {

                if (entidad instanceof Jugador
                    && ((Jugador) entidad).estaTrepando()) {
                    continue;
                }

                entidad.setY(suelo.y - entidad.getAlto());
                entidad.setVelocidadY(0);

            }
		}

		entidad.getBounds().setPosition(entidad.getX(), entidad.getY());

		for (Rectangle pared : colisiones) {
			if (!entidad.getBounds().overlaps(pared)) {
				continue;
			}

			// Viene desde izquierda
			if (entidad.getXAnterior() + entidad.getAncho() <= pared.x && entidad.getX() + entidad.getAncho() > pared.x) {
				entidad.setX(pared.x - entidad.getAncho());
				chocoPared = true;
				continue;
			}

			// Viene desde derecha
			if (entidad.getXAnterior() >= pared.x + pared.width && entidad.getX() < entidad.getXAnterior()) {
				entidad.setX(pared.x + pared.width);
				chocoPared = true;
			}
		}
        chequearPendiente(entidad);

		if (!entidad.estaEnSuelo()) {
			Rectangle pies = new Rectangle(entidad.getX(), entidad.getY() - 2, entidad.getAncho(), 3);

			for (Rectangle suelo : colisiones) {
				if (pies.overlaps(suelo)) {
					entidad.setEnSuelo(true);
					if (entidad.getVelocidadY() <= 0) {
						entidad.setY(suelo.y + suelo.height);
						entidad.setVelocidadY(0);
					}

				}
			}
		}

		if (chocoPared && entidad instanceof Enemigo && estabaEnSuelo) {
			entidad.saltar();
		}
	}

    private void chequearPendiente(Entidad entidad) {
        float centroX = entidad.getX() + entidad.getAncho() / 2f;

        for (Polygon pendiente : pendientes) {
            float[] puntos = pendiente.getTransformedVertices();

            float xInicio = puntos[0];
            float yInicio = puntos[1];

            float xFin = puntos[2];
            float yFin = puntos[3];

            if (xInicio > xFin) {
                float auxX = xInicio;
                float auxY = yInicio;

                xInicio = xFin;
                yInicio = yFin;

                xFin = auxX;
                yFin = auxY;
            }

            if (centroX < xInicio || centroX > xFin) {
                continue;
            }

            float progreso = (centroX - xInicio) / (xFin - xInicio);
            float ySuelo = yInicio + progreso * (yFin - yInicio);

            if (entidad.getVelocidadY() <= 0
                && entidad.getY() <= ySuelo
                && entidad.getY() + entidad.getAlto() >= ySuelo) {

                entidad.setY(ySuelo);
                entidad.setVelocidadY(0);
                entidad.setEnSuelo(true);
                return;
            }
        }
    }

    public ArrayList<Rectangle> getColisiones() {
		return colisiones;
	}

	public void dibujarHitboxes(ShapeRenderer shapeRenderer) {
		shapeRenderer.setColor(Color.BLUE);
		for (Rectangle colision : colisiones) {
			shapeRenderer.rect(colision.x, colision.y, colision.width, colision.height);
		}
	}
}
