package com.ggj16.game.view;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ggj16.game.data.ImageCache;
import com.ggj16.game.model.Direction;

/**
 * Created by kettricken on 30.01.2016.
 */
public class Priest {

    protected static final float SPEED = 300;



    public enum Action {
        NONE, RUN, ACT
    }

    enum State {
        STAND("priest", 1, 0);

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
    protected Direction direction;

    private State state;
    private float stateTime;

    private Action action = Action.RUN;

    public Priest() {
        setState(State.STAND);
    }

    public void process(float delta) {
        updateState(delta);
    }

    public void draw(SpriteBatch batch) {
        TextureRegion region = getStateFrame();
        batch.draw(region, x, y, region.getRegionWidth(), region.getRegionHeight());
    }

    public void draw(ShapeRenderer shapeRenderer) {
    }

    private void setState(State state) {
        if (this.state != state) {
            this.state = state;
            stateTime = 0;
        }
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    private void updateState(float delta) {
        stateTime += delta;
    }

    private TextureRegion getStateFrame() {
        return state.getFrame(stateTime);
    }

    public Direction getDirection() {
        return direction;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return getStateFrame().getRegionWidth();
    }

    public int getHeight() {
        return getStateFrame().getRegionHeight();
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

    /**
     * Update position according to current speed
     * @param delta time
     * @return whether target is reached or not
     */
    protected boolean updatePosition(float delta) {
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
                if (action == Action.RUN) {
                    setAction(Action.ACT);
                } else if (action == Action.ACT) {
                    setAction(Action.NONE);
                }
            }

            return isTargetReached;
        } else {
            return false;
        }
    }
}
