package com.projectoop.javainvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class LevelCompleteScreen implements Screen {
    final JavaInvadersGame game;
    private int previousLevel;
    private Stage stage;
    private Texture backgroundTexture;

    private static final float GAME_WIDTH = 800;
    private static final float GAME_HEIGHT = 600;

    public LevelCompleteScreen(JavaInvadersGame game, int level) {
        this.game = game;
        previousLevel = level;
    }

    @Override
    public void show() {
        // Inicializa o palco com o mesmo tamanho padrão do jogo
        stage = new Stage(new FitViewport(GAME_WIDTH, GAME_HEIGHT));

        // Carrega a imagem de level completed
        backgroundTexture = new Texture(Gdx.files.internal("Level_complete.png"));
        
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true); // Faz a imagem ocupar a tela inteira

        stage.addActor(backgroundImage);
    }

    @Override
    public void render(float delta) {
        // Limpa a tela com fundo preto
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Desenha a imagem
        stage.act(delta);
        stage.draw();

        // Se o jogador apertar Espaço, avança.
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            int newLevel = previousLevel + 1;
            System.out.println("Mudando para o nível: " + newLevel);
            game.setScreen(new GameScreen(game, newLevel));
            dispose(); // Destrói a tela de Level Completed para liberar memória
        }
    }

    @Override
    public void resize(int width, int height) {
        if(width <= 0 || height <= 0) return;
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        // Libera a memória das texturas e stages 
        if (stage != null) stage.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
    }
}