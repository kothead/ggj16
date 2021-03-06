package com.ggj16.game.view;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.ggj16.game.data.ImageCache;
import com.ggj16.game.data.SoundCache;
import com.ggj16.game.model.Direction;
import com.ggj16.game.screen.GameScreen;
import com.ggj16.game.util.Utils;

/**
 * Created by kettricken on 30.01.2016.
 */
public abstract class Priest {

    protected static final float SPEED = 400;

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
        MAGNET("priest-magnet", 2, 0.1f),
        MAGNET_DOWN("priest-magnet-down", 2, 0.1f),
        MAGNET_UP("priest-magnet-up", 2, 0.1f),
        FALL("priest-fall", 3, 0.1f, Animation.PlayMode.NORMAL),
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

    private boolean alive = true;
    private int fallenTileX, fallenTileY;

    private State state;
    private float stateTime;
    private GameScreen screen;
    private Rectangle boundingBox = new Rectangle();

    private Action action = Action.RUN;

    public Priest(GameScreen screen) {
        this.screen = screen;
        setState(State.STAND);
    }

    public void process(float delta) {
        updateState(delta);
    }

    public void draw(SpriteBatch batch) {
        TextureRegion region = getStateFrame();
        if (state != State.FALL) {
            batch.draw(region, x, y, region.getRegionWidth(), region.getRegionHeight());
        } else {
            Floor floor = screen.getFloor();
            batch.draw(region, fallenTileX * floor.getTileWidth() + (floor.getTileWidth() - getWidth()) / 2,
                    fallenTileY * floor.getTileHeight() + (floor.getTileHeight() - getHeight()) / 2,
                    region.getRegionWidth(), region.getRegionHeight());
        }
    }

    public void draw(ShapeRenderer shapeRenderer) {
    }

    protected void setState(State state) {
        if (this.state != state) {
            this.state = state;
            stateTime = 0;

            switch (state) {
                case FALL:
                    SoundCache.play(SoundCache.SOUND_FALLING);
                    break;

                case MAGNET:
                    SoundCache.play(SoundCache.SOUND_LASSO);
                    break;

                case FEAR:
                case FEAR_RUN:
                    SoundCache.play(SoundCache.SOUND_FEAR);
                    break;

                case CHALK_DOWN:
                case CHALK_LEFT:
                case CHALK_RIGHT:
                case CHALK_UP:
                    //SoundCache.play(SoundCache.SOUND_DRAWING);
                    break;
            }
        }
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public boolean isAlive() {
        return alive;
    }

    private void updateState(float delta) {
        stateTime += delta;
        if (state.isEnded(stateTime)) {
            switch (state) {
                case FALL:
                    alive = false;
                    return;
            }
            setState(State.STAND);
        }
    }

    private TextureRegion getStateFrame() {
        return state.getFrame(stateTime);
    }

    public Direction getDirection() {
        return direction;
    }

    public GameScreen getGameScreen() {
        return screen;
    }

    public Floor getFloor() {
        return screen.getFloor();
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
            this.action = action;
            targetX = x;
            targetY = y;

            float diffX = x - getX();
            float diffY = y - getY();
            float path = (float) Math.sqrt(diffX * diffX + diffY * diffY);
            if (path > 0) {
                vx = diffX / path * SPEED;
                vy = diffY / path * SPEED;

                Direction direction = Direction.getByOffset(Math.abs(vx) >= Math.abs(vy) ? Math.signum(vx) : 0,
                        Math.abs(vy) > Math.abs(vx) ? Math.signum(vy) : 0);
                updateStateForDirection(direction);
            }
        }
    }

    protected abstract void updateStateForDirection(Direction direction);

    public float getCenterX() {
        return x + getWidth() / 2;
    }

    public float getCenterY() {
        return y + getHeight() / 2;
    }

    public void panicStrike(float x, float y) {
        float ratio = 2.5f;

        float x3 = ratio * getCenterX() + (1 - ratio) * x; // find point that divides the segment
        float y3 = ratio * getCenterY() + (1 - ratio) * y; //into the ratio (1-r):r

        setTarget(Action.PANIC, x3, y3);
    }

    public void fall() {
        if (state == State.FALL) return;
        action = Action.NONE;
        vx = 0;
        vy = 0;

        int tileWidth = getFloor().getTileWidth();
        int tileHeight = getFloor().getTileHeight();
        fallenTileX = (int) ((getX() + getWidth() / 2) / tileWidth);
        fallenTileY = (int) ((getY() + getHeight() / 2) / tileHeight);

        setState(State.FALL);
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
                } else if (action == Action.PANIC) {
                    setRandomTargetPosition(Action.IDLE_RUN);
                }
            }

            return isTargetReached;
        }
        return false;
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
