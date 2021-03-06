package com.ggj16.game.view;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ggj16.game.model.Direction;
import com.ggj16.game.screen.GameScreen;
import com.ggj16.game.util.Utils;

/**
 * Created by kettricken on 30.01.2016.
 */
public class ChalkPriest extends Priest {

    public enum Side {
        LEFT, RIGHT, TOP, BOTTOM
    }

    private ChalkLine chalkLine;

    public ChalkPriest(GameScreen screen) {
        super(screen);
        initNewSequence(true);
    }

    private void initNewSequence(boolean setInitialPosition) {
        if (getFloor().isPentagramCircled()) {
            return;
        }

        int random = Utils.randInt(0, 5);
        Side side = Side.values()[Utils.randInt(0, 3)];
        if (random == 0 || random == 1) {
            this.direction = Direction.getDirections()[Utils.randInt(0, 3)];
        } else {
            if (getFloor().getVisibleWidthInPixels() > getFloor().getVisibleHeightInPixels()) {
                this.direction = Direction.getDirections()[Utils.randInt(2, 3)];
                if (getFloor().getLeftVisibleWidth() > getFloor().getRightVisibleWidth()) {
                    side = Side.LEFT;
                } else if (getFloor().getLeftVisibleWidth() < getFloor().getRightVisibleWidth()) {
                    side = Side.RIGHT;
                }
            } else if (getFloor().getVisibleWidthInPixels() < getFloor().getVisibleHeightInPixels()) {
                this.direction = Direction.getDirections()[Utils.randInt(0, 1)];
                if (getFloor().getTopVisibleHeight() > getFloor().getBottomVisibleHeight()) {
                    side = Side.TOP;
                } else if (getFloor().getTopVisibleHeight() < getFloor().getBottomVisibleHeight()) {
                    side = Side.BOTTOM;
                }
            } else {
                this.direction = Direction.getDirections()[Utils.randInt(0, 3)];
            }
        }
        if (setInitialPosition)
            setInitialPosition();
        setRunTargetPosition(side);
    }

    @Override
    public void process(float delta) {
        super.process(delta);
        super.updatePosition(delta);
        if (getAction() == Action.ACT && chalkLine != null) {
            chalkLine.increase(SPEED * delta * direction.getDx(),
                    SPEED * delta * direction.getDy());
        }
    }

    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        if (chalkLine != null) {
            chalkLine.draw(shapeRenderer);
        }
    }

    public void setAction(Action action) {
        super.setAction(action);
        if (action == Action.ACT) {
            startDrawing();
            int random = Utils.randInt(0, 3);
            if (random != 0) {
                setActTargetPosition();
            } else {
                setRandomTargetPosition(Action.IDLE_RUN);
            }
        } else if (action == Action.NONE) {
            boolean cut = getFloor().cut(chalkLine);
            if (cut) getGameScreen().onVisibleZoneCut();
            chalkLine = null;
            initNewSequence(false);
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

    }

    private void setInitialPosition() {
        switch (direction) {
            case UP:
                setX(getFloor().getWidthInPixels() / 2);
                setY(getFloor().getVisibleBottom() - getFloor().getTileHeight());
                break;
            case DOWN:
                setX(getFloor().getWidthInPixels() / 2);
                setY(getFloor().getVisibleTop() + getFloor().getTileHeight());
                break;
            case LEFT:
                setX(getFloor().getVisibleRight() + getFloor().getTileWidth());
                setY(getFloor().getHeightInPixels() / 2);
                break;
            case RIGHT:
                setX(getFloor().getVisibleLeft() - getFloor().getTileWidth());
                setY(getFloor().getHeightInPixels() / 2);
                break;
        }
    }

    private void setRunTargetPosition(Side side) {
        switch (direction) {
            case UP:
                if (side == Side.LEFT){
                    setTarget(Action.RUN, getFloor().getVisibleLeft() + getFloor().getCutWidth() - getWidth() / 2, getFloor().getVisibleBottom());
                } else {
                    setTarget(Action.RUN, getFloor().getVisibleRight() - getFloor().getCutWidth() - getWidth() / 2, getFloor().getVisibleBottom());
                }
                break;
            case DOWN:
                if (side == Side.LEFT){
                    setTarget(Action.RUN, getFloor().getVisibleLeft() + getFloor().getCutWidth() - getWidth() / 2, getFloor().getVisibleTop());
                } else {
                    setTarget(Action.RUN, getFloor().getVisibleRight() - getFloor().getCutWidth() - getWidth() / 2, getFloor().getVisibleTop());
                }
                break;
            case LEFT:
                if (side == Side.TOP){
                    setTarget(Action.RUN, getFloor().getVisibleRight(), getFloor().getVisibleTop() - getFloor().getCutWidth() - getHeight() / 2);
                } else {
                    setTarget(Action.RUN, getFloor().getVisibleRight(), getFloor().getVisibleBottom() + getFloor().getCutWidth() - getHeight() / 2);
                }
                break;
            case RIGHT:
                if (side == Side.TOP){
                    setTarget(Action.RUN, getFloor().getVisibleLeft(), getFloor().getVisibleTop() - getFloor().getCutWidth() - getHeight() / 2);
                } else {
                    setTarget(Action.RUN, getFloor().getVisibleLeft(), getFloor().getVisibleBottom() + getFloor().getCutWidth() - getHeight() / 2);
                }
                break;
        }

    }

    private void setActTargetPosition() {
        switch (direction) {
            case UP:
                setTarget(Action.ACT, getX(), getFloor().getVisibleTop());
                setState(State.CHALK_UP);
                break;
            case DOWN:
                setTarget(Action.ACT, getX(), getFloor().getVisibleBottom() - getFloor().getTileHeight());
                setState(State.CHALK_DOWN);
                break;
            case LEFT:
                setTarget(Action.ACT, getFloor().getVisibleLeft() - getFloor().getTileWidth(), getY());
                setState(State.CHALK_LEFT);
                break;
            case RIGHT:
                setTarget(Action.ACT, getFloor().getVisibleRight(), getY());
                setState(State.CHALK_RIGHT);
                break;
        }
    }

    @Override
    protected void updateStateForDirection(Direction direction) {
        switch (getAction()) {
            case ACT:
                switch (direction) {
                    case DOWN:
                        setState(State.CHALK_DOWN);
                        break;

                    case LEFT:
                        setState(State.CHALK_LEFT);
                        break;

                    case RIGHT:
                        setState(State.CHALK_RIGHT);
                        break;

                    case UP:
                        setState(State.CHALK_UP);
                        break;
                }
                break;

            case IDLE_RUN:
            case RUN:
                switch (direction) {
                    case DOWN:
                        setState(State.DOWN);
                        break;

                    case LEFT:
                        setState(State.LEFT);
                        break;

                    case RIGHT:
                        setState(State.RIGHT);
                        break;

                    case UP:
                        setState(State.UP);
                        break;
                }
                break;

            case PANIC:
                setState(State.FEAR_RUN);
                break;

            case NONE:
                setState(State.STAND);
                break;
        }
    }
}
