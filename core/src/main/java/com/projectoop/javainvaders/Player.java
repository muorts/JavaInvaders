package com.projectoop.javainvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player {
    private Texture flyingSpriteSheet;
    private Animation<TextureRegion> flyingAnimation;
    private Texture shootingSpriteSheet;
    private Animation<TextureRegion> shootingAnimation;

    private float stateTime;        // acumulador para o tempo de animação
    private Rectangle hitbox;

    private static final float SHIP_SPEED = 300f;   // 300 pixels por segundo
    private float shootTimer;   // cronômetro para atirar
    private static final float SHOOT_COOLDOWN = 0.72f;

    private static final float GAME_WIDTH = 800;

    public Player() {
        initFlyingAnimation();
        initShootAnimation();
        initPlayerHitbox();
        initPlayerPosition();
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
     * OBS: para aumentar o tamanho da nave, aumente width e height aqui
     */
    private void initPlayerHitbox() {
        // Hitbox do player
        hitbox = new Rectangle();
        hitbox.width = 36;
        hitbox.height = 128;
    }

    /**
     * Método utilizado para inicializar o player na posição inicial do jogo.
     * Player sempre fica na parte inferior da tela, iniciando no centro
     */
    private void initPlayerPosition() {
        // posiciona o player no centro inferior da tela
        hitbox.x = (GAME_WIDTH / 2f) - (hitbox.width / 2f);
        hitbox.y = 20;    // 20 pixels acima da borda inferior
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
            hitbox.x -= SHIP_SPEED * delta;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            hitbox.x += SHIP_SPEED * delta;
        }
        // Garante os limites da tela
        if (hitbox.x < 0)
            hitbox.x = 0;
        else if (hitbox.x > GAME_WIDTH - hitbox.width)
            hitbox.x = GAME_WIDTH - hitbox.width;

        stateTime += delta;
    }

    /**
     *  Método que atira um laser do centro da nave se for apertado espaço e o cronometro estiver no 0
     * @return - um novo Laser com as coordenadas do meio do topo da nave ou nulo se não obedecer uma das condições
     */
    public Laser shoot() {
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE) && shootTimer <= 0) {
            shootTimer = SHOOT_COOLDOWN;    // reinicia o cronômetro

            float laserX = hitbox.x + (hitbox.width/2f) - 4.5f;
            float laserY = hitbox.height;

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

        batch.draw(currentFrame, hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    /**
     * Método para limpar a memória
     */
    public void dispose() {
        flyingSpriteSheet.dispose();
        shootingSpriteSheet.dispose();
    }

    /**
     * Método para pegar as coordenadas do jogador
     * @return - hitbox do jogador, ou seja, as coordenadas (x, y) dele
     */
    public Rectangle getHitbox() {
        return hitbox;
    }
}
