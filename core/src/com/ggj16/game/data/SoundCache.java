package com.ggj16.game.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.ObjectMap;

public class SoundCache {

    public static final String SOUND_DEATH = "death";
    public static final String SOUND_DESTROYER = "destroyer";
    public static final String SOUND_DRAWING = "drawing";
    public static final String SOUND_FALLING = "falling";
    public static final String SOUND_FALLING_TILE = "falling-tile";
    public static final String SOUND_FEAR = "fear";
    public static final String SOUND_LASSO = "lasso";
    public static final String SOUND_SCREAM = "scream";

    private static final String SOUND_DIR = "audio/sound/";
    private static final String SOUND_EXT = ".mp3";

    private static ObjectMap<String, Sound> sounds;

    public static void load() {
        sounds = new ObjectMap<String, Sound>();

        String[] keys = {
                SOUND_DEATH, SOUND_DESTROYER, SOUND_DRAWING, SOUND_FALLING,
                SOUND_FALLING_TILE, SOUND_FEAR, SOUND_LASSO, SOUND_SCREAM
        };
        for (String key: keys) {
            Sound sound = Gdx.audio.newSound(Gdx.files.internal(SOUND_DIR + key + SOUND_EXT));
            sounds.put(key, sound);
        }
    }

    public static void play(String key) {
        sounds.get(key).play(0.3f);
    }
}
