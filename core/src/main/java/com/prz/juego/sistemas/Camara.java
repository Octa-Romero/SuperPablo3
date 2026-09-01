package com.prz.juego.sistemas;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Camara {

	private OrthographicCamera camera;
	private Viewport viewport;

	// Resolución virtual fija para Pixel Art (16:9)
	// Con estos valores el bosque, las montañas y el nivel se ven en proporción real
	public static final float V_WIDTH = 800f;  // En lugar de 480f
	public static final float V_HEIGHT = 450f; // En lugar de 270f
	// Límites del mapa para que la cámara no muestre el exterior
	private float limiteMinX, limiteMaxX;
	private float limiteMinY, limiteMaxY;

	public Camara() {
		camera = new OrthographicCamera();
		viewport = new FitViewport(V_WIDTH, V_HEIGHT, camera);
		camera.position.set(V_WIDTH / 2f, V_HEIGHT / 2f, 0);
		camera.update();
	}

	/**
	 * Define los bordes del mapa (ejemplo: 320 tiles x 16px = 5120px de ancho)
	 */
	public void setLimitesMapa(float anchoMapaPx, float altoMapaPx) {
		this.limiteMinX = V_WIDTH / 2f;
		this.limiteMaxX = anchoMapaPx - (V_WIDTH / 2f);

		this.limiteMinY = V_HEIGHT / 2f;
		this.limiteMaxY = altoMapaPx - (V_HEIGHT / 2f);
	}

	public void seguirPersonaje(float targetX, float targetY, float delta) {
		// Velocidad de suavizado del seguimiento (0.1f = suave, 1f = rígido)
		float lerp = 10f * delta;
		// Movimiento interpolado
		camera.position.x += (targetX - camera.position.x) * lerp;
		camera.position.y += (targetY - camera.position.y) * lerp;

		// Aplicar límites si están configurados para no mostrar el borde negro
		if (limiteMaxX > limiteMinX) {
			camera.position.x = MathUtils.clamp(camera.position.x, limiteMinX, limiteMaxX);
		}
		if (limiteMaxY > limiteMinY) {
			camera.position.y = MathUtils.clamp(camera.position.y, limiteMinY, limiteMaxY);
		}

		camera.update();
	}

	public void actualizarTamano(int width, int height) {
		viewport.update(width, height, true);
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public Viewport getViewport() {
		return viewport;
	}
}