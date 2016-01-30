package com.ggj16.game.view;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.ggj16.game.data.ImageCache;
import com.ggj16.game.model.Direction;
import com.ggj16.game.util.Utils;

/**
 * Created by kettricken on 30.01.2016.
 */
public class Priest {

    protected static final float SPEED = 200;

    public enum Action {
        NONE, RUN, ACT, IDLE_RUN, ACTING, PANIC
    }

    enum State {
        STAND("priest-stand", 1, 0),
        UP("priest-back", 2, 0.1f),
        LEFT("priest-left", 2, 0.1f),
        RIGHT("priest-left", 2, 0.1f, true, false),
        DOWN("priest-down", 2, 0.1f),
        FEAR("priest-fear", 3, 0.2f, Animation.PlayMode.NORMAL),
        FEAR_RUN("priest-fear-run", 3, 0.1f),
        MAGNET("priest-lasso", 2, 0.1f),
        FALL("priest-fall", 3, 0.2f, Animation.PlayMode.NORMAL),
        CHALK_UP("priest-back", 2, 0.1f),
        CHALK_LEFT("priest-chalk-left", 2, 0.1f),
        CHALK_RIGHT("priest-chalk-left", 2, 0.1f, true, false),
        CHALK_DOWN("priest-chalk", 2, 0.1f);

        private boolean animated;
        private Animation animation;
        private TextureRegion region;

        State(String texture, int count, float duration) {
            this(texture, count, duration, false, false);
        }

        State(String texture, int count, float duration, boolean flipX, boolean flipY) {
            this(texture, count, duration, flipX, flipY, Animation.PlayMode.LOOP);
        }

        State(String texture, int count, float duration, Animation.PlayMode playMode) {
            this(texture, count, duration, false, false, playMode);
        }

        State(String texture, int count, float duration, boolean flipX, boolean flipY, Animation.PlayMode playMode) {
            if (count > 1) {
                animated = true;
                TextureRegion[] regions = ImageCache.getFrames(texture, 1, count, flipX, flipY);
                animation = new Animation(duration, new Array<TextureRegion>(regions), playMode);
            } else {
                region = ImageCache.getTexture(texture);
                if (flipX || flipY) {
                    region = new TextureRegion(region);
                    region.flip(flipX, flipY);
                }
            }
        }

        public TextureRegion getFrame(float stateTime) {
            if (animated) {
                return animation.getKeyFrame(stateTime, true);
            } else {
                return region;
            }
        }

        public boolean isEnded(float stateTime) {
            if (animated && animation.getPlayMode() == Animation.PlayMode.NORMAL) {
                return animation.isAnimationFinished(stateTime);
            } else {
                return false;
            }
        }
    }

    private float x, y, targetX, targetY;
    private float vx, vy;
    protected Direction direction;

    private State state;
    private float stateTime;
    private Floor floor;
    private Rectangle boundingBox = new Rectangle();

    private Action action = Action.RUN;

    private float panicTime = 0;

    public Priest(Floor floor) {
        this.floor = floor;
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

    public Floor getFloor() {
        return floor;
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

    public void panicStrike() {
        setRandomTargetPosition(Action.PANIC);
    }

    public void fall() {
        setTarget(Action.NONE, getX(), getY());
        // TODO: fall animation
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
                } else if (action == Action.ACT || action == Action.IDLE_RUN) {
                    setAction(Action.NONE);
                }
            }

            return isTargetReached;
        } else {
            return false;
        }
    }

    public void setRandomTargetPosition(Action action) {
        float posX = Utils.randomFloat(getFloor().getVisibleLeft(), getFloor().getVisibleRight());
        float posY = Utils.randomFloat(getFloor().getVisibleBottom(), getFloor().getVisibleTop());
        setTarget(action, posX, posY);
    }

    public Rectangle getBoundingBox() {
        TextureRegion region = getStateFrame();
        boundingBox.set(x, y, region.getRegionWidth(), region.getRegionHeight());
        return boundingBox;
    }
}
