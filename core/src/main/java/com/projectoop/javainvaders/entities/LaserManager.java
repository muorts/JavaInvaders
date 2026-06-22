package com.projectoop.javainvaders.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

/**
 * Classe responsável, unicamente, por gerenciar os lasers existentes no nível. 
 * Criado para melhorar a performance do jogo. Carrega a textura na memória e gerencia
 * o movimento, delegando a atualização e o desenho ao Laser.
 */
public class LaserManager {
    // array de lasers existentes no nível
    private Array<Laser> activateLasers;

    // atributos de desenho
    private Texture laserTexture;
    private static final float GAME_HEIGHT = 600;

    public LaserManager() {
        activateLasers = new Array<Laser>();
        laserTexture = new Texture(Gdx.files.internal("Laser.png"));
    }

    /**
     * Adiciona um laser específico ao array para gerenciá-lo
     * @param laser     laser inicializado
     */
    public void addLaser(Laser laser) {
        activateLasers.add(laser);
    }

    /**
     * Realiza o movimento de cada laser no jogo e remove eles da memória
     * caso tenha saído da tela
     * @param delta
     */
    public void update(float delta) {
        // atualiza, desenha e gerencia a memória dos lasers
        for(int i = activateLasers.size - 1; i>=0; i--) {
            Laser laser = activateLasers.get(i);
            laser.update(delta);

            if(laser.getHitbox().y > GAME_HEIGHT) {
                activateLasers.removeIndex(i);
            }
        }
    }

    /**
     * Faz o desenho de cada laser inicializada
     * @param batch     game sprite global
     */
    public void draw(SpriteBatch batch) {
        for(Laser laser:activateLasers) {
            laser.draw(batch, laserTexture);
        }
    }

    /**
     * Libera a textura do laser da memória
     */
    public void dispose() {
        laserTexture.dispose();
    }

    /**
     * Método que retorna o vetor de lasers ativos
     * @return      vetor de lasers ativos
     */
    public Array<Laser> getLasers() {
        return activateLasers;
    }
}
