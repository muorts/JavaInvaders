package com.projectoop.javainvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class BombManager {
    private Array<Bomb> bombs;
    private Texture bombSpriteSheet;
    private Animation<TextureRegion> bombAnimation;
    private float bombStateTime;

    public BombManager() {
        initBombs();
    }

    private void initBombs() {
        bombs = new com.badlogic.gdx.utils.Array<Bomb>();

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

    public void draw(SpriteBatch batch) {
        TextureRegion currentBombFrame = bombAnimation.getKeyFrame(bombStateTime, true);
        for(Bomb bomb: bombs)
            bomb.draw(batch, currentBombFrame);
    }

    public void dispose() {
        bombSpriteSheet.dispose();
    }

    public Array<Bomb> getBombs() {
        return bombs;
    }
}
