package com.projectoop.javainvaders;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

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

    public void update(float delta) {
        hitbox.y -= BOMB_SPEED * delta;
    }

    public void draw(SpriteBatch batch, TextureRegion currentFrame) {
        batch.draw(currentFrame, hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    public Rectangle getHitbox() {
        return hitbox;
    }
}
