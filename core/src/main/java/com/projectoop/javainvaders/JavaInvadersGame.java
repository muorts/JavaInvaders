package com.projectoop.javainvaders;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class JavaInvadersGame extends Game {
    public SpriteBatch sprite;          // envia os comandos de desenho para a GPU de forma otimizada
    public OrthographicCamera camera;       

    @Override
    public void create() {
        this.sprite = new SpriteBatch();
        this.setScreen(new MainMenuScreen(this));       // envia uma instancia de JavaInvadersGame para a tela

    }

    @Override
    // Método auxiliar que limpa o SpriteBatch do jogo quando for fechado
    public void dispose() {
        sprite.dispose();
    }
}