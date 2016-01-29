package com.ggj16.game.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class SkinCache {

    private static final String SKIN_DIR = "image/";
    private static final String SKIN_FILE = "uiskin.json";

    private static Skin skin;

    public static void load() {
        skin = new Skin(Gdx.files.internal(SKIN_DIR + SKIN_FILE));
    }

    public static Skin getDefaultSkin() {
        return skin;
    }
}
