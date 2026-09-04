package com.prz.juego.pantallas;

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
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import com.prz.juego.Principal;
import com.prz.juego.recursos.Imagen;
import com.prz.juego.utilidades.Config;
import com.prz.juego.utilidades.Render;

public class PantallaMenu implements Screen {

    private Stage stage;
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

        inicializarStage();
        inicializarFondo();
        inicializarFuentes();
        crearInterfaz();

        Gdx.input.setInputProcessor(stage);
    }

    private void inicializarStage() {
        stage = new Stage(new ExtendViewport(Config.ANCHO_BASE, Config.ALTO_BASE));
    }

    private void inicializarFondo() {
        fondo = new Imagen("menuSP3.png");
        actualizarFondo();
    }

    private void actualizarFondo() {
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();

        fondo.setSize(w, h);
        fondo.setPosition(0, 0);
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

        Label titulo = new Label(
            "SUPER PABLO 3",
            new Label.LabelStyle(fontTitulo, Color.GOLD)
        );

        Label subtitulo = new Label(
            "~ LA ÚLTIMA BATALLA ~",
            new Label.LabelStyle(fontSubtitulo, Color.RED)
        );

        TextButton.TextButtonStyle estiloBoton = new TextButton.TextButtonStyle();
        estiloBoton.font = fontBoton;
        estiloBoton.fontColor = Color.WHITE;
        estiloBoton.overFontColor = Color.GOLD;
        estiloBoton.downFontColor = Color.RED;

        TextButton btnJugar = new TextButton("JUGAR", estiloBoton);
        TextButton btnOpciones = new TextButton("OPCIONES", estiloBoton);
        TextButton btnSalir = new TextButton("SALIR", estiloBoton);

        btnJugar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Iniciando carga del Nivel 2...");
                dispose();
                juego.setScreen(new PantallaJuego(juego));
            }
        });

        btnOpciones.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                juego.setScreen(new PantallaOpciones(juego));
            }
        });

        btnSalir.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        Table contenedor = new Table();
        contenedor.setFillParent(true);

        contenedor.add(titulo).padBottom(5).row();
        contenedor.add(subtitulo).padBottom(60).row();

        contenedor.add(btnJugar).width(250).height(50).padBottom(25).row();
        contenedor.add(btnOpciones).width(250).height(50).padBottom(25).row();
        contenedor.add(btnSalir).width(250).height(50);

        stage.addActor(contenedor);
    }

    @Override
    public void render(float delta) {
        render.limpiarPantalla();

        render.begin(stage.getCamera());
        fondo.dibujar(render.getBatch());
        render.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        actualizarFondo();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        fondo.dispose();
        fontTitulo.dispose();
        fontSubtitulo.dispose();
        fontBoton.dispose();
    }
}
