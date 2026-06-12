package com.projectoop.javainvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {
    final JavaInvadersGame game;
    private OrthographicCamera camera;
    private Viewport view;

    // variaveis que definem o tamanho da tela do jogo
    private static final float GAME_WIDHT = 800;
    private static final float GAME_HEIGHT = 600;

    public GameScreen(JavaInvadersGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        view = new FitViewport(GAME_WIDHT, GAME_HEIGHT, camera);

        camera.position.set(GAME_HEIGHT/2f, GAME_WIDHT/2f, 0);
    }

    @Override
    public void show() {
        // inicializar as entidades do jogo
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Deve ser chamado toda vez antes de desenhar algo se a câmera tiver se movido ou se a tela foi redimensionada.
        camera.update();

        game.sprite.setProjectionMatrix(camera.combined);

        // o sprite dever ser renderizado aqui
        // game.sprite.begin
        // DESENHO
        // game.sprite.end
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your screen here. The parameters represent the new window size.
        view.update(width, height);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
    }
}