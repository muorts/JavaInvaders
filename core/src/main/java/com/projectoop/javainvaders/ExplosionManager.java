package com.projectoop.javainvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;


public class ExplosionManager {
    private Array<Explosion> explosions;            // lista com as explosoes que estao acontecendo
    private Texture explosionSpriteSheet;
    private Animation<TextureRegion> explosionAnimation;

    public ExplosionManager() {
        explosions = new Array<Explosion>();
        initAnimation();
    }

    /**
     * Faz o carregamento da animação de explosão em memória
     */
    private void initAnimation() {
        explosionSpriteSheet = new Texture(Gdx.files.internal("Explosion_Spritesheet.png"));
        explosionSpriteSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        int frameWidth = 96;
        int frameHeight = 96;

        TextureRegion[][] tmp = TextureRegion.split(explosionSpriteSheet, frameWidth, frameHeight);
        TextureRegion[] frames = new TextureRegion[12];         // a imagem tem 12 frames

        int idx=0;
        for(int i=0; i<1; i++) {
            for(int j=0; j<12; j++) {
                frames[idx++] = tmp[i][j];
            }
        }

        explosionAnimation = new Animation<TextureRegion>(0.05f, frames);
    }

    /**
     * Inicia uma nova explosão e adiciona ela no vetor
     * @param x posição x do alien que acabou de morrer
     * @param y posição y do alien que acabou de morrer
     */
    public void addExplosion(float x, float y) {
        explosions.add(new Explosion(x, y));
    }


    /**
     * Realiza a verificação das bombas carregadas para ver se alguma já parou e pode ser liberada da memória
     * @param delta     mesmo delta que das outras classes
     */
    public void update(float delta) {
        // itera de tras pra frente para ter mais segurança na hora de remover as explosoes
        for(int i = explosions.size -1; i>=0; i--) {
            Explosion exp = explosions.get(i);

            exp.update(delta);

            if(exp.isFinished(explosionAnimation))
                explosions.removeIndex(i);

        }
    }

    /**
     * Desenha as explosoes na tela
     * @param batch   sprite a ser passado pelo jogo
     */
    public void draw(SpriteBatch batch) {
        for(Explosion exp : explosions) 
            exp.draw(batch, explosionAnimation);
    }

    /**
     * libera a memória
     */
    public void dispose() {
        explosionSpriteSheet.dispose();
    }
}
