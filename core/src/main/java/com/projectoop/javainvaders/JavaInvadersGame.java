package com.projectoop.javainvaders;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class JavaInvadersGame extends Game {
    public SpriteBatch sprite;          // envia os comandos de desenho para a GPU de forma otimizada
    public OrthographicCamera camera; 
    public Music backgroundMusic;       // controla a música principal do jogo
      

    @Override
    public void create() {
        this.sprite = new SpriteBatch();

        // inicializa a música na memória global
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("level_1.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);

        this.setScreen(new MainMenuScreen(this));       // envia uma instancia de JavaInvadersGame para a tela

    }

    @Override
    // Método auxiliar que limpa o SpriteBatch do jogo quando for fechado
    public void dispose() {
        sprite.dispose();
    }
}