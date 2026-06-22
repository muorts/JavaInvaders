package com.projectoop.javainvaders.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.audio.Sound;


/**
 * Classe que representa o jogador principal. É responsável por tudo relacionado
 * ao player, desde o seu desenho e animações, até a lógica de vida e morte.
 */
public class Player {
    // atributos de desenho
    private Texture flyingSpriteSheet;
    private Animation<TextureRegion> flyingAnimation;
    private Texture shootingSpriteSheet;
    private Animation<TextureRegion> shootingAnimation;
    private Sound shootSound;
    private float stateTime;        // acumulador para o tempo de animação

    // atributos de hitbox
    private Rectangle visualBox;        // caixa que mantem a arte do jogador
    private Rectangle collisionBox;     // hitbox real do jogador

    // atributos de jogo
    private int points = 0;         // pontos iniciais
    private int lives = 3;          // jogador inicia com 3 vidas
    private static final float SHIP_SPEED = 300f;   // 300 pixels por segundo
    private float shootTimer;   // cronômetro para atirar
    private static final float SHOOT_COOLDOWN = 0.72f;
    private boolean laserSpawnedThisShot = true;

    // atributo da tela do jogo
    private static final float GAME_WIDTH = 800;

    public Player() {
        initFlyingAnimation();
        initShootAnimation();
        initPlayerHitbox();
        initPlayerPosition();
        shootSound = Gdx.audio.newSound(Gdx.files.internal("shot.mp3"));
    }

    /**
     * Método que inicializa a arte da nave do player. Inicializa o sprite, fazendo com que tenha a animação
     * Utiliza uma matriz 2D temporaria para o desenho da nave e depois transforma em um vetor 
     * porque a classe Animation exige que seja um vetor de 1 dimensão.
     */
    private void initFlyingAnimation() {
        flyingSpriteSheet = new Texture(Gdx.files.internal("Spaceship_flying_spritesheet.png"));
        flyingSpriteSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);    // mantém o desenho da nave nítido

        // O Sprite Sheet da nave voando tem 2 colunas e 1 linha
        int frameWidth = flyingSpriteSheet.getWidth() / 2;
        int frameHeight = flyingSpriteSheet.getHeight();

        TextureRegion[][] tmp = TextureRegion.split(flyingSpriteSheet, frameWidth, frameHeight);      // fatia automatico em uma matriz 2D
        // Transforma a matriz 2D em um vetor 1D por conta da classe Animation
        TextureRegion[] frames = new TextureRegion[2];  // 2 frames no total

        int idx = 0;
        for(int i = 0; i < 1; i++) {    // 1 linha
            for(int j = 0; j < 2; j++) {    // 2 colunas
                frames[idx++] = tmp[i][j];
            }
        }

