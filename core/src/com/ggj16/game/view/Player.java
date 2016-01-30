package com.ggj16.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ggj16.game.data.ImageCache;

/**
 * Created by st on 1/30/16.
 */
public class Player {

    private static final float SPEED = 600;

    public enum Action {
        NONE, GO, SCARE, BREAK_FLOOR
    }

    enum State {
        STAND("ghost", 1, 0);

        private boolean animated;
        private Animation animation;
        private TextureRegion region;

        State(String texture, int count, float duration) {
            if (count > 1) {
                animated = true;
                animation = new Animation(duration, ImageCache.getFrames(texture, 1, count));
            } else {
                region = ImageCache.getTexture(texture);
            }
        }

        public TextureRegion getFrame(float stateTime) {
            if (animated) {
                return animation.getKeyFrame(stateTime, true);
            } else {
                return region;
            }
        }
    }

    private float x, y, targetX, targetY;
    private float vx, vy;
    private Action action = Action.NONE;
    private State state;
    private float stateTime;

    public Player() {
        setState(State.STAND);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setTarget(Action action, float x, float y) {
        // calculate speed pixel per second
        if (action != Action.NONE) {
            float diffX = x - getX();
            float diffY = y - getY();
            float path = (float) Math.sqrt(diffX * diffX + diffY * diffY);
            if (path > 0) {
                vx = diffX / path * SPEED;
                vy = diffY / path * SPEED;

                this.action = action;
                targetX = x;
                targetY = y;
            }
        }
    }

    public void process(float delta) {
        updateState(delta);
        if (action != Action.NONE && updatePosition(delta)) {
            switch (action) {
                case GO:
                    break;

                case SCARE:
                    break;

                case BREAK_FLOOR:
                    break;
            }
            action = Action.NONE;
        }
    }

    public void draw(Batch batch, float delta) {
        TextureRegion region = getStateFrame();
        batch.draw(region, x, y, region.getRegionWidth(), region.getRegionHeight());
    }

    private void setState(State state) {
        if (this.state != state) {
            this.state = state;
            stateTime = 0;
        }
    }

    private void updateState(float delta) {
        stateTime += delta;
    }

    /**
     * Update position according to current speed
     * @param delta time
     * @return whether target is reached or not
     */
    private boolean updatePosition(float delta) {
        if (vx != 0 || vy != 0) {
            setPosition(getX() + vx * delta, getY() + vy * delta);
            float diffX = x - targetX;
            float diffY = y - targetY;
            boolean isTargetReached = (diffX == 0 || Math.signum(diffX) == Math.signum(vx))
                    && (diffY == 0 || Math.signum(diffY) == Math.signum(vy));
            if (isTargetReached) {
                setPosition(targetX, targetY);
                vx = 0;
                vy = 0;
            }

            return isTargetReached;
        } else {
            return false;
        }
    }

    private TextureRegion getStateFrame() {
        return state.getFrame(stateTime);
    }
}
