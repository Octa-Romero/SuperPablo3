package com.prz.juego.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.prz.juego.principal.Navegable;
import com.prz.juego.principal.Principal;
import com.prz.juego.recursos.Imagen;
import com.prz.juego.utilidades.Config;
import com.prz.juego.utilidades.Render;
import com.prz.juego.entidades.Personajes;

public class SeleccionPersonaje implements Screen {

    private final Navegable nav;
    private Stage stage;
    private BitmapFont font;
    private BitmapFont fontBoton;
    private Imagen fondo;
    private Personajes personajeSeleccionado;
    private TextButton btnComenzar;

    public SeleccionPersonaje(Navegable nav) {
        this.nav = nav;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Config.ANCHO_BASE, Config.ALTO_BASE));
        Gdx.input.setInputProcessor(stage);
        inicializarFuentes();
        inicializarFondo();


        crearInterfaz();
    }

    private void inicializarFuentes() {

        FreeTypeFontGenerator titulo = new FreeTypeFontGenerator(Gdx.files.internal("assets/Fuentes/alagard.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter parametro = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parametro.size = 48;
        parametro.color = Color.WHITE;
        parametro.shadowOffsetX = 3;
        parametro.shadowOffsetY = 3;
        parametro.shadowColor = new Color(0, 0, 0, 0.9f);
        font = titulo.generateFont(parametro);
        titulo.dispose();


        FreeTypeFontGenerator boton = new FreeTypeFontGenerator( Gdx.files.internal( "assets/Fuentes/alagard.ttf" ) );
        FreeTypeFontGenerator.FreeTypeFontParameter parametroBoton = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parametroBoton.size = 28;
        parametroBoton.color = Color.WHITE;
        parametroBoton.shadowOffsetX = 2;
        parametroBoton.shadowOffsetY = 2;
        parametroBoton.shadowColor = new Color(0, 0, 0, 0.9f);
        fontBoton = boton.generateFont(parametroBoton);
        boton.dispose();
    }

    private void inicializarFondo() {
        fondo = new Imagen("Menu/seleccionPersonaje.png");
        actualizarFondo();
    }

    private void actualizarFondo() {

        float ancho = stage.getViewport().getWorldWidth();
        float alto = stage.getViewport().getWorldHeight();
        fondo.setSize(ancho, alto); fondo.setPosition(0, 0);
    }

    private void crearInterfaz() {

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        table.defaults().pad(10);

        Label titulo = new Label("SELECCIÓN DE PERSONAJE",
            new Label.LabelStyle(font, Color.WHITE));


        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = fontBoton;
        style.fontColor = Color.WHITE;
        style.overFontColor = Color.GOLD;
        style.downFontColor = Color.RED;

        TextButton btnPablo = new TextButton("PABLO", style);
        TextButton btnWalter = new TextButton("WALTER", style);

        btnComenzar = new TextButton("COMENZAR", style);
        btnComenzar.setVisible(false);

        btnPablo.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                personajeSeleccionado = Personajes.PABLO;
                btnComenzar.setVisible(true);
            }
        });

        btnWalter.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                personajeSeleccionado = Personajes.WALTER;
                btnComenzar.setVisible(true);
            }
        });

        btnComenzar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                if (personajeSeleccionado == null) return;

                nav.cambiarPantalla(new PantallaJuego(nav, personajeSeleccionado));
            }
        });

        table.add(titulo).padBottom(40).row();

        table.add(btnPablo).width(250).height(60).padBottom(20).row();
        table.add(btnWalter).width(250).height(60).padBottom(40).row();

        table.add(btnComenzar).width(300).height(70);

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla();
        Render.begin(stage.getCamera());
        fondo.dibujar(Render.batch);
        Render.end();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
        fontBoton.dispose();
        fondo.dispose();
    }
}
