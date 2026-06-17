package com.projectoop.javainvaders;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Laser {
    private Rectangle hitbox;
    private static final float LASER_SPEED = 350f;

    public Laser(float startX, float startY) {
        hitbox = new Rectangle();
        hitbox.width = 16;
        hitbox.height = 32;
        hitbox.x = startX;
        hitbox.y = startY;
    }

    /**
     * Atualiza a posição do laser. Ele só se move para cima.
     * @param delta -  mesmo delta que o do Player
     */
    public void update(float delta) {
        hitbox.y += LASER_SPEED * delta;
    }

    /**
     * Desenha a si mesmo
     * @param batch - recebido pelo GameScreen
     * @param laserTexture - recebido pelo GameScreen, não instancia diversas Textures para melhorar o uso de RAM
     */
    public void draw(SpriteBatch batch, Texture laserTexture) {
        batch.draw(laserTexture, hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    public Rectangle getHitbox() {
        return hitbox;
    }
}
