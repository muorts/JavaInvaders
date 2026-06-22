package com.projectoop.javainvaders.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * Classe que representa uma única bomba. Responsável por definir o movimento
 * e o tamanho das bombas no jogo, delegando a sua existência ao BombManager. 
 * Responsável, também, pelo desenho próprio de uma única bomba.
 */
public class Bomb {
    private Rectangle hitbox;
    private static final float BOMB_SPEED = 250f;

    public Bomb(float startX, float startY) {
        hitbox = new Rectangle();
        hitbox.x = startX;
        hitbox.y = startY;
        hitbox.width = 32;
        hitbox.height = 32;
    }

    /**
     * Método que movimenta a bomba para baixo
     * @param delta - controla o movimento da bomba em relação ao tempo
     */
    public void update(float delta) {
        hitbox.y -= BOMB_SPEED * delta;
    }

/**
     * Método para desenhar uma única bomba
     * @param batch - SpriteBatch enviado pela própria GameScreen para desenhar na tela
     */
    public void draw(SpriteBatch batch, TextureRegion currentFrame) {
        batch.draw(currentFrame, hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    /**
     * Método para retornar os atributos espaciais da bomba
     * @return um retângulo contendo a hitbox da bomba
     */
    public Rectangle getHitbox() {
        return hitbox;
    }
}
