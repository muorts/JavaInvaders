package com.projectoop.javainvaders;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;


public class MainMenuScreen implements Screen {
    final JavaInvadersGame game;        // referência ao jogo global
    private Stage stage;       
    private Texture backgroundTexture;
    private TextButton newGameButton;
    private TextButton exitGameButton;
    private BitmapFont font;

    private static final float GAME_WIDTH = 800;
    private static final float GAME_HEIGHT = 600;

    public MainMenuScreen(JavaInvadersGame game) {
        this.game = game;
    }

    private void setButtons() {
        // determina uma fonte e estilo padrão para os botões
        font = new BitmapFont(Gdx.files.internal("GameFont.fnt"));
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        font.getData().setScale(0.35f);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = Color.YELLOW;

        this.newGameButton = new TextButton("New Game", buttonStyle);
        this.exitGameButton = new TextButton("Quit Game", buttonStyle);


        // adiciona a função de sair do jogo para o botão de sair
        this.exitGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent e, Actor a) {
                Gdx.app.exit();
            }
        });

        // criar um novo jogo 
        this.newGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent e, Actor a) {
                game.setScreen(new GameScreen(game, 1, 0));     // começa no nível 1 com 0 pontos
                dispose();
            }
        });
    }

    // faz a configuração do layout da tela de menu
    private void setLayout(Table table, Image image) {
        
        table.setFillParent(true);  // ocupa a tela inteira
        image.setFillParent(true);
        
        table.bottom();
        table.padLeft(20).padBottom(60);
        table.add(newGameButton).padBottom(15).center();
        table.row();
        table.add(exitGameButton).center();
    }

    @Override
    public void show() {
        // ScreenViewport se adapta à tela
        stage = new Stage(new FitViewport(GAME_WIDTH, GAME_HEIGHT));
        
        // permite o reconhecimente de mouse e teclado
        Gdx.input.setInputProcessor(stage);

        // inicializa e estiliza os botões
        setButtons();

        backgroundTexture = new Texture(Gdx.files.internal("MainMenuBackground.png"));
        Image backgroundImage = new Image(backgroundTexture);

        // organiza o layout
        Table table = new Table();
        setLayout(table, backgroundImage);

        stage.addActor(backgroundImage);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.1f, 1);       // limpa a tela para um azul bem escuro(0.1f no azul)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);       // aplica a limpeza

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));      // stage calcula a logica de animações
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Verifica se a tela está minimizada e, se sim, não renderiza nada
        if(width <= 0 || height <= 0) return;

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
        // libera a memória(NÂO RETIRAR)
        stage.dispose();  
        backgroundTexture.dispose();
        font.dispose();
    }

}
