package com.ggj16.game.view;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by kettricken on 30.01.2016.
 */
public class ChalkLine {

    Vector2 start = new Vector2();
    Vector2 end = new Vector2();

    public ChalkLine(float startX, float startY) {
        start.set(startX, startY);
        end.set(startX, startY);
    }

    public void increase(float x, float y) {
        end.set(end.x + x, end.y + y);
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.rectLine(start.x, start.y, end.x, end.y, 3);
    }

    public Vector2 getEnd() {
        return end;
    }

}
