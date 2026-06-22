package com.projectoop.javainvaders.screens;

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
import com.projectoop.javainvaders.entities.Alien;
import com.projectoop.javainvaders.entities.AlienFleet;
import com.projectoop.javainvaders.entities.Bomb;
import com.projectoop.javainvaders.entities.BombManager;
import com.projectoop.javainvaders.entities.ExplosionManager;
import com.projectoop.javainvaders.entities.JavaInvadersGame;
import com.projectoop.javainvaders.entities.Laser;
import com.projectoop.javainvaders.entities.LaserManager;
import com.projectoop.javainvaders.entities.Player;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;




/**
 * Classe que coordena a tela de jogo. Ela é responsável por renderizar os níveis, as entidades e 
 * suas texturas. Contém a lógica de colisão e checagem de vitória, de nível e global, e de derrota.
 * Delega o carregamento das texturas das entidades para os seus gerenciadores para reduzir o consumo 
 * de hardware a manter o FPS alto.
 */
public class GameScreen implements Screen {
    // variáveis de jogo
    final JavaInvadersGame game;
    final Player player;      
    private AlienFleet aliensFleet;         // gerenciador da horda de aliens
    private LaserManager laserManager;      // gerenciador dos lasers em tela
    private BombManager bombManager;        // gerenciador das bombas em tela
    private int currentLevel = 1;       // o jogo inicia no primeiro nível
    private ExplosionManager explosionManager;

    // variáveis para renderização do design do nível
    private OrthographicCamera camera;
    private Viewport view;
    private Stage stage;       
    private Texture backgroundTexture;
    private BitmapFont hudFont;

    // variáveis de som do nível
    private Sound exploseSound;
    private Sound damageSound;
    private Sound bigExploseSound;

    // variaveis que definem o tamanho da tela do jogo
    private static final float GAME_WIDTH = 800;
    private static final float GAME_HEIGHT = 600;


    public GameScreen(JavaInvadersGame game, int level, int previousPoints) {
        currentLevel = level;
        this.game = game;
        this.player = new Player();
        player.addPoints(previousPoints);       // garante que o jogador irá começar com 0 pontos ou com os pontos das outras fases

        this.aliensFleet = new AlienFleet(level);
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

    /**
     * Método chamado quando essa tela está com o foco.
     * Método oposto ao hide().
     */
    @Override
    public void show() {
        // ScreenViewport se adapta à tela
        stage = new Stage(new FitViewport(GAME_WIDTH, GAME_HEIGHT));
        
        // carregamento da tela de fundo
        backgroundTexture = new Texture(Gdx.files.internal("GameBackground.png"));
        Image backgroundImage = new Image(backgroundTexture);

        // organiza o layout
        Table table = new Table();
        table.setFillParent(true);  // ocupa a tela inteira
        backgroundImage.setFillParent(true);

        stage.addActor(backgroundImage);
        stage.addActor(table);
    }

    /**
     * Realiza a renderização do jogo por completo. 
     * Método chamado naturalmente pela LibGDX.
     */
    @Override
    public void render(float delta) {
        // checagem de pause do game
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new PauseScreen(game, this));    // Chama a tela de Pause passando o jogo principal E a tela atual (this)
            return;         // interrompe o render deste frame
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);        // pinta a tela de preto
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

        // colocar todas as funções de desenho dentro desse intervalo
        drawEntities();
        drawHUD();

