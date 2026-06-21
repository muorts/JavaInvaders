package com.projectoop.javainvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.audio.Sound;





public class GameScreen implements Screen {

    final JavaInvadersGame game;
    final Player player;      
    private AlienFleet aliensFleet;         // gerenciador da horda de aliens
    private LaserManager laserManager;      // gerenciador dos lasers em tela
    private BombManager bombManager;        // gerenciador das bombas em tela
    private int currentLevel = 1;       // o jogo inicia no primeiro nível
    private ExplosionManager explosionManager;

    private OrthographicCamera camera;
    private Viewport view;
    private Stage stage;       
    private Texture backgroundTexture;
    private BitmapFont hudFont;

    private Sound exploseSound;
    private Sound damageSound;
    private Sound bigExploseSound;

    // variaveis que definem o tamanho da tela do jogo
    private static final float GAME_WIDTH = 800;
    private static final float GAME_HEIGHT = 600;


    public GameScreen(JavaInvadersGame game, int Level, int previousPoints) {
        currentLevel = Level;
        this.game = game;
        this.player = new Player();
        player.addPoints(previousPoints);       // garante que o jogador irá começar com 0 pontos ou com os pontos das outras fases

        this.aliensFleet = new AlienFleet(Level);
        this.bombManager = new BombManager();
        this.laserManager = new LaserManager();
        this.explosionManager = new ExplosionManager();
        initGameSound();

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
        // checagem de pause do game
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new PauseScreen(game, this));    // Chama a tela de Pause passando o jogo principal E a tela atual (this)
            return;         // interrompe o render deste frame
        }

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
        explosionManager.dispose();
        exploseSound.dispose();
        damageSound.dispose();
        bigExploseSound.dispose();
    }

    private void updateStates(float delta) {
        // atualização do jogo

        // atualiza as posições e o tiro
        player.update(delta);
        Laser newLaser = player.shoot();
        if(newLaser != null)
            laserManager.addLaser(newLaser);

        laserManager.update(delta);
        aliensFleet.update(delta, bombManager.getBombs());
        checkWinOrLoose();

        // atualiza as bombas
        bombManager.update(delta);

        // atualiza as explosoes
        explosionManager.update(delta);
        
        // checa as colisões
        checkCollisions();
    }

    private void drawEntities() {
        player.draw(game.sprite);
        aliensFleet.draw(game.sprite);
        laserManager.draw(game.sprite);
        bombManager.draw(game.sprite);
        explosionManager.draw(game.sprite);
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

                    // Pega o centro do alien e recua metade do tamanho da explosão (24px)
                    float expX = alien.getHitbox().x + (alien.getHitbox().width / 2f) - (48f / 2f);
                    float expY = alien.getHitbox().y + (alien.getHitbox().height / 2f) - (48f / 2f);
                    explosionManager.addExplosion(expX, expY);

                    exploseSound.play(0.5f);

                    aliens.removeIndex(j);
                    laserHit = true;
                    player.addPoints(50);           // ganha 50 pontos por alien destruido
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
                
                float expX = player.getHitbox().x + (player.getHitbox().width / 2f) - (48f / 2f);
                float expY = player.getHitbox().y + (player.getHitbox().height / 2f) - (48f / 2f);
                explosionManager.addExplosion(expX, expY);

                damageSound.play(3f);

                System.out.println("Jogador foi Atingido");
                player.takeDamage();            // perde uma vida
            }
        }

        // Colisao 3: Alien atinge player
        for (int i = aliens.size - 1; i >= 0; i--) {
            Alien alien = aliens.get(i);

            if(alien.getHitbox().overlaps(player.getHitbox())) {

                bigExploseSound.play(0.5f);

                System.out.println("ALIEN INVADIU A NAVE!");
                gameLoose();
            }

        }
    }

    private void passLevel() {
        game.setScreen(new LevelCompleteScreen(game, currentLevel, player.getPoints()));
    }

    private void winGame() {
        System.out.println("GANHOU O JOGO");
        System.out.println("Pontuação total: " + player.getPoints());
        game.backgroundMusic.dispose();
        game.setScreen(new GameWinScreen(game, player.getPoints()));
        dispose();
    }

    private void gameLoose() {
        System.out.println("PERDEU O JOGO");
        System.out.println("Pontuação total: " + player.getPoints());
        game.backgroundMusic.dispose();
        game.setScreen(new GameOverScreen(game, player.getPoints()));
        dispose();
    }

    private void drawHUD() {
        int[] status = player.getStatus();
        int playerPoints = status[0];
        int playerLives = status[1];
        hudFont.draw(game.sprite, "SCORE: " + playerPoints, 20, GAME_HEIGHT - 20);
        hudFont.draw(game.sprite, "LIVES: " + playerLives, GAME_WIDTH - 150, GAME_HEIGHT - 20);

    }

    private void checkWinOrLoose() {
        // checa se matou todos os aliens
        if(aliensFleet.getAliens().size == 0) {
            // acabou o nível
            if(currentLevel == 3) {
                // checa se o jogo acabou
                winGame();
                return;
            }
            passLevel();
        }

        // Verifica se acabaram as vidas do player
        if (player.getLives() <= 0) {
            gameLoose();
        }
    }


    private void initGameSound() {
        exploseSound = Gdx.audio.newSound(Gdx.files.internal("kill.mp3"));
        damageSound = Gdx.audio.newSound(Gdx.files.internal("damage.mp3"));
        bigExploseSound = Gdx.audio.newSound(Gdx.files.internal("big_explose.mp3"));
    }
}