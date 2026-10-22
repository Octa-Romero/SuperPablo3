
package com.prz.juego.sistemas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;


public class CuadroDialogo implements Disposable {

    private final BitmapFont fuente;
    private final ShapeRenderer shapeRenderer;

    private String mensaje = "";
    private boolean visible = false;

    public CuadroDialogo() {
        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(
                Gdx.files.internal("Fuentes/alagard.ttf")
            );

        FreeTypeFontGenerator.FreeTypeFontParameter parametro =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        parametro.size = 20;
        parametro.color = Color.WHITE;

        fuente = generator.generateFont(parametro);
        generator.dispose();

        shapeRenderer = new ShapeRenderer();

    }

    public void mostrar(String mensaje) {
        this.mensaje = mensaje;
        visible = true;
    }

    public void ocultar() {
        visible = false;
    }

    public boolean estaVisible() {
        return visible;
    }

    public void dibujar(SpriteBatch batch, Camera camara) {
        if (!visible) {
            return;
        }

        float ancho = camara.viewportWidth - 64;
        float alto = 150;
        float x = camara.position.x - camara.viewportWidth / 2f + 32;
        float y = camara.position.y - camara.viewportHeight / 2f + 24;

        batch.end();

        shapeRenderer.setProjectionMatrix(camara.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Fondo oscuro.
        shapeRenderer.setColor(0.09f, 0.15f, 0.17f, 1f);
        shapeRenderer.rect(x, y, ancho, alto);

        // Borde cian.
        shapeRenderer.setColor(0.22f, 0.87f, 0.88f, 1f);
        shapeRenderer.rect(x, y + alto - 4, ancho, 4);
        shapeRenderer.rect(x, y, ancho, 4);
        shapeRenderer.rect(x, y, 4, alto);
        shapeRenderer.rect(x + ancho - 4, y, 4, alto);

        // Placa verde del nombre.
        shapeRenderer.setColor(0.44f, 0.84f, 0.60f, 1f);
        shapeRenderer.rect(x + 110, y + alto - 39, 170, 27);

        shapeRenderer.end();

        batch.begin();

        // Nombre.
        fuente.draw(batch, "PANCRASIO", x + 120, y + alto - 20);

        // Mensaje.
        fuente.draw(batch, mensaje, x + 110, y + alto - 53, ancho - 140, Align.left, true);

        // Indicador para continuar.
        fuente.draw(batch, "▼", x + ancho - 28, y + 20);
    }

    @Override
    public void dispose() {
        fuente.dispose();
        shapeRenderer.dispose();
    }
}
