package com.ggj16.game.view;

import com.ggj16.game.util.Utils;

/**
 * Created by kettricken on 30.01.2016.
 */
public class LassoPriest extends Priest {


    public LassoPriest(Floor floor) {
        super(floor);
        setInitialPosition();
        setRandomTargetPosition();
    }

    @Override
    public void process(float delta) {
        super.process(delta);
        super.updatePosition(delta);
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

//    public void setAction(Action action) {
//        super.setAction(action);
//        if (action == Action.ACT) {
//            startDrawing();
//            int random = Utils.randInt(0, 3);
//            if (random != 0) {
//                setActTargetPosition();
//            } else {
//                setRandomTargetPosition();
//            }
//        } else if (action == Action.NONE) {
//            getFloor().cut(chalkLine);
//            chalkLine = null;
//            initNewSequence(false);
//        }
//    }
}
