package com.ggj16.game.view;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.ggj16.game.data.ImageCache;
import com.ggj16.game.screen.GameScreen;

/**
 * Created by st on 1/31/16.
 */
public class Portal extends Sprite {

    private static final String TEXTURE_GRAMMA = "gramma";

    private GameScreen screen;

    public Portal(GameScreen screen) {
        super(ImageCache.getTexture(TEXTURE_GRAMMA));
        this.screen = screen;

        Floor floor = screen.getFloor();
        setPosition((floor.getWidthInPixels() - getWidth()) / 2,
                (floor.getHeightInPixels() - getHeight()) / 2);
    }
}
