package com.prz.juego.pantallas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.prz.juego.niveles.Nivel2;
import com.prz.juego.utilidades.Config;
import com.prz.juego.utilidades.Render;

public class PantallaMenu implements Screen {

    private Stage stage;
    private Texture texturaFondo;
    private BitmapFont fontTitulo;
    private BitmapFont fontSubtitulo;
    private BitmapFont fontBoton;

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Config.ANCHO, Config.ALTO));
        Gdx.input.setInputProcessor(stage);

        // 1. Cargar la imagen de fondo
        texturaFondo = new Texture("menuSP3.png"); // Ajustá la ruta a tu PNG
        Image imagenFondo = new Image(texturaFondo);
        imagenFondo.setFillParent(true); // Estirar fondo a toda la pantalla
        stage.addActor(imagenFondo);

        // 2. Definir Fuentes y Paleta Épica Medieval
        fontTitulo = new BitmapFont();
        fontTitulo.getData().setScale(3.5f);

        fontSubtitulo = new BitmapFont();
        fontSubtitulo.getData().setScale(1.8f);

        fontBoton = new BitmapFont();
        fontBoton.getData().setScale(2f);

        // Colores: Dorado Real y Rojo Carmesí
        Color colorDorado = new Color(0.95f, 0.78f, 0.2f, 1f);
        Color colorRojoBordo = new Color(0.85f, 0.15f, 0.15f, 1f);
        Color colorTextoBoton = new Color(0.9f, 0.9f, 0.8f, 1f);

        // 3. Estilos de Etiquetas (Labels)
        Label.LabelStyle estiloTitulo = new Label.LabelStyle(fontTitulo, colorDorado);
        Label.LabelStyle estiloSubtitulo = new Label.LabelStyle(fontSubtitulo, colorRojoBordo);

        Label tituloPrincipal = new Label("SUPER PABLO 3", estiloTitulo);
        Label subtitulo = new Label("~ LA ÚLTIMA BATALLA ~", estiloSubtitulo);

        // 4. Estilo de Botones
        TextButton.TextButtonStyle estiloBoton = new TextButton.TextButtonStyle();
        estiloBoton.font = fontBoton;
        estiloBoton.fontColor = colorTextoBoton;
        estiloBoton.downFontColor = colorRojoBordo; // Color al presionar
        estiloBoton.overFontColor = colorDorado;    // Color al pasar el mouse (Hover)

        TextButton btnJugar = new TextButton("JUGAR", estiloBoton);
        TextButton btnOpciones = new TextButton("OPCIONES", estiloBoton);
        TextButton btnSalir = new TextButton("SALIR", estiloBoton);

        // 5. Eventos de los Botones
        btnJugar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Iniciando carga del Nivel 2...");

                // Liberar memoria del menú antes de cambiar
                dispose();

                // Cambiar a la pantalla Load2Screen
                ((Game) Gdx.app.getApplicationListener()).setScreen(new PantallaJuego());
            }
        });

        btnOpciones.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Abrir Opciones...");
                // Acá podés hacer un setScreen(new PantallaOpciones());
            }
        });

        btnSalir.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        // 6. Maquetación con Tabla
        Table tabla = new Table();
        tabla.setFillParent(true);

        // Título y Subtítulo arriba
        tabla.add(tituloPrincipal).padBottom(5).row();
        tabla.add(subtitulo).padBottom(60).row();

        // Menú de Botones
        tabla.add(btnJugar).padBottom(25).width(250).height(50).row();
        tabla.add(btnOpciones).padBottom(25).width(250).height(50).row();
        tabla.add(btnSalir).width(250).height(50).row();

        stage.addActor(tabla);
    }

    @Override
    public void render(float delta) {

        Render.limpiarPantalla();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (texturaFondo != null) texturaFondo.dispose();
        if (fontTitulo != null) fontTitulo.dispose();
        if (fontSubtitulo != null) fontSubtitulo.dispose();
        if (fontBoton != null) fontBoton.dispose();
    }
}
