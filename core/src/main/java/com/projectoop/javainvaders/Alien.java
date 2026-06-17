package com.projectoop.javainvaders;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Alien {
    private Rectangle hitbox;
    
    public Alien(float startX, float startY) {
        hitbox = new Rectangle();
        hitbox.x = startX;
        hitbox.y = startY;
        hitbox.width = 22;
        hitbox.height = 32;
    }

    /**
     * Método que faz a atualização da posição de um alien, quem decide o movimento é a frota em GameScreen
     * @param dx - delta x
     * @param dy - delta y
     */
    public void update(float dx, float dy) {
        hitbox.x += dx;
        hitbox.y += dy;
    }

    public void draw(SpriteBatch batch, TextureRegion currentFrame) {
        batch.draw(currentFrame, hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    
}
