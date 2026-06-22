package com.projectoop.javainvaders.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.projectoop.javainvaders.entities.JavaInvadersGame;


/**
 * Classe unicamente responsável por carregar a imagem de transição de níveis e
 * passar o player de nível, mantendo a sua pontuação anterior. 
 * Aceita apenas a tecla de espaço como input e retorna o foco para a GameScreen
 * passando o próximo nível.
 */
public class LevelCompleteScreen implements Screen {
    // atributos de jogo
    final JavaInvadersGame game;
    private int previousLevel;
    private int previousPoints;

    // atributos de renderização e design
    private Stage stage;
    private Texture backgroundTexture;

    // variaveis que definem o tamanho da tela do jogo
    private static final float GAME_WIDTH = 800;
    private static final float GAME_HEIGHT = 600;

    public LevelCompleteScreen(JavaInvadersGame game, int level, int points) {
        this.game = game;
        previousLevel = level;
        previousPoints = points;
    }

    /**
     * Método chamado quando essa tela está com o foco.
     * Método oposto ao hide().
     */
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

    /**
     * Realiza a renderização da tela. 
     * Método chamado naturalmente pela LibGDX.
     */
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
            game.setScreen(new GameScreen(game, newLevel, previousPoints));
            dispose(); // Destrói a tela de Level Completed para liberar memória
        }
    }

    /**
     * método para redefinir o tamanho da imagem
     */
    @Override
    public void resize(int width, int height) {
        if(width <= 0 || height <= 0) return;
        stage.getViewport().update(width, height, true);
    }

    // método a ser utilizado quando a tela é tirada de foco(ex: quando minimizar a tela)
    @Override
    public void pause() {}

    // método a ser utilizado quando a tela retorna ao foco
    @Override
    public void resume() {}

    // método a ser utilizado quando a tela retorna ao foco
    @Override
    public void hide() {}

    @Override
    public void dispose() {
        // Libera a memória das texturas e stages 
        if (stage != null) stage.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();        
    }
}