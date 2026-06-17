package com.projectoop.javainvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class LaserManager {
    private Array<Laser> activateLasers;
    private Texture laserTexture;
    private static final float GAME_HEIGHT = 600;

    public LaserManager() {
        activateLasers = new Array<Laser>();
        laserTexture = new Texture(Gdx.files.internal("Laser.png"));
    }

    public void addLaser(Laser laser) {
        activateLasers.add(laser);
    }

    public void update(float delta) {
        // atualiza, desenha e gerencia a memória dos lasers
        for(int i = activateLasers.size - 1; i>=0; i--) {
            Laser laser = activateLasers.get(i);
            laser.update(delta);

            // TODO: quando implementar os aliens, deve colocar a condição de se bateu em um alien
            if(laser.getHitbox().y > GAME_HEIGHT) {
                activateLasers.removeIndex(i);
            }
        }
    }

    public void draw(SpriteBatch batch) {
        for(Laser laser:activateLasers) {
            laser.draw(batch, laserTexture);
        }
    }

    public void dispose() {
        laserTexture.dispose();
    }

    public Array<Laser> getLasers() {
        return activateLasers;
    }
}
