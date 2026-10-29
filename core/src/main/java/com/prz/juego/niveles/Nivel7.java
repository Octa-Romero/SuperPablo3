package com.prz.juego.niveles;

import com.prz.juego.entidades.jefes.Retumbar;
import com.prz.juego.entidades.jugadores.Jugador;
import com.prz.juego.utilidades.Musica;

public class Nivel7 extends Nivel {

    public static final String RUTA = "Niveles/Niveles/Nivel7.tmx";
    private static final float CENTRO_MAPA = 688;
    private static final float ANCHO_MAPA = CENTRO_MAPA * 2;
    private static final float SUELO = 210;
    private static final float X_INICIAL_JUGADOR = 100;
    private static final float Y_INICIAL_JUGADOR = 200;

    private Retumbar jefe;

    public Nivel7() {
        musica = Musica.RETUMBAR;
    }

    @Override
    protected String getRutaMapa() {
        return RUTA;
    }

    @Override
    protected void configurarContenidoEspecifico(Jugador jugador) {
        // El suelo dañino del ataque de suelo cubre todo el ancho del mapa.
        jefe = new Retumbar(CENTRO_MAPA, SUELO, jugador, 0, ANCHO_MAPA);

        // El cuerpo va al fondo (se dibuja detras de todo); manos y onda, normales.
        agregarEntidadAlFondo(jefe);
        agregarEntidad(jefe.getManoIzquierda());
        agregarEntidad(jefe.getManoDerecha());
        agregarEntidad(jefe.getOndaSuelo());
    }

    @Override
    public Retumbar getJefe() {
        return jefe;
    }

    @Override
    protected float getPosicionInicialX() {
        return X_INICIAL_JUGADOR;
    }

    @Override
    protected float getPosicionInicialY() {
        return Y_INICIAL_JUGADOR;
    }
}
