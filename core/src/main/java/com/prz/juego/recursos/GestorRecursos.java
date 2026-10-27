package com.prz.juego.recursos;

import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.Map;

public class GestorRecursos {

    private static final Map<String, Texture> TEXTURAS = new HashMap<>();

    public static Texture obtenerTextura(String ruta) {
        if (!TEXTURAS.containsKey(ruta)) {
            TEXTURAS.put(ruta, new Texture(ruta));
        }
        return TEXTURAS.get(ruta);
    }

    public static void dispose() {
        for (Texture textura : TEXTURAS.values()) {
            textura.dispose();
        }
        TEXTURAS.clear();
    }
}
