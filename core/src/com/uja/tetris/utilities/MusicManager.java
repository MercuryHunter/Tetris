package com.uja.tetris.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Created by jonathanalp on 2016/08/07.
 */
public class MusicManager {

    public static final float DEFAULT_VOLUME = 0.1F;
    private Music music;
    private boolean muted;

    private Sound rotateSound;
    private Sound setSound;

    public MusicManager(String file) {
        music = Gdx.audio.newMusic(Gdx.files.internal(file));
        music.setLooping(true);
        muted = false;
        music.setVolume(DEFAULT_VOLUME);

        rotateSound = Gdx.audio.newSound(Gdx.files.internal("rotate_sound.wav"));
        setSound = Gdx.audio.newSound(Gdx.files.internal("set_sound.mp3"));
    }

    public void play() {
        music.play();
    }

    public void muteButton() {
        if (muted)
            music.setVolume(DEFAULT_VOLUME);
        else
            music.setVolume(0.0F);

        muted = !muted;
    }

    public boolean muted() {
        return muted;
    }

    public void pause() {
        music.pause();
    }

    public void playRotateSound(float volume) {
        if (!muted) rotateSound.play(volume);
    }

    public void playSetSound(float volume) {
        if (!muted) setSound.play(volume);
    }

    public void dispose() {
        music.dispose();
        rotateSound.dispose();
        setSound.dispose();
    }

}
