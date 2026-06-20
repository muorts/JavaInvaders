package com.projectoop.javainvaders;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Explosion {
    private float x, y;         // coordenadas do alien que morreu
    private float stateTime;

    private static final float WIDTH = 48;
    private static final float HEIGHT = 48;

    public Explosion(float x, float y) {
        this.x = x;
        this.y = y;
        this.stateTime = 0f;        // inicia o tempo de animação no 0
    }

    public void update(float delta) {
        stateTime += delta;
    }

    public void draw(SpriteBatch batch, Animation<TextureRegion> animation) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, false);       // false nao permite a animação reiniciar
        batch.draw(currentFrame, x, y, WIDTH, HEIGHT);
    }

    public boolean isFinished(Animation<TextureRegion> animation) {
        return animation.isAnimationFinished(stateTime);
    }
}
