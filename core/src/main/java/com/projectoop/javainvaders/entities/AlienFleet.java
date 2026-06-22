package com.projectoop.javainvaders.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Classe responsável por gerenciar a horda de aliens. A sua existência é justificada pela otimização
 * de hardware, mantendo o FPS alto. É aqui que se define o movimento dos aliens e o intervalo de tiro.
 * Gerencia, também, o desenho dos aliens. Caso queira aumentar a dificuldade dos níveis, ou aumentar um 
 * nível de dificuldade, faça a modificação dos intervalos aqui.
 */
public class AlienFleet {
    private Array<Alien> aliensAlive;        // lista que contem os aliens vivos

    // Variáveis de desenho dos aliens - MANTENHA ESSA LÓGICA AQUI PARA MELHOR USO DE MEMÓRIA
    private Texture alienSpriteSheet;
    private Animation<TextureRegion> alienAnimation;
    private float alienStateTime;

    private float alienMoveTimer;
    private float alienMoveInterval = 0.5f; // Os aliens se movem a cada 0.5s
    private int alienDirection = 1;     // 1 = direita / -1 = esquerda
    private static final float ALIEN_STEP_X = 20f;  // Pixels para andar horizontalmente
    private static final float ALIEN_STEP_Y = 25f;  // Pixels descidos ao bater na borda

    private float alienShootTimer;
    private float ALIEN_SHOOT_INTERVAL = 0.8f;      // velocidade inicial do tiro dos alienígenas

    private static final float GAME_WIDTH = 800;
    private static final float GAME_HEIGHT = 600;

    public AlienFleet(int currentLevel) {
        initFleet(currentLevel);
    }

    /**
     * Método que desenha a frota de aliens na parte superior da tela e inicia a frota por completo
     */
    private void initFleet(int currentLevel) {
        aliensAlive = new Array<Alien>();

        // carrega a textura na memória
        alienSpriteSheet = new Texture(Gdx.files.internal("Alien_Spritesheet.png"));
        alienSpriteSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        int frameWidth = alienSpriteSheet.getWidth() / 3;       // 3 colunas
        int frameHeight = alienSpriteSheet.getHeight();         // 1 linha

        TextureRegion[][] tmp = TextureRegion.split(alienSpriteSheet, frameWidth, frameHeight);
        TextureRegion[] frames = new TextureRegion[3];
        int idx = 0;
        for(int i = 0; i < 1; i++) {    // 1 linha
            for(int j = 0; j < 3; j++) {    // 3 colunas
                frames[idx++] = tmp[i][j];
            }
        }

        // inicializa a animação
        alienAnimation = new Animation<TextureRegion>(alienMoveInterval, frames);
        alienStateTime = 0f;
        alienShootTimer = 0f;

        // posicionamento da matriz dos aliens
        int rows = 0;
        int cols = 0;
        if(currentLevel == 1){
            // não há alteração na velocidade dos tiros nem no movimento
            rows = 5;
            cols = 9;
        }
        else if(currentLevel == 2) {
            rows = 5;
            cols = 10;
            ALIEN_SHOOT_INTERVAL = 0.6f;
            alienMoveInterval = 0.35f;
        }
        else {
            rows = 5;
            cols = 11;
            ALIEN_SHOOT_INTERVAL = 0.4f;
            alienMoveInterval = 0.25f;
        }

        // posicionamento inicial dos aliens
        float startX = 100f;    // margem inicial esquerda
        float startY = GAME_HEIGHT - 100f; // posição da linha mais alta
        float spacingX = 50f;   // distância horizontal entre os aliens
        float spacingY = 50f;   // distância vertical entre os aliens

        for(int row=0; row<rows; row++) {
            for(int col=0; col<cols; col++) {
                float x = startX + (col * spacingX);
                float y = startY - (row * spacingY);

                aliensAlive.add(new Alien(x, y));
            }
        }
    }

    /**
     * Método que verifica os limites da tela e controla a descida.
     * Para aumentar a velocidade dos aliens, faça aqui.
     * @param delta - usada para controle dos timers de movimento
     */
    public void update(float delta, Array<Bomb> activeBombs) {
        alienStateTime += delta;
        alienMoveTimer += delta;
        alienShootTimer += delta;

        if(alienMoveTimer >= alienMoveInterval) {
            alienMoveTimer = 0f;    // reseta o cronômetro
            boolean hitEdge = false;

            // checagem das bordas
            for (Alien alien: aliensAlive) {
                if (alienDirection == 1 && alien.getHitbox().x + alien.getHitbox().width >= GAME_WIDTH) {
                    hitEdge = true;
                    break;
                } else if (alienDirection == -1 && alien.getHitbox().x <= 0) {
                    hitEdge = true;
                    break;
                }
            }

            // aplica o movimento na frota inteira
            if (hitEdge) {
                alienDirection *= -1;   // inverte o eixo horizontal

                for(Alien alien : aliensAlive) 
                    alien.update(0, -ALIEN_STEP_Y);
            } else {
                for(Alien alien : aliensAlive)
                    alien.update(ALIEN_STEP_X * alienDirection, 0);
            }
        }

        // Checa a lógica de atirar
        shoot(activeBombs);
    }

    /**
     * Método que realiza o tiro dos aliens. Utiliza uma lógica de aleatoriedade no tiro, ou seja,
     * qualquer alien vivo consegue atirar.
     * @param activeBombs array de bombas que já estão no jogo(pode estar vazio)
     */
    private void shoot(Array<Bomb> activeBombs) {
        if(alienShootTimer >= ALIEN_SHOOT_INTERVAL) {
            alienShootTimer = 0f;       // reseta o cronômetro

            if(aliensAlive.size > 0) {
                int randomIndex = com.badlogic.gdx.math.MathUtils.random(0, aliensAlive.size - 1);
                Alien shooter = aliensAlive.get(randomIndex);       // seleciona um alien para atirar

                float bombX = shooter.getHitbox().x + (shooter.getHitbox().width / 2f) - 16f;       // subtrai 16 porque é metade da bomba
                float bombY = shooter.getHitbox().y;

                activeBombs.add(new Bomb(bombX, bombY));        // adiciona a bomba no vetor 
            }
        }
    }

    /**
     * Faz o desenho da frota. Delega a responsabilidade de "se desenhar" para cada alien,
     * fazendo, apenas, um loop no qual cada alien se desenha na posição correta.
     * @param batch game sprite já inicializado
     */
    public void draw(SpriteBatch batch) {
        TextureRegion currentAlienFrame = alienAnimation.getKeyFrame(alienStateTime, true);
        for (Alien alien : aliensAlive) 
            alien.draw(batch, currentAlienFrame);
    }

    /**
     * Método para liberar a memória da frota como um todo
     */
    public void dispose() {
        alienSpriteSheet.dispose();
    }

    /**
     * Método que retorna o vetor de aliens vivos no jogo
     * @return  vetor de aliens vivos
     */
    public Array<Alien> getAliens() {
        return aliensAlive;
    }
}
