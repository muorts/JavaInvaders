package com.projectoop.javainvaders.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * Entidade que representa um único alien. Apresenta os métodos e atributos que
 * todos os aliens possuem. Delega o carregamento e o movimento da horda de aliens 
 * ao AlienFleet. 
 */
public class Alien {
    private Rectangle hitbox;
    
    public Alien(float startX, float startY) {
        hitbox = new Rectangle();
        hitbox.x = startX;
        hitbox.y = startY;
        // ajuste esses valores para ajustar a hitbox completa e o tamanho do desenho do alien
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

    /**
     * Método utilizado para um único alien "se desenhar".
     * @param batch game sprite passado pela instância geral do jogo
     * @param currentFrame  frame atual em que o alien será desenhado
     */
    public void draw(SpriteBatch batch, TextureRegion currentFrame) {
        batch.draw(currentFrame, hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    /**
     * Método utilizado para pegar os valores da hitbox do alien.
     * Utilizado para checagem de colisão, por exemplo.
     * @return  o retângulo que representa o alien.
     */
    public Rectangle getHitbox() {
        return hitbox;
    }
}
