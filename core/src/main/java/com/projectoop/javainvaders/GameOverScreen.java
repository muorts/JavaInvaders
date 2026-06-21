package com.projectoop.javainvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;

public class GameOverScreen implements Screen {
    final JavaInvadersGame game;
    private Stage stage;
    private BitmapFont font;
    private int finalScore;
    private Texture backgroundTexture;

    private Sound confirmSound;
    private Music backgroundMusic;

    private static final float GAME_WIDTH = 800;
    private static final float GAME_HEIGHT = 600;

    public GameOverScreen(JavaInvadersGame game, int score) {
        this.game = game;
        this.finalScore = score;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(GAME_WIDTH, GAME_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        confirmSound = Gdx.audio.newSound(Gdx.files.internal("confirm.mp3"));
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("gameOver.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);
        backgroundMusic.play();

        // Carrega a imagem de fundo e ajusta para o tamanho da tela
        // TROQUE O NOME "GameOverBackground.png" PELO NOME EXATO DO SEU ARQUIVO DE IMAGEM!
        backgroundTexture = new Texture(Gdx.files.internal("GameBackground.png"));
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);

        font = new BitmapFont(Gdx.files.internal("GameFont.fnt"));
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        
        // Estilo dos Textos (Labels)
        Label.LabelStyle labelStyleTitle = new Label.LabelStyle();
        labelStyleTitle.font = font;
        labelStyleTitle.fontColor = Color.RED;

        Label.LabelStyle labelStyleScore = new Label.LabelStyle();
        labelStyleScore.font = font;
        labelStyleScore.fontColor = Color.WHITE;

        Label titleLabel = new Label("GAME OVER", labelStyleTitle);
        titleLabel.setFontScale(1.5f); // Aumenta o texto

        Label scoreLabel = new Label("Final Score: " + finalScore, labelStyleScore);
        scoreLabel.setFontScale(0.6f);

        // Estilo dos Botões
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = Color.YELLOW;

        TextButton menuButton = new TextButton("Back Menu", buttonStyle);
        menuButton.getLabel().setFontScale(0.5f);
        
        TextButton exitButton = new TextButton("Exit Game", buttonStyle);
        exitButton.getLabel().setFontScale(0.5f);

        // Ações dos botões
        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                confirmSound.play(1f);

                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                confirmSound.play(1f);

                Gdx.app.exit();
            }
        });

        // Organização na Tela
        Table table = new Table();
        table.setFillParent(true);
        
        table.add(titleLabel).padBottom(20).row();
        table.add(scoreLabel).padBottom(60).row();
        table.add(menuButton).padBottom(20).row();
        table.add(exitButton);

        // A ordem de adição importa: primeiro o fundo, depois a interface!
        stage.addActor(backgroundImage); 
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0, 0, 1); // Fundo vermelho bem escuro para Game Over
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
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
        stage.dispose();
        font.dispose();
        backgroundTexture.dispose(); // Não esqueça de liberar a memória da textura
        backgroundMusic.dispose();
        confirmSound.dispose();
    }
}