package com.projectoop.javainvaders;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
    final JavaInvadersGame game;        // referência ao jogo global
    private Stage stage;       
    private Texture backgroundTexture;


    public MainMenuScreen(JavaInvadersGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        // ScreenViewport se adapta à tela
        stage = new Stage(new ScreenViewport());
        
        // permite o reconhecimente de mouse e teclado
        Gdx.input.setInputProcessor(stage);

        // determina uma fonte e estilo padrão para os botões
        BitmapFont font = new BitmapFont();
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;

        TextButton newGameButton = new TextButton("New Game", buttonStyle);
        TextButton loadGameButton = new TextButton("Load Game", buttonStyle);
        TextButton exitGameButton = new TextButton("Quit Game", buttonStyle);

        backgroundTexture = new Texture(Gdx.files.internal("MainMenuBackground.png"));
        Image backgroundImage = new Image(backgroundTexture);

        // organiza o layout
        Table table = new Table();
        table.setFillParent(true);  // ocupa a tela inteira
        backgroundImage.setFillParent(true);
        
        table.bottom();
        table.add(newGameButton).pad(10);
        table.row();
        table.add(loadGameButton).pad(10);
        table.row();
        table.add(exitGameButton).pad(10);
        table.padBottom(50);

        // adiciona a função de sair do jogo para o botão de sair
        exitGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent e, Actor a) {
                Gdx.app.exit();
            }
        });

        stage.addActor(backgroundImage);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        // Draw your screen here. "delta" is the time since last render in seconds.
        Gdx.gl.glClearColor(0, 0, 0.1f, 1);       // limpa a tela para um azul bem escuro(0.1f no azul)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);       // aplica a limpeza

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));      // stage calcula a logica de animações
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your screen here. The parameters represent the new window size.
        stage.getViewport().update(width, height, true);        // atualiza o viewport caso o usuario redimensione a tela
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
        stage.dispose();  // libera a memória(NÂO RETIRAR)
        backgroundTexture.dispose();
    }

}
