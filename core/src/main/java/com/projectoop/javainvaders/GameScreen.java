package com.projectoop.javainvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;





public class GameScreen implements Screen {

    final JavaInvadersGame game;
    final Player player;
    private AlienFleet aliensFleet;
    private LaserManager laserManager;
    private BombManager bombManager;

    private OrthographicCamera camera;
    private Viewport view;
    private Stage stage;       
    private Texture backgroundTexture;

    // variaveis que definem o tamanho da tela do jogo
    private static final float GAME_WIDTH = 800;
    private static final float GAME_HEIGHT = 600;


    public GameScreen(JavaInvadersGame game) {
        this.game = game;
        this.player = new Player();
        this.aliensFleet = new AlienFleet();
        this.bombManager = new BombManager();
        this.laserManager = new LaserManager();

        camera = new OrthographicCamera();
        view = new FitViewport(GAME_WIDTH, GAME_HEIGHT, camera);
        camera.position.set(GAME_WIDTH/2f, GAME_HEIGHT/2f, 0);
    }

    @Override
    public void show() {
        // inicializar as entidades do jogo
        // ScreenViewport se adapta à tela
        stage = new Stage(new FitViewport(GAME_WIDTH, GAME_HEIGHT));
        

        backgroundTexture = new Texture(Gdx.files.internal("GameBackground.png"));
        Image backgroundImage = new Image(backgroundTexture);

        // organiza o layout
        Table table = new Table();
        table.setFillParent(true);  // ocupa a tela inteira
        backgroundImage.setFillParent(true);

        stage.addActor(backgroundImage);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        // Deve ser chamado toda vez antes de desenhar algo se a câmera tiver se movido ou se a tela foi redimensionada.
        camera.update();

        game.sprite.setProjectionMatrix(camera.combined);

        // Atualiza os estados das entidades do jogo
        updateStates(delta);
        // renderização
        game.sprite.begin();

        drawEntities();

        game.sprite.end();
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your screen here. The parameters represent the new window size.
        view.update(width, height, true);
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
        backgroundTexture.dispose();
        player.dispose();
        laserManager.dispose();
        aliensFleet.dispose();
        bombManager.dispose();
    }

    private void updateStates(float delta) {
        // atualização do jogo
        player.update(delta);
        Laser newLaser = player.shoot();
        if(newLaser != null)
            laserManager.addLaser(newLaser);

        laserManager.update(delta);
        aliensFleet.update(delta, bombManager.getBombs());
        bombManager.update(delta);
    }

    private void drawEntities() {
        player.draw(game.sprite);
        aliensFleet.draw(game.sprite);
        laserManager.draw(game.sprite);
        bombManager.draw(game.sprite);
    }
}