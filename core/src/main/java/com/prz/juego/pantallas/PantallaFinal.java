
package com.prz.juego.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.prz.juego.principal.Navegable;
import com.prz.juego.recursos.Imagen;
import com.prz.juego.utilidades.Config;
import com.prz.juego.utilidades.Render;

public class PantallaFinal implements Screen {

    private Stage stage;
    private Imagen fondo;

    private BitmapFont fontTitulo;
    private BitmapFont fontRelato;

    private Label textoEscena;
    private Label titulo;

    private final Navegable NAV;

    private int escenaActual = 0;

    private final String[] escenas = {
        "La batalla habia llegado a su fin.",

        "El sacrificio de Pablo salvó al reino y sello para siempre la Maldicion del Retumbar.",

        "El Retumbar fue derrotado, y con el desaparecio la oscuridad que durante tanto tiempo habia amenazado estas tierras.",

        "Sin embargo, la victoria tuvo un precio. Pablo entrego todo para que los demas pudieran vivir en paz.",

        "Su nombre jamas seria olvidado. Porque incluso cuando los heroes desaparecen, sus leyendas permanecen."
    };

    public PantallaFinal(Navegable NAV) {
        this.NAV = NAV;
    }

    @Override
    public void show() {
        inicializarStage();
        inicializarFondo();
        inicializarFuentes();
        crearInterfaz();

        Gdx.input.setInputProcessor(null);
    }

    private void inicializarStage() {
        stage = new Stage(
            new ExtendViewport(Config.ANCHO_BASE, Config.ALTO_BASE)
        );
    }

    private void inicializarFondo() {
        fondo = new Imagen("Menu/fondoFinal.png");
        actualizarFondo();
    }

    private void actualizarFondo() {
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();

        fondo.setSize(w, h);
        fondo.setPosition(0, 0);
    }

    private void inicializarFuentes() {

        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(
                Gdx.files.internal("Fuentes/alagard.ttf")
            );

        FreeTypeFontGenerator.FreeTypeFontParameter parametro =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        parametro.size = 48;
        parametro.color = Color.GOLD;
        parametro.borderWidth = 2f;
        parametro.borderColor = Color.BLACK;

        fontTitulo = generator.generateFont(parametro);

        parametro = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parametro.size = 22;
        parametro.color = Color.WHITE;
        parametro.borderWidth = 1.5f;
        parametro.borderColor = Color.BLACK;

        fontRelato = generator.generateFont(parametro);

        generator.dispose();
    }

    private void crearInterfaz() {

        titulo = new Label(
            "SUPER PABLO 3",
            new Label.LabelStyle(fontTitulo, Color.GOLD)
        );

        textoEscena = new Label(
            "",
            new Label.LabelStyle(fontRelato, Color.WHITE)
        );

        textoEscena.setAlignment(Align.center);
        textoEscena.setWrap(true);

        Table contenedor = new Table();
        contenedor.setFillParent(true);

        contenedor.add(titulo)
            .center()
            .padBottom(70)
            .row();

        contenedor.add(textoEscena)
            .width(Config.ANCHO_BASE * 0.85f)
            .center();

        stage.addActor(contenedor);

        titulo.getColor().a = 0f;
        textoEscena.getColor().a = 0f;

        mostrarSiguienteEscena();
    }

    private void mostrarSiguienteEscena() {

        if (escenaActual >= escenas.length) {
            mostrarFin();
            return;
        }

        textoEscena.setText(escenas[escenaActual]);
        textoEscena.getColor().a = 0f;

        textoEscena.addAction(
            Actions.sequence(
                Actions.fadeIn(2f),
                Actions.delay(4f),
                Actions.fadeOut(2f),
                Actions.run(() -> {
                    escenaActual++;
                    mostrarSiguienteEscena();
                })
            )
        );
    }

    private void mostrarFin() {

        textoEscena.setText("FIN");
        textoEscena.getColor().a = 0f;

        textoEscena.addAction(
            Actions.sequence(
                Actions.fadeIn(3f),
                Actions.delay(5f),
                Actions.fadeOut(2f),
                Actions.run(this::mostrarCreditos)
            )
        );
    }

    private void mostrarCreditos() {

        titulo.setText("SUPER PABLO 3");
        titulo.getColor().a = 0f;

        textoEscena.setText(
            "~ LA ULTIMA BATALLA ~\n\n" +

                "DESARROLLO\n\n" +

                "Octavio Romero\n" +
                "Interfaz, sistema multijugador\n\n" +

                "Santiago Papale\n" +
                "Arte, sprites, mapas e interfaz\n\n" +

                "Matias Rodrigo Zurita\n" +
                "Enemigos, musica y sonido\n\n" +

                "AGRADECIMIENTOS ESPECIALES\n\n" +

                "Leandro Zelaya\n" +
                "Por sus diseños de los juegos anteriores,\n" +
                "reutilizados en esta entrega.\n\n"


        );

        textoEscena.getColor().a = 0f;

        titulo.addAction(
            Actions.fadeIn(3f)
        );

        textoEscena.addAction(
            Actions.sequence(
                Actions.fadeIn(3f),
                Actions.delay(18f),
                Actions.fadeOut(3f),
                Actions.run(() -> {
                    NAV.cambiarPantalla(new PantallaMenu(NAV));
                })
            )
        );
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
        actualizarFondo();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {

        if (stage != null) {
            stage.dispose();
        }

        if (fondo != null) {
            fondo.dispose();
        }

        if (fontTitulo != null) {
            fontTitulo.dispose();
        }

        if (fontRelato != null) {
            fontRelato.dispose();
        }
    }
}
