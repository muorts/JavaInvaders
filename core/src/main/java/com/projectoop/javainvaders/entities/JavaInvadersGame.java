package com.projectoop.javainvaders.entities;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.projectoop.javainvaders.screens.MainMenuScreen;

/**
 * Classe responsável pelo ponto de entrada do jogo e manter o contexto global.
 * É aqui que ocorre a navegação do jogo(ScreenManager) e o controle da música de fundo
 * da gameplay. Esta última está aqui para que ela não pare de tocar na transição entre as fases
 * É nessa classe que é instanciada o Sprite Batch global para melhor performance.
 */
/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class JavaInvadersGame extends Game {
    public SpriteBatch sprite;          // envia os comandos de desenho para a GPU de forma otimizada
    public OrthographicCamera camera; 
    public Music backgroundMusic;       // controla a música principal do jogo
      
    /**
     * Inicializa o jogo por completo
     */
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