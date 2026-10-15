
package com.prz.juego.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

import com.prz.juego.principal.Navegable;
import com.prz.juego.recursos.GestorRecursos;
import com.prz.juego.recursos.Imagen;
import com.prz.juego.utilidades.Config;
import com.prz.juego.utilidades.Render;
import com.prz.juego.entidades.Personajes;

public class SeleccionNivel implements Screen {

    private final Navegable nav;
    private final Personajes personajeElegido;

    private Stage stage;
    private BitmapFont font;
    private Imagen fondo;

    private Texture texturaPortal1;
    private Texture texturaPortal2;
    private Texture texturaPortal3;
    private Texture texturaPortal4;
    private Texture texturaPortal5;
    private Texture texturaPortal6;

    public SeleccionNivel(Navegable nav, Personajes personajeElegido) {
        this.nav = nav;
        this.personajeElegido = personajeElegido;
    }

    @Override
    public void show() {

        stage = new Stage(new FitViewport(Config.ANCHO_BASE, Config.ALTO_BASE));

        Gdx.input.setInputProcessor(stage);

        inicializarFuentes();
        inicializarFondo();
        inicializarPortales();
        crearInterfaz();
    }

    private void inicializarFuentes() {

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fuentes/alagard.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter parametro = new FreeTypeFontGenerator.FreeTypeFontParameter();

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
        texturaPortal1 = GestorRecursos.obtenerTextura("Menu/Portales/portal1.png");
        texturaPortal2 = GestorRecursos.obtenerTextura("Menu/Portales/portal2.png");
        texturaPortal3 = GestorRecursos.obtenerTextura("Menu/Portales/portal3.png");
        texturaPortal4 = GestorRecursos.obtenerTextura("Menu/Portales/portal4.png");
        texturaPortal5 = GestorRecursos.obtenerTextura("Menu/Portales/portal5.png");
        texturaPortal6 = GestorRecursos.obtenerTextura("Menu/Portales/portal6.png");
    }

    private ImageButton crearPortal(Texture textura, final int numeroNivel) {

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();

        style.imageUp = new TextureRegionDrawable(new TextureRegion(textura));

        ImageButton portal = new ImageButton(style);

        portal.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                String rutaNivel;

                switch (numeroNivel) {
                    case 1:
                        rutaNivel = "Niveles/Niveles/Level1.tmx";
                        break;
                    case 2:
                        rutaNivel = "Niveles/Niveles/Nivel2.1.tmx";
                        break;
                    case 3:
                        rutaNivel = "Niveles/Niveles/Nivel3.tmx";
                        break;
                    case 4:
                        rutaNivel = "Niveles/Niveles/Nivel4.tmx";
                        break;
                    case 5:
                        rutaNivel = "Niveles/Niveles/Nivel5.tmx";
                        break;
                    case 6:
                        rutaNivel = "Niveles/Niveles/Nivel6.tmx";
                        break;
                    default:
                        return;
                }

                nav.cambiarPantalla(new PantallaJuego(nav,personajeElegido, rutaNivel));
            }
        });

        return portal;
    }

    private void animarPortal(ImageButton portal, float retraso) {

        portal.getColor().a = 0f;

        portal.addAction(Actions.sequence(Actions.delay(retraso), Actions.fadeIn(0.6f)));
    }

    private void crearInterfaz() {

        Table principal = new Table();
        principal.setFillParent(true);

        Label titulo = new Label("SELECCIÓN DE NIVEL", new Label.LabelStyle(font, Color.WHITE));


        titulo.getColor().a = 0f;

        titulo.addAction(Actions.sequence(Actions.delay(0.2f), Actions.fadeIn(0.6f)));

        ImageButton portal1 = crearPortal(texturaPortal1, 1);
        ImageButton portal2 = crearPortal(texturaPortal2, 2);
        ImageButton portal3 = crearPortal(texturaPortal3, 3);
        ImageButton portal4 = crearPortal(texturaPortal4, 4);
        ImageButton portal5 = crearPortal(texturaPortal5, 5);
        ImageButton portal6 = crearPortal(texturaPortal6, 6);

        animarPortal(portal1, 0.5f);
        animarPortal(portal2, 0.7f);
        animarPortal(portal3, 0.9f);
        animarPortal(portal4, 1.1f);
        animarPortal(portal5, 1.3f);
        animarPortal(portal6, 1.5f);


        principal.add(titulo).colspan(3).padBottom(20).row();
        principal.add(portal1).width(170).height(170).pad(5);
        principal.add(portal2).width(170).height(170).pad(5);
        principal.add(portal3).width(170).height(170).pad(5).row();
        principal.add(portal4).width(170).height(170).pad(5);
        principal.add(portal5).width(170).height(170).pad(5);
        principal.add(portal6).width(170).height(170).pad(5);

        stage.addActor(principal);
    }

    @Override
    public void render(float delta) {

        Render.limpiarPantalla();

        // Dibujar el fondo.
        Render.begin(stage.getCamera());
        fondo.dibujar(Render.batch);
        Render.end();

        // Actualizar y dibujar las animaciones de la interfaz.
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

        stage.getViewport().update(width, height, true);
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

        if (stage != null) {
            stage.dispose();
        }

        if (font != null) {
            font.dispose();
        }

        if (fondo != null) {
            fondo.dispose();
        }
    }
}
