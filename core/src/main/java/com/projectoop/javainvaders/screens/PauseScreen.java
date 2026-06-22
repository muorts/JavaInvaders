package com.projectoop.javainvaders.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.projectoop.javainvaders.entities.JavaInvadersGame;

/**
 * Classe responsável por carregar a tela de pause e manter o estado do jogo salvo na memória.
 * Não possui nenhuma lógica e apenas aceita a tela 'esc' de input para retornar ao jogo.
 */
public class PauseScreen implements Screen {
    // atributos de jogo
    final JavaInvadersGame game;
    private GameScreen pausedGameScreen;        // guarda a tela de jogo congelada

    // atributos de desenho
    private Texture backgroundTexture;
    private Stage stage;

     // atributos que definem o tamanho da tela do jogo
    private static final float GAME_WIDTH = 800;
    private static final float GAME_HEIGHT = 600;


    public PauseScreen(JavaInvadersGame game, GameScreen pausedScreen) {
        this.game = game;
        this.pausedGameScreen = pausedScreen;
    }

    /**
     * Método chamado quando essa tela está com o foco.
     * Método oposto ao hide().
     * Carrega a imagem.
     */
    @Override
    public void show() {
        stage = new Stage(new FitViewport(GAME_WIDTH, GAME_HEIGHT));
        backgroundTexture = new Texture(Gdx.files.internal("GamePause.png"));
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);
    }

    /**
     * Realiza a renderização do jogo por completo. 
     * Método chamado naturalmente pela LibGDX.
     * Checa se o jogador quer voltar ao jogo.
     */
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

    // Verifica se a tela está minimizada e, se sim, não renderiza nada
    @Override
    public void resize(int width, int height) {
        if(width <= 0 || height <= 0) return;
        stage.getViewport().update(width, height, true);
    }

    // método a ser utilizado quando a tela é tirada de foco(ex: quando minimizar a tela)
    @Override
    public void pause() {
    }

    // método a ser utilizado quando a tela é tirada de foco(ex: quando minimizar a tela)
    @Override
    public void resume() {
    }

    // método a ser utilizado quando a tela retorna ao foco
    @Override
    public void hide() {
    }

    // método para liberar as texturas carregadas da memória
    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
    }
}