package com.ggj16.game.view;

import com.ggj16.game.screen.GameScreen;
import com.ggj16.game.util.Utils;

/**
 * Created by kettricken on 30.01.2016.
 */
public class LassoPriest extends Priest {


    private static final float VISIBILITY_DISTANCE = 500;
    private static final float LASSO_DISTANCE = 60;
    private static final float ACTING_TIME = 5;
    GameScreen gameScreen;

    float actingTimer = 0;

    public LassoPriest(Floor floor, GameScreen gameScreen) {
        super(floor);
        this.gameScreen = gameScreen;
        setInitialPosition();
        setRandomTargetPosition(Action.IDLE_RUN);
    }

    @Override
    public void process(float delta) {
        super.process(delta);
        super.updatePosition(delta);
        if (getAction() == Action.IDLE_RUN) {
            Player player = gameScreen.getPlayer();
            float distance = Utils.getDistance(getX(), getY(), player.getX(), player.getY());
            if (distance < VISIBILITY_DISTANCE) {
                setTarget(Action.ACT, player.getX(), player.getY());
            }
        } else if (getAction() == Action.ACT) {
            Player player = gameScreen.getPlayer();
            float distance = Utils.getDistance(getX(), getY(), player.getX(), player.getY());
            if (distance <= LASSO_DISTANCE) {
                setAction(Action.ACTING);
                player.setTrapped();
            } else if (distance > VISIBILITY_DISTANCE) {
                setRandomTargetPosition(Action.IDLE_RUN);
            }
        } else if (getAction() == Action.ACTING) {
            actingTimer += delta;
            if (actingTimer >= ACTING_TIME) {
                actingTimer = 0;
                Player player = gameScreen.getPlayer();
                player.release();
                setRandomTargetPosition(Action.IDLE_RUN);
            }
        }
    }

    private void setInitialPosition() {
        float x = 0, y = 0;
        int random = Utils.randInt(0,1);
        if (random == 0) {
            x = Utils.randomFloat(getFloor().getVisibleLeft() - getFloor().getTileWidth(),
                    getFloor().getVisibleRight() + getFloor().getTileWidth());
            random = Utils.randInt(0,1);
            if (random == 0) {
                y = Utils.randomFloat(getFloor().getVisibleTop(),
                        getFloor().getVisibleTop() + getFloor().getTileHeight());
            } else {
                y = Utils.randomFloat(-getFloor().getTileHeight(), getFloor().getVisibleBottom());
            }
        } else {
            y = Utils.randomFloat(getFloor().getVisibleBottom(),
                    getFloor().getVisibleTop() + getFloor().getTileHeight());
            random = Utils.randInt(0,1);
            if (random == 0) {
                x = Utils.randomFloat(getFloor().getVisibleRight(),
                        getFloor().getVisibleRight() + getFloor().getTileWidth());
            } else {
                y = Utils.randomFloat(-getFloor().getTileWidth(), getFloor().getVisibleLeft());
            }
        }
        setPosition(x, y);
    }

    public void setAction(Action action) {
        super.setAction(action);
        if (action == Action.ACT) {

        } else if (action == Action.NONE) {
            setRandomTargetPosition(Action.IDLE_RUN);
        }
    }
}
