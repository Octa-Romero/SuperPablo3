package com.prz.juego.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.prz.juego.Principal;
import com.prz.juego.recursos.Imagen;
import com.prz.juego.utilidades.Config;
import com.prz.juego.utilidades.Render;

public class PantallaOpciones implements Screen {

    private Stage stage;
    private Imagen fondo;

    private BitmapFont fontTitulo;
    private BitmapFont fontBoton;

    private Principal juego;
    private Render render;

    private boolean mostrarResoluciones = false;

    private Table contenedor;
    private Table listaResoluciones;
    private Label titulo;
    private TextButton btnVolumen;
    private TextButton btnSalir;
    private TextButton btnResolucion;
    private TextButton btnToggleFullscreen;


    public PantallaOpciones(Principal juego) {
        this.juego = juego;
        this.render = juego.getRender();
    }


    @Override
    public void show() {
        inicializarStage();
        inicializarFondo();
        inicializarFuentes();
        crearUI();

        Gdx.input.setInputProcessor(stage);
    }

    private void inicializarStage() {
        stage = new Stage(new ExtendViewport(Config.ANCHO_BASE, Config.ALTO_BASE));
    }

    private void inicializarFondo() {
        fondo = new Imagen("fondoAjustes.jpg");

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

        fontBoton = new BitmapFont();
        fontBoton.getData().setScale(2f);
    }


    private void crearUI() {

        titulo = new Label(
            "OPCIONES",
            new Label.LabelStyle(fontTitulo, Color.GOLD)
        );

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = fontBoton;

        style.fontColor = Color.WHITE;
        style.overFontColor = Color.GOLD;
        style.downFontColor = Color.RED;

        btnVolumen = new TextButton("VOLUMEN", style);
        btnResolucion = new TextButton("RESOLUCIÓN", style);
        btnSalir = new TextButton("SALIR", style);

        listaResoluciones = new Table();
        listaResoluciones.setVisible(false);

        agregarResolucion("800x450", 800, 450, style);
        agregarResolucion("1280x720", 1280, 720, style);
        agregarResolucion("1600x900", 1600, 900, style);
        agregarResolucion("1920x1080", 1920, 1080, style);

        btnToggleFullscreen = new TextButton("MODO VENTANA", style);

        btnResolucion.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mostrarResoluciones = !mostrarResoluciones;
                listaResoluciones.setVisible(mostrarResoluciones);
                actualizarInterfaz();
            }
        });

        btnToggleFullscreen.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                Config.toggleFullscreen();

                btnResolucion.setText("RESOLUCIÓN");

                btnToggleFullscreen.setText(Config.fullscreen ? "MODO VENTANA" : "PANTALLA COMPLETA");

                stage.getViewport().update(Config.getAncho(), Config.getAlto(), true);
                actualizarFondo();
            }
        });

        btnSalir.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                juego.setScreen(new PantallaMenu(juego));
            }
        });

        contenedor = new Table();
        contenedor.setFillParent(true);

        contenedor.add(titulo).padBottom(30).row();

        contenedor.add(btnVolumen).size(250, 50).padBottom(15).row();
        contenedor.add(btnResolucion).size(250, 50).padBottom(10).row();

        contenedor.add(btnToggleFullscreen).size(250, 50).padBottom(10).row();

        contenedor.add(btnSalir).size(250, 50);

        stage.addActor(contenedor);
    }

    private void actualizarInterfaz() {
        contenedor.clearChildren();

        contenedor.add(titulo).padBottom(30).row();

        contenedor.add(btnVolumen).size(250, 50).padBottom(15).row();
        contenedor.add(btnResolucion).size(250, 50).padBottom(10).row();

        if (mostrarResoluciones) {
            contenedor.add(listaResoluciones).row();
        }
        contenedor.add(btnToggleFullscreen).size(250, 50).padBottom(10).row();

        contenedor.add(btnSalir).size(250, 50);
    }

    private void agregarResolucion(String text, int w, int h, TextButton.TextButtonStyle style) {

        TextButton btn = new TextButton(text, style);

        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                btnResolucion.setText("RESOLUCIÓN: " + text);
                listaResoluciones.setVisible(false);
                mostrarResoluciones = false;

                Config.setWindowed(w, h);

                stage.getViewport().update(Config.getAncho(), Config.getAlto(), true);

                actualizarFondo();

                btnToggleFullscreen.setText("PANTALLA COMPLETA");

                actualizarInterfaz();
            }
        });

        listaResoluciones.add(btn).size(250, 40).row();
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
        fontBoton.dispose();
    }
}
