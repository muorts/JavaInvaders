package com.projectoop.javainvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;





public class GameScreen implements Screen {

    final JavaInvadersGame game;
    final Player player;
    private AlienFleet aliensFleet;         // gerenciador da horda de aliens
    private LaserManager laserManager;      // gerenciador dos lasers em tela
    private BombManager bombManager;        // gerenciador das bombas em tela
    private int currentLevel = 1;       // o jogo inicia no primeiro nível

    private OrthographicCamera camera;
    private Viewport view;
    private Stage stage;       
    private Texture backgroundTexture;
    private BitmapFont hudFont;

    // variaveis que definem o tamanho da tela do jogo
    private static final float GAME_WIDTH = 800;
    private static final float GAME_HEIGHT = 600;


    public GameScreen(JavaInvadersGame game, int Level) {
        currentLevel = Level;
        this.game = game;
        this.player = new Player();
        this.aliensFleet = new AlienFleet(Level);
        this.bombManager = new BombManager();
        this.laserManager = new LaserManager();

        camera = new OrthographicCamera();
        view = new FitViewport(GAME_WIDTH, GAME_HEIGHT, camera);
        camera.position.set(GAME_WIDTH/2f, GAME_HEIGHT/2f, 0);
        hudFont = new BitmapFont(Gdx.files.internal("GameFont.fnt"));
        hudFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        hudFont.getData().setScale(0.5f);
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
        drawHUD();

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
        hudFont.dispose();
    }

    private void updateStates(float delta) {
        // atualização do jogo
        player.update(delta);
        Laser newLaser = player.shoot();
        if(newLaser != null)
            laserManager.addLaser(newLaser);

        laserManager.update(delta);
        aliensFleet.update(delta, bombManager.getBombs());
        if(aliensFleet.getAliens().size == 0) {
            // acabou o nível
            if(currentLevel == 3) {
                // checa se o jogo acabou
                winGame();
                return;
            }
            passLevel();
        }
        bombManager.update(delta);
        checkCollisions();
    }

    private void drawEntities() {
        player.draw(game.sprite);
        aliensFleet.draw(game.sprite);
        laserManager.draw(game.sprite);
        bombManager.draw(game.sprite);
    }


    // funcao criada para checar as colisoes e der um retorno sobre elas
    private void checkCollisions() {
        // lista as entidades da tela
        com.badlogic.gdx.utils.Array<Laser> lasers = laserManager.getLasers();
        com.badlogic.gdx.utils.Array<Alien> aliens = aliensFleet.getAliens();
        com.badlogic.gdx.utils.Array<Bomb> bombs = bombManager.getBombs();

        // Colisão 1: Player atingindo os Aliens
        for (int i = lasers.size - 1; i >= 0; i--) {
            Laser laser = lasers.get(i);
            boolean laserHit = false;

            for (int j = aliens.size -1; j >= 0; j--) {
                Alien alien = aliens.get(j);

                // Método AABB para colisões
                if (laser.getHitbox().overlaps(alien.getHitbox())) {
                    // Remove o alien e marca que o laser bateu

                    // OPCIONAL: ADICIONAR UM SOM 

                    aliens.removeIndex(j);
                    laserHit = true;
                    break; // laser bateu, logo nao precisa checar outros aliens
                }
            }

            if (laserHit) {
                lasers.removeIndex(i);
            }
        }

        // Colisao 2: Bomba atinge player
        for (int i = bombs.size -1; i >= 0; i--) {
            Bomb bomb = bombs.get(i);

            if(bomb.getHitbox().overlaps(player.getHitbox())) {
                bombs.removeIndex(i);
                
                // DECIDIR O QUE VAI FAZER QUANDO PLAYER TOMAR DANO

                System.out.println("Jogador foi Atingido");
            }
        }

        // Colisao 3: Alien atinge player
        for (int i = aliens.size - 1; i >= 0; i--) {
            Alien alien = aliens.get(i);

            if(alien.getHitbox().overlaps(player.getHitbox())) {
                // ADICIONAR ALGO PARA QUANDO O PLAYER EH ATINGIDO POR UMA NAVE

                System.out.println("ALIEN INVADIU A NAVE!");
            }

        }
    }

    private void passLevel() {
        game.setScreen(new LevelCompleteScreen(game, currentLevel));
    }

    private void winGame() {
        // TODO: Desenhar tela de game win
        return;
    }

    private void drawHUD() {
        int[] status = player.getStatus();
        int currentPoints = status[0];
        int currentLives = status[1];
        hudFont.draw(game.sprite, "SCORE: " + currentPoints, 20, GAME_HEIGHT - 20);
        hudFont.draw(game.sprite, "LIVES: " + currentLives, GAME_WIDTH - 150, GAME_HEIGHT - 20);

    }
}