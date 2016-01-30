package com.ggj16.game.view;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ggj16.game.data.ImageCache;

/**
 * Created by kettricken on 30.01.2016.
 */
public class Priest {

    ChalkLine chalkLine;

    public void startDrawing() {
        // should depend on priest position
        chalkLine = new ChalkLine(ImageCache.getTexture("floor").getRegionWidth(), 0);
    };

    public void update() {
        chalkLine.increase(0, 5);
    }
    public void draw(ShapeRenderer shapeRenderer) {
        chalkLine.draw(shapeRenderer);
    }
}
