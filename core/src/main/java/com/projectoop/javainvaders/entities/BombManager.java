package com.projectoop.javainvaders.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Classe responsável por gerenciar as bombas. A sua existência é justificada pela otimização
 * de hardware, mantendo o FPS alto. É aqui que se define a inicialização das bombas, emprestando
 * a textura carregada na memória para cada uma se desenhar.
 */
public class BombManager {
    // array de bombas "vivas"
    private Array<Bomb> bombs;

    // atributos de desenho e design
    private Texture bombSpriteSheet;
    private Animation<TextureRegion> bombAnimation;
    private float bombStateTime;

    public BombManager() {
        initBombs();
    }

    /**
     * Método que inicializa a textura das bombas e deixa pronta para cada uma utilizar
     * e se desenhar, evitando que ocorra um carregamento desnecessário na memória.
     */
    private void initBombs() {
        bombs = new Array<Bomb>();

        bombSpriteSheet = new Texture(Gdx.files.internal("Bombs_Spritesheet.png"));
        bombSpriteSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        int frameWidth = 32;
        int frameHeight = 32;

        TextureRegion[][] tmp = TextureRegion.split(bombSpriteSheet, frameWidth, frameHeight);
        TextureRegion [] frames = new TextureRegion[4];

        int idx = 0;
        for(int i=0; i<1; i++) {
            for(int j=0; j<4; j++) {        // 4colunas
                frames[idx++] = tmp[i][j];
            }
        }

        bombAnimation = new Animation<TextureRegion>(0.1f, frames);
        bombStateTime = 0f;
    }

    /**
     * Método que verifica os limites da tela e move a bomba para baixo.
     * @param delta - usada para controle dos timers de movimento
     */
    public void update(float delta) {
        bombStateTime += delta;

        for(int i = bombs.size-1; i>=0; i--) {
            Bomb bomb = bombs.get(i);
            bomb.update(delta);

            //checa o limite da tela
            if(bomb.getHitbox().y +bomb.getHitbox().height < 0) 
                bombs.removeIndex(i);
        }
    }

    /**
     * Faz o desenho de cada bomba viva, delegando a entidade Bomb seu desenho próprio
     * @param batch game sprite já inicializado
     */ 
    public void draw(SpriteBatch batch) {
        TextureRegion currentBombFrame = bombAnimation.getKeyFrame(bombStateTime, true);
        for(Bomb bomb: bombs)
            bomb.draw(batch, currentBombFrame);
    }

    /**
     * Método para liberar a memória da frota como um todo
     */
    public void dispose() {
        bombSpriteSheet.dispose();
    }

    /**
     * Método que retorna o vetor de bombas inicializadas
     * @return  vetor de bombas
     */
    public Array<Bomb> getBombs() {
        return bombs;
    }
}
