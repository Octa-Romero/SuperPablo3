package com.prz.juego.pantallas;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.prz.juego.Principal;
import com.prz.juego.utilidades.Config;

public class MenuPausa {

    private Stage stage;
    private Principal juego;
    private boolean activo = false;
    private PantallaJuego pantallaJuego;

    private BitmapFont font;

    public MenuPausa(Principal juego, PantallaJuego anteriorPantalla) {
        this.juego = juego;
        this.pantallaJuego =  anteriorPantalla;

        stage = new Stage(new FitViewport(Config.ANCHO_BASE, Config.ALTO_BASE));
        stage.setKeyboardFocus(null);
        stage.setScrollFocus(null);

        font = new BitmapFont();
        font.getData().setScale(2f);

        mostrar();
    }

    private void mostrar() {

        Table contenedor = new Table();
        contenedor.setFillParent(true);

        Label titulo = new Label("PAUSA", new Label.LabelStyle(font, Color.WHITE));

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;
        style.overFontColor = Color.YELLOW;
        style.downFontColor = Color.RED;

        TextButton reanudar = new TextButton("REANUDAR", style);
        TextButton menu = new TextButton("SALIR AL MENU", style);


        reanudar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pantallaJuego.setPausa(false);
                desactivar();
            }
        });

        menu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                juego.setScreen(new PantallaMenu(juego));
            }
        });

        contenedor.add(titulo).padBottom(40).row();
        contenedor.add(reanudar).width(250).height(50).padBottom(20).row();
        contenedor.add(menu).width(250).height(50);

        stage.addActor(contenedor);
    }

    public void activar() {
        activo = true;
        stage.getRoot().setVisible(true);
        stage.getRoot().setTouchable(Touchable.enabled);
    }

    public void desactivar() {
        activo = false;
        stage.getRoot().setVisible(false);
        stage.getRoot().setTouchable(Touchable.disabled);
    }

    public void update(float delta) {
        if(activo) {
            stage.act(delta);
        }
    }

    public void render() {
        if (activo) {
            stage.draw();
        }
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        font.dispose();
    }

    public Stage getStage() {
        return this.stage;
    }
}
