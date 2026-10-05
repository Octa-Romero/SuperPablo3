
package com.prz.juego.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

import com.prz.juego.principal.Navegable;
import com.prz.juego.recursos.Imagen;
import com.prz.juego.utilidades.Config;
import com.prz.juego.utilidades.Render;

public class SeleccionNivel implements Screen {

    private final Navegable nav;

    private Stage stage;

    private BitmapFont font;

    private Imagen fondo;

    private Texture texturaPortal1;
    private Texture texturaPortal2;
    private Texture texturaPortal3;
    private Texture texturaPortal4;
    private Texture texturaPortal5;

    public SeleccionNivel(Navegable nav) {
        this.nav = nav;
    }

    @Override
    public void show() {

        stage = new Stage(
            new FitViewport(
                Config.ANCHO_BASE,
                Config.ALTO_BASE
            )
        );

        Gdx.input.setInputProcessor(stage);

        inicializarFuentes();
        inicializarFondo();
        inicializarPortales();
        crearInterfaz();
    }

    private void inicializarFuentes() {

        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(
                Gdx.files.internal("assets/Fuentes/alagard.ttf")
            );

        FreeTypeFontGenerator.FreeTypeFontParameter parametro =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        parametro.size = 48;
        parametro.color = Color.WHITE;

        parametro.shadowOffsetX = 3;
        parametro.shadowOffsetY = 3;
        parametro.shadowColor = new Color(0, 0, 0, 0.9f);

        font = generator.generateFont(parametro);

        generator.dispose();
    }

    private void inicializarFondo() {

        fondo = new Imagen("Menu/seleccionNivel.png");

        float ancho = stage.getViewport().getWorldWidth();
        float alto = stage.getViewport().getWorldHeight();

        fondo.setSize(ancho, alto);
        fondo.setPosition(0, 0);
    }

    private void inicializarPortales() {

        texturaPortal1 =
            new Texture(
                Gdx.files.internal("Menu/Portales/portal1.png")
            );

        texturaPortal2 =
            new Texture(
                Gdx.files.internal("Menu/Portales/portal2.png")
            );

        texturaPortal3 =
            new Texture(
                Gdx.files.internal("Menu/Portales/portal3.png")
            );

        texturaPortal4 =
            new Texture(
                Gdx.files.internal("Menu/Portales/portal4.png")
            );

        texturaPortal5 =
            new Texture(
                Gdx.files.internal("Menu/Portales/portal5.png")
            );
    }

    private ImageButton crearPortal(Texture textura, final int numeroNivel) {

        ImageButton.ImageButtonStyle style =
            new ImageButton.ImageButtonStyle();

        style.imageUp =
            new TextureRegionDrawable(
                new TextureRegion(textura)
            );

        ImageButton portal =
            new ImageButton(style);

        portal.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {

                System.out.println(
                    "Nivel " + numeroNivel + " seleccionado"
                );
            }
        });

        return portal;
    }

    private void crearInterfaz() {

        Table principal = new Table();

        principal.setFillParent(true);

        Label titulo = new Label(
            "SELECCIÓN DE NIVEL",
            new Label.LabelStyle(font, Color.WHITE)
        );

        ImageButton portal1 =
            crearPortal(texturaPortal1, 1);

        ImageButton portal2 =
            crearPortal(texturaPortal2, 2);

        ImageButton portal3 =
            crearPortal(texturaPortal3, 3);

        ImageButton portal4 =
            crearPortal(texturaPortal4, 4);

        ImageButton portal5 =
            crearPortal(texturaPortal5, 5);

        /*
         * Título
         */
        principal.add(titulo)
            .colspan(3)
            .padBottom(20)
            .row();

        /*
         * Primera fila
         */
        principal.add(portal1)
            .width(170)
            .height(170)
            .pad(5);

        principal.add(portal2)
            .width(170)
            .height(170)
            .pad(5);

        principal.add(portal3)
            .width(170)
            .height(170)
            .pad(5)
            .row();

        /*
         * Segunda fila
         */
        principal.add(portal4)
            .width(170)
            .height(170)
            .pad(5);

        principal.add(portal5)
            .width(170)
            .height(170)
            .pad(5);

        stage.addActor(principal);
    }

    @Override
    public void render(float delta) {

        Render.limpiarPantalla();

        /*
         * Dibujar fondo
         */
        Render.begin(stage.getCamera());

        fondo.dibujar(Render.batch);

        Render.end();

        /*
         * Dibujar interfaz
         */
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

        stage.getViewport().update(
            width,
            height,
            true
        );
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {

        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {

        stage.dispose();

        font.dispose();

        fondo.dispose();

        texturaPortal1.dispose();
        texturaPortal2.dispose();
        texturaPortal3.dispose();
        texturaPortal4.dispose();
        texturaPortal5.dispose();
    }
}
