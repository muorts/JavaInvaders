package com.projectoop.javainvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class PauseScreen implements Screen {
    final JavaInvadersGame game;
    private GameScreen pausedGameScreen;        // guarda a tela de jogo congelada
    private Texture backgroundTexture;
    private Stage stage;

    private static final float GAME_WIDTH = 800;
    private static final float GAME_HEIGHT = 600;


    public PauseScreen(JavaInvadersGame game, GameScreen pausedScreen) {
        this.game = game;
        this.pausedGameScreen = pausedScreen;
    }


    @Override
    public void show() {
        stage = new Stage(new FitViewport(GAME_WIDTH, GAME_HEIGHT));
        backgroundTexture = new Texture(Gdx.files.internal("GamePause.png"));
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            // devolve a tela congelada para o roteador do jogo
            game.setScreen(pausedGameScreen);
            dispose();      // joga fora essa tela
        }
    }

    @Override
    public void resize(int width, int height) {
        if(width <= 0 || height <= 0) return;
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
    }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
    }
}