        flyingAnimation = new Animation<TextureRegion>(0.12f, frames);    //0.12f é a duração de cada frame, NAO MEXA
        stateTime = 0f;
    }

    /**
     * Método para desenhar a animação de nave atirando. Utiliza a mesma lógica de desenho
     * que os outros métodos.
     */
    private void initShootAnimation() {
        shootingSpriteSheet = new Texture(Gdx.files.internal("Spaceship_shooting_spritesheet.png"));
        shootingSpriteSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);    // mantém o desenho da nave nítido

        // O Sprite Sheet da nave voando tem 6 colunas e 1 linha
        int frameWidth = shootingSpriteSheet.getWidth() / 6;
        int frameHeight = shootingSpriteSheet.getHeight();

        TextureRegion[][] tmp = TextureRegion.split(shootingSpriteSheet, frameWidth, frameHeight);      // fatia automatico em uma matriz 2D
        // Transforma a matriz 2D em um vetor 1D por conta da classe Animation
        TextureRegion[] frames = new TextureRegion[6];  // 6 frames no total

        int idx = 0;
        for(int i = 0; i < 1; i++) {    // 1 linha
            for(int j = 0; j < 6; j++) {    // 6 colunas
                frames[idx++] = tmp[i][j];
            }
        }

        shootingAnimation = new Animation<TextureRegion>(0.12f, frames);    // 0.12f é a duração de cada frame, NAO MEXA
        stateTime = 0f;
    }

    /**
     * Método para inicializar a hitbox do player.
     * O jogador é essencialmente um retângulo.
     * A separação de hitbox ocorreu para não atrapalhar a imagem do player e manter 
     * uma colisão fiel.
     * OBS: para aumentar o tamanho da nave, aumente visualBox.width e visualBox.height aqui
     */
    private void initPlayerHitbox() {
        // Hitbox visual do player
        visualBox = new Rectangle();
        visualBox.width = 36;
        visualBox.height = 128;

        // Hitbox real do player
        collisionBox = new Rectangle();
        collisionBox.width = 18;        // largura real da imagem   
        collisionBox.height = 64;       // altura real da imagem
    }

    /**
     * Método utilizado para inicializar o player na posição inicial do jogo.
     * Player sempre fica na parte inferior da tela, iniciando no centro
     */
    private void initPlayerPosition() {
        // posiciona o player no centro inferior da tela
        visualBox.x = (GAME_WIDTH / 2f) - (visualBox.width / 2f);
        visualBox.y = 20;    // 20 pixels acima da borda inferior
        collisionBox.x = visualBox.x;
        collisionBox.y = visualBox.y;
    }

    /**
     * Método que atualiza o a posição de um jogador com base no movimento dele
     * @param delta - é o tempo que passou entre a última vez que a tela foi desenhada e o instante presente, 
     * tem que se manter na função para que a velocidade do jogo se mantenha constante, independente do hardware do PC
     */
    public void update(float delta) {
        if (shootTimer > 0)
            shootTimer -= delta;
        
        // Multiplicar por 'delta' garante que a nave ande a 300pix/s independente do FPS
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            visualBox.x -= SHIP_SPEED * delta;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            visualBox.x += SHIP_SPEED * delta;
        }
        // Garante os limites da tela
        if (visualBox.x < 0)
            visualBox.x = 0;
        else if (visualBox.x > GAME_WIDTH - visualBox.width)
            visualBox.x = GAME_WIDTH - visualBox.width;

        // mantém a hitbox real sincronizada com a visual
        collisionBox.x = visualBox.x;
        collisionBox.y = visualBox.y;
        stateTime += delta;
    }

    /**
     * Adiciona uma certa quantidade de pontos ao jogador
     * @param x     pontos a serem adicionados
     */
    public void addPoints(int x) {
        points += (x * lives); // ganha x pontos por alien destruido proporcionalmente às vidas do player
    }

    /**
     * Método usado para pegar apenas os pontos atuais do player
     * @return os pontos do player no instante que o método é chamado
     */
    public int getPoints() {
        return points;
    }

    /**
     * Método que atualiza as vidas do player rapidamente.
     */
    public void takeDamage() {
        lives--;
    }

    /**
     * Método usado para pegar apenas as vidas do player
     * @return as vidas do player no instante que o método é chamado
     */
    public int getLives() {
        return lives;
    }
    

    /**
     *  Método que atira um laser do centro da nave se for apertado espaço e o cronometro estiver no 0.
     *  Faz uma verificação mais complexa para coordenar a animação de tiro com a saída do laser de verdade.
     * @return - um novo Laser com as coordenadas do meio do topo da nave ou nulo se não obedecer uma das condições
     */
    public Laser shoot() {
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && shootTimer <= 0) {
            // inicia o processo de "carregar a arma"
            shootTimer = SHOOT_COOLDOWN;    // reinicia o cronômetro
            stateTime = 0f;
            laserSpawnedThisShot = false;
        }

        if(shootTimer > 0 && !laserSpawnedThisShot && stateTime >= 0.70f) { // 60f está no 6° frame da animação de tiro
            // sincroniza a animação de tiro com o laser propriamente dito
            laserSpawnedThisShot = true;
            float laserX = visualBox.x + (visualBox.width / 2f) - 10f;
            float laserY = visualBox.y + visualBox.height;

            shootSound.play(0.4f);

            return new Laser(laserX, laserY);
        }

        return null;
    }


    /**
     * Método para desenhar a nave do jogador
     * @param batch - SpriteBatch enviado pela própria GameScreen para desenhar na tela
     */
    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame;

        // se o cronometro estiver rodando, significa que a nave está atirando
        if(shootTimer>0) {
            currentFrame = shootingAnimation.getKeyFrame(stateTime, true);
        } else {
            currentFrame = flyingAnimation.getKeyFrame(stateTime, true);
        }

        batch.draw(currentFrame, visualBox.x, visualBox.y, visualBox.width, visualBox.height);
    }

    
    /**
     * Método para limpar a memória
     */
    public void dispose() {
        flyingSpriteSheet.dispose();
        shootingSpriteSheet.dispose();
        shootSound.dispose();
    }

    /**
     * Método para pegar as coordenadas do jogador
     * @return - hitbox do jogador, ou seja, as coordenadas (x, y) dele
     */
    public Rectangle getHitbox() {
        return collisionBox;
    }

    /**
     * Retorna o status atual do player
     * @return um par(pontos, vidas) nessa ordem
     */
    public int[] getStatus() {
        int[] status = {points, lives};
        return status;
    }
}
