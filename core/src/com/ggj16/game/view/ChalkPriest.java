package com.ggj16.game.view;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ggj16.game.data.ImageCache;
import com.ggj16.game.model.Direction;

/**
 * Created by kettricken on 30.01.2016.
 */
public class ChalkPriest extends Priest {

    private ChalkLine chalkLine;
    private float drawingSpeed = 0.5f;

    public void startDrawing() {
        // should depend on priest position
        chalkLine = new ChalkLine(ImageCache.getTexture("floor").getRegionWidth() * 2, -ImageCache.getTexture("floor").getRegionHeight());
        this.direction = Direction.DOWN;
    };

    public void update() {
        if (chalkLine != null && direction != null) {
            chalkLine.increase(drawingSpeed * direction.getDx(), drawingSpeed * direction.getDy());
        }
    }

    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        chalkLine.draw(shapeRenderer);
    }

    public ChalkLine getChalkLine() {
        return chalkLine;
    }
}
