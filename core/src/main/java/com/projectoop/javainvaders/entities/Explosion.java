package com.projectoop.javainvaders.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Classe que representa as explosões ao longo do jogo. Sua existência é para 
 * manter uma boa utilização da memória, carregando a textura geral da explosão 
 * uma vez e emprestando ela para cada instância que ocorrer durante o jogo.
 */
public class Explosion {
    private float x, y;         // coordenadas do alien que morreu
    private float stateTime;

    // atributos de tamanho da bomba(reflete o tamanho da imagem(96x96))
    private static final float WIDTH = 48;
    private static final float HEIGHT = 48;

    public Explosion(float x, float y) {
        this.x = x;
        this.y = y;
        this.stateTime = 0f;        // inicia o tempo de animação no 0
    }

    /**
     * Atualiza o estado da bomba para ocorrer a animação
     * @param delta controla a animação com base no tempo relativo do jogo
     */
    public void update(float delta) {
        stateTime += delta;
    }

    /**
     * Desenha a animação da bomba
     * @param batch     Sprite fornecido pelo jogo
     * @param animation     Animação carregada pelo ExplosionManager
     */
    public void draw(SpriteBatch batch, Animation<TextureRegion> animation) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, false);       // false nao permite a animação reiniciar
        batch.draw(currentFrame, x, y, WIDTH, HEIGHT);
    }

    /**
     * Checa se a animação de uma bomba já finalizou
     * @param animation     Animação já carregada
     * @return      booleano indicando se a animação finalizou ou não
     */
    public boolean isFinished(Animation<TextureRegion> animation) {
        return animation.isAnimationFinished(stateTime);
    }
}