        game.sprite.end();
    }

    @Override
    public void resize(int width, int height) {
        // método para redefinir o tamanho da imagem
        if(width <= 0 || height <= 0) return;

        view.update(width, height, true);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // método a ser utilizado quando a tela é tirada de foco(ex: quando minimizar a tela)
    }

    @Override
    public void resume() {
        // método a ser utilizado quando a tela retorna ao foco
    }

    @Override
    public void hide() {
        // método chamado naturalmente quando outra tela substitui essa. 
    }

    @Override
    public void dispose() {
        // método para liberar as texturas carregadas da memória
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

    /**
     * Método para atualizar os estados e ações do jogo.
     * Atualiza as posições de todas as entidades e chama a checagem de colisões
     * @param delta - é o tempo que passou entre a última vez que a tela foi desenhada e o instante presente, 
     * tem que se manter na função para que a velocidade do jogo se mantenha constante, independente do hardware do PC
     */
    private void updateStates(float delta) {
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

    /**
     * Chama o método de desenho de cada uma das 
     * entidades para elas "se desenharem" na tela
     */
    private void drawEntities() {
        player.draw(game.sprite);
        aliensFleet.draw(game.sprite);
        laserManager.draw(game.sprite);
        bombManager.draw(game.sprite);
        explosionManager.draw(game.sprite);
    }


    /**
     * Método que realiza a verificação das colisões utilizando o método AABB.
     * Para cada um dos cenários, a colisão tem uma consequência diferente.
     */
    private void checkCollisions() {
        // lista as entidades da tela
        Array<Laser> lasers = laserManager.getLasers();
        Array<Alien> aliens = aliensFleet.getAliens();
        Array<Bomb> bombs = bombManager.getBombs();

        // Colisão 1: Player atingindo os Aliens
        for (int i = lasers.size - 1; i >= 0; i--) {
            Laser laser = lasers.get(i);
            boolean laserHit = false;

            for (int j = aliens.size -1; j >= 0; j--) {
                Alien alien = aliens.get(j);

                // Método AABB para colisões
                if (laser.getHitbox().overlaps(alien.getHitbox())) {
                    // Pega o centro do alien e recua metade do tamanho da explosão (24px)
                    float expX = alien.getHitbox().x + (alien.getHitbox().width / 2f) - (48f / 2f);
                    float expY = alien.getHitbox().y + (alien.getHitbox().height / 2f) - (48f / 2f);
                    explosionManager.addExplosion(expX, expY);

                    exploseSound.play(0.5f);

                    // Remove o alien e marca que o laser bateu
                    aliens.removeIndex(j);
                    laserHit = true;
                    player.addPoints(50);           // mude os pontos do player aqui        
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
                // remove a bomba do array de bombas do jogo
                bombs.removeIndex(i);
                
                // pega o centro da bomba e recua metade do tamanho da explosão para desenhar em uma posição mais correta
                float expX = player.getHitbox().x + (player.getHitbox().width / 2f) - (48f / 2f);
                float expY = player.getHitbox().y + (player.getHitbox().height / 2f) - (48f / 2f);
                explosionManager.addExplosion(expX, expY);

                damageSound.play(3f);

                System.out.println("Jogador foi Atingido");
                player.takeDamage();            // player perde uma vida
            }
        }

        // Colisao 3: Alien atinge player
        for (int i = aliens.size - 1; i >= 0; i--) {
            // colisão mais catastrófica: se o alien encostar no player ou passar da linha dele, o jogo acaba
            Alien alien = aliens.get(i);

            if(alien.getHitbox().overlaps(player.getHitbox()) || alien.getHitbox().y <= 0) {

                bigExploseSound.play(0.5f);

                System.out.println("ALIEN INVADIU A NAVE!");
                gameLoose();
                return;
            }

        }
    }

    /**
     * Método a ser chamado quando o jogador destroi todos os aliens de certo nível
     * Substitui a tela atual pela tela de que passou de nível.
     * É necessário passar os pontos do jogador para que não reiniciem de acordo com a fase
     */
    private void passLevel() {
        game.setScreen(new LevelCompleteScreen(game, currentLevel, player.getPoints()));
    }

    /**
     * Método a ser chamado quando o jogador ganha o jogo.
     * Substitui a tela atual pela tela de jogo ganho.
     */
    private void winGame() {
        System.out.println("GANHOU O JOGO");
        System.out.println("Pontuação total: " + player.getPoints());
        game.backgroundMusic.dispose();         // necessário para a música de gameplay não continuar tocando
        game.setScreen(new GameWinScreen(game, player.getPoints()));
        dispose();
    }

    /**
     * Método a ser chamado quando o jogador perde o jogo.
     * Substitui a tela atual pela tela de Game Loose.
     */
    private void gameLoose() {
        System.out.println("PERDEU O JOGO");
        System.out.println("Pontuação total: " + player.getPoints());
        game.backgroundMusic.dispose();         // necessário para a música de gameplay não continuar tocando
        game.setScreen(new GameOverScreen(game, player.getPoints()));
        dispose();
    }

    /**
     * Método que desenha o HUD do nível.
     */
    private void drawHUD() {
        int[] status = player.getStatus();
        int playerPoints = status[0];
        int playerLives = status[1];
        hudFont.draw(game.sprite, "SCORE: " + playerPoints, 20, GAME_HEIGHT - 20);
        hudFont.draw(game.sprite, "LIVES: " + playerLives, GAME_WIDTH - 150, GAME_HEIGHT - 20);

    }

    /**
     * Método que checa se o player morreu ou se matou todos os aliens
     */
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

    /**
     * Método que inicializa as variáveis de som da fase. 
     * Apenas inicializa os sons momentâneos, delegando a música de fundo
     * para a classe global do jogo.
     */
    private void initGameSound() {
        exploseSound = Gdx.audio.newSound(Gdx.files.internal("kill.mp3"));
        damageSound = Gdx.audio.newSound(Gdx.files.internal("damage.mp3"));
        bigExploseSound = Gdx.audio.newSound(Gdx.files.internal("big_explose.mp3"));
    }
}