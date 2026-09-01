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

    public PantallaMenu(Principal juego)
    {
        this.juego = juego;
        this.render = juego.getRender();
    }


    @Override
    public void show() {
        camaraMenu = new Camara();
        camaraMenu.actualizarTamano(Config.ANCHO, Config.ALTO);

        stage = new Stage(new FitViewport(Config.ANCHO, Config.ALTO));
        Gdx.input.setInputProcessor(stage);

        fondo = new Imagen("menuSP3.png"); // Ajustá la ruta a tu PNG
        fondo.setPosition(0,0); // Estirar fondo a toda la pantalla
        fondo.setSize(Config.ANCHO, Config.ALTO);

        fontTitulo = new BitmapFont();
        fontTitulo.getData().setScale(3.5f);

        fontSubtitulo = new BitmapFont();
        fontSubtitulo.getData().setScale(1.8f);

        fontBoton = new BitmapFont();
        fontBoton.getData().setScale(2f);

        Color colorDorado = new Color(0.95f, 0.78f, 0.2f, 1f);
        Color colorRojoBordo = new Color(0.85f, 0.15f, 0.15f, 1f);
        Color colorTextoBoton = new Color(0.9f, 0.9f, 0.8f, 1f);

        Label.LabelStyle estiloTitulo = new Label.LabelStyle(fontTitulo, colorDorado);
        Label.LabelStyle estiloSubtitulo = new Label.LabelStyle(fontSubtitulo, colorRojoBordo);

        Label tituloPrincipal = new Label("SUPER PABLO 3", estiloTitulo);
        Label subtitulo = new Label("~ LA ÚLTIMA BATALLA ~", estiloSubtitulo);

        TextButton.TextButtonStyle estiloBoton = new TextButton.TextButtonStyle();
        estiloBoton.font = fontBoton;
        estiloBoton.fontColor = colorTextoBoton;
        estiloBoton.downFontColor = colorRojoBordo; // Color al presionar
        estiloBoton.overFontColor = colorDorado;    // Color al pasar el mouse (Hover)

        TextButton btnJugar = new TextButton("JUGAR", estiloBoton);
        TextButton btnOpciones = new TextButton("OPCIONES", estiloBoton);
        TextButton btnSalir = new TextButton("SALIR", estiloBoton);

        btnJugar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Iniciando carga del Nivel 2...");

                dispose();

                ((Game) Gdx.app.getApplicationListener()).setScreen(new PantallaJuego(juego));
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

        tabla.add(tituloPrincipal).padBottom(5).row();
        tabla.add(subtitulo).padBottom(60).row();

        tabla.add(btnJugar).padBottom(25).width(250).height(50).row();
        tabla.add(btnOpciones).padBottom(25).width(250).height(50).row();
        tabla.add(btnSalir).width(250).height(50).row();

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
    public void resize(int width, int height)
    {
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
