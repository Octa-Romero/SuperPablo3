package com.prz.juego.pantallas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import com.prz.juego.Principal;
import com.prz.juego.recursos.Imagen;
import com.prz.juego.sistemas.Camara;
import com.prz.juego.utilidades.Config;
import com.prz.juego.utilidades.Render;

public class PantallaMenu implements Screen {

    private Stage stage;
    private Camara camaraMenu;
    private Imagen fondo;

    private BitmapFont fontTitulo;
    private BitmapFont fontSubtitulo;
    private BitmapFont fontBoton;

    private Principal juego;
    private Render render;

    public PantallaMenu(Principal juego) {
        this.juego = juego;
        this.render = juego.getRender();
    }

    @Override
    public void show() {

        inicializarCamara();
        inicializarStage();
        inicializarFondo();
        inicializarFuentes();
        crearInterfaz();

        Gdx.input.setInputProcessor(stage);
    }


    private void inicializarCamara() {
        camaraMenu = new Camara();
        camaraMenu.actualizarTamano(Config.ANCHO, Config.ALTO);
    }

    private void inicializarStage() {
        stage = new Stage(new FitViewport(Config.ANCHO, Config.ALTO));
    }

    private void inicializarFondo() {
        fondo = new Imagen("menuSP3.png");
        fondo.setPosition(0, 0);
        fondo.setSize(Config.ANCHO, Config.ALTO);
    }

    private void inicializarFuentes() {
        fontTitulo = new BitmapFont();
        fontTitulo.getData().setScale(3.5f);

        fontSubtitulo = new BitmapFont();
        fontSubtitulo.getData().setScale(1.8f);

        fontBoton = new BitmapFont();
        fontBoton.getData().setScale(2f);
    }

    private void crearInterfaz() {

        Color dorado = new Color(0.95f, 0.78f, 0.2f, 1f);
        Color rojo = new Color(0.85f, 0.15f, 0.15f, 1f);
        Color texto = new Color(0.9f, 0.9f, 0.8f, 1f);

        Label titulo = new Label(
            "SUPER PABLO 3",
            new Label.LabelStyle(fontTitulo, dorado)
        );

        Label subtitulo = new Label(
            "~ LA ÚLTIMA BATALLA ~",
            new Label.LabelStyle(fontSubtitulo, rojo)
        );

        TextButton.TextButtonStyle estiloBoton = new TextButton.TextButtonStyle();
        estiloBoton.font = fontBoton;
        estiloBoton.fontColor = texto;
        estiloBoton.overFontColor = dorado;
        estiloBoton.downFontColor = rojo;

        TextButton btnJugar = new TextButton("JUGAR", estiloBoton);
        TextButton btnOpciones = new TextButton("OPCIONES", estiloBoton);
        TextButton btnSalir = new TextButton("SALIR", estiloBoton);

        btnJugar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Iniciando carga del Nivel 2...");
                dispose();
                ((Game) Gdx.app.getApplicationListener())
                    .setScreen(new PantallaJuego(juego));
            }
        });

        btnOpciones.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Abrir Opciones...");
            }
        });

        btnSalir.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        Table tabla = new Table();
        tabla.setFillParent(true);

        tabla.add(titulo).padBottom(5).row();
        tabla.add(subtitulo).padBottom(60).row();

        tabla.add(btnJugar).width(250).height(50).padBottom(25).row();
        tabla.add(btnOpciones).width(250).height(50).padBottom(25).row();
        tabla.add(btnSalir).width(250).height(50);

        stage.addActor(tabla);
    }

    @Override
    public void render(float delta) {
        render.limpiarPantalla();

        render.begin(camaraMenu.getCamera());
        fondo.dibujar(render.getBatch());
        render.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        camaraMenu.actualizarTamano(width, height);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (fondo != null) fondo.dispose();

        if (fontTitulo != null) fontTitulo.dispose();
        if (fontSubtitulo != null) fontSubtitulo.dispose();
        if (fontBoton != null) fontBoton.dispose();
    }
}
