package com.ggj16.game.view;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ggj16.game.model.Direction;
import com.ggj16.game.util.Utils;

/**
 * Created by kettricken on 30.01.2016.
 */
public class ChalkPriest extends Priest {


    private Floor floor;
    private ChalkLine chalkLine;

    public ChalkPriest(Floor floor) {
        super();
        this.floor = floor;
        this.direction = Direction.DOWN;//Direction.getDirections()[Utils.randInt(0, 4)];
        int side = Utils.randInt(0, 2);
        switch (direction) {
            case UP:
                setX(floor.getWidthInPixels() / 2);
                setY(floor.getVisibleBottom() - floor.getTileHeight());
                if (side == 0){
                    setTarget(Action.RUN, floor.getVisibleLeft() + floor.getCutWidth() - getWidth() / 2, floor.getVisibleBottom());
                } else {
                    setTarget(Action.RUN, floor.getVisibleRight() - floor.getCutWidth() - getWidth() / 2, floor.getVisibleBottom());
                }
                break;
            case DOWN:
                setX(floor.getWidthInPixels() / 2);
                setY(floor.getVisibleTop() + floor.getTileHeight());
                if (side == 0){
                    setTarget(Action.RUN, floor.getVisibleLeft() + floor.getCutWidth() - getWidth() / 2, floor.getVisibleTop());
                } else {
                    setTarget(Action.RUN, floor.getVisibleRight() - floor.getCutWidth() - getWidth() / 2, floor.getVisibleTop());
                }
                break;
            case LEFT:
                setX((int) (floor.getVisibleRight() + floor.getTileWidth()));
                setY(floor.getHeightInPixels() / 2);
                if (side == 0){
                    setTarget(Action.RUN, floor.getVisibleRight(), floor.getVisibleTop() - floor.getCutWidth() - getHeight() / 2);
                } else {
                    setTarget(Action.RUN, floor.getVisibleRight(), floor.getVisibleBottom() + floor.getCutWidth() - getHeight() / 2);
                }
                break;
            case RIGHT:
                setX((int) (floor.getVisibleLeft() - floor.getTileWidth()));
                setY(floor.getHeightInPixels() / 2);
                if (side == 0){
                    setTarget(Action.RUN, floor.getVisibleLeft(), floor.getVisibleTop() - floor.getCutWidth() - getHeight() / 2);
                } else {
                    setTarget(Action.RUN, floor.getVisibleLeft(), floor.getVisibleBottom() + floor.getCutWidth() - getHeight() / 2);
                }
                break;
        }
    }

    public void startDrawing() {
        switch (direction) {
            case UP:
            case DOWN:
                chalkLine = new ChalkLine(getX() + getWidth() / 2, getY());
                break;
            case LEFT:
            case RIGHT:
                chalkLine = new ChalkLine(getX(), getY() + getHeight() / 2);
                break;
        }

    };

    public void process(float delta) {
        super.process(delta);
        super.updatePosition(delta);
        if (getAction() == Action.ACT) {
            chalkLine.increase(SPEED * delta * direction.getDx(), SPEED * delta * direction.getDy());
        } else if (getAction() == Action.NONE) {
            floor.cut(chalkLine);
        }
    }

    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        chalkLine.draw(shapeRenderer);
    }

    public ChalkLine getChalkLine() {
        return chalkLine;
    }

    public void setAction(Action action) {
        super.setAction(action);
        if (action == Action.ACT) {
            switch (direction) {
                case UP:
                    startDrawing();
                    setTarget(Action.ACT, getX(), floor.getVisibleTop());
                    break;
                case DOWN:
                    startDrawing();
                    setTarget(Action.ACT, getX(), floor.getVisibleBottom() - floor.getTileHeight());
                    break;
                case LEFT:
                    startDrawing();
                    setTarget(Action.ACT, floor.getVisibleLeft() - floor.getTileWidth(), getY());
                    break;
                case RIGHT:
                    startDrawing();
                    setTarget(Action.ACT, floor.getVisibleRight(), getY());
                    break;
            }
        }
    }


}
