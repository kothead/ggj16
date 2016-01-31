package com.ggj16.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.ggj16.game.data.ImageCache;
import com.ggj16.game.data.SoundCache;
import com.ggj16.game.model.Direction;
import com.ggj16.game.screen.GameScreen;
import com.ggj16.game.state.GameStates;

/**
 * Created by st on 1/30/16.
 */
public class Player {

    private static final float SPEED = 600;
    private static final float TRAPPED_SPEED = 200;
    private static final float TIME_TO_DEATH = 5;

    public enum Action {
        NONE, GO, SCARE, BREAK_FLOOR, TRAPPED
    }

    enum State {
        STAND("ghost-standing", 2, 0.1f),
        DOWN("ghost-down", 4, 0.1f),
        LEFT("ghost-left", 4, 0.1f),
        RIGHT("ghost-left", 4, 0.1f, true, false),
        UP("ghost-up", 4, 0.1f),
        TRAPPED("ghost-trapped", 3, 0.1f, Animation.PlayMode.LOOP),
        BREAK_FLOOR("ghost-floor", 4, 0.1f, Animation.PlayMode.NORMAL),
        SCREAM("ghost-scream", 7, 0.1f, Animation.PlayMode.NORMAL);

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

    private GameScreen gameScreen;
    private float x, y, targetX, targetY;
    private float vx, vy;
    private Action action = Action.NONE;
    private State state;
    private float stateTime;
    private float deathTime = 0;
    private Rectangle boundingBox = new Rectangle();

    public Player(GameScreen screen) {
        setState(State.STAND);
        gameScreen = screen;
    }

    public void reset() {
        setState(State.STAND);
        setX(gameScreen.getFloor().getWidthInPixels() / 2);
        setY(gameScreen.getFloor().getHeightInPixels() / 2);
    }

    public int getWidth() {
        return getStateFrame().getRegionWidth();
    }

    public int getHeight() {
        return getStateFrame().getRegionHeight();
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

        Floor floor = gameScreen.getFloor();
        if (y + getHeight() > floor.getVisibleTop()) {
            y = floor.getVisibleTop() - getHeight();
        } else if (y < floor.getVisibleBottom()) {
            y = floor.getVisibleBottom();
        }
        if (x + getWidth() > floor.getVisibleRight()) {
            x = floor.getVisibleRight() - getWidth();
        } else if (x < floor.getVisibleLeft()) {
            x = floor.getVisibleLeft();
        }

        // calculate speed pixel per second
        if (action == Action.TRAPPED && state != State.TRAPPED) {
            setState(State.TRAPPED);
        } else if (this.action == Action.TRAPPED) {
            return;
        }
        if (action != Action.NONE) {
            float diffX = x - getX();
            float diffY = y - getY();
            float path = (float) Math.sqrt(diffX * diffX + diffY * diffY);
            if (path > 0) {
                float speed = state == State.TRAPPED ? TRAPPED_SPEED : SPEED;
                vx = diffX / path * speed;
                vy = diffY / path * speed;

                if (action != Action.TRAPPED) {
                    Direction direction = Direction.getByOffset(Math.abs(vx) >= Math.abs(vy) ? Math.signum(vx) : 0,
                            Math.abs(vy) > Math.abs(vx) ? Math.signum(vy) : 0);
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
                }
            }
            this.action = action;
            targetX = x;
            targetY = y;
        }
    }

    public void process(float delta) {
        Rectangle death = gameScreen.getPortal().getBoundingRectangle();
        if (death.contains(x + getWidth() / 2, y + getHeight() / 2)) {
            deathTime += delta;
            if (deathTime >= TIME_TO_DEATH) {
                deathTime = TIME_TO_DEATH;
                SoundCache.play(SoundCache.SOUND_DESTROYER);
                gameScreen.getStateMachine().changeState(GameStates.GAME_OVER);
            }
        } else if (deathTime > 0) {
            deathTime -= delta;
        } else if (deathTime < 0) {
            deathTime = 0;
        }

        updateState(delta);
        if (action != Action.NONE && updatePosition(delta)) {
            switch (action) {
                case GO:
                    action = Action.NONE;
                    break;

                case SCARE:
                    setState(State.SCREAM);
                    action = Action.NONE;
                    break;

                case BREAK_FLOOR:
                    setState(State.BREAK_FLOOR);
                    action = Action.NONE;
                    break;
            }
        }
    }

    public void draw(Batch batch, float delta) {
        TextureRegion region = getStateFrame();
        Color color = new Color(batch.getColor());
        batch.setColor(color.r, color.g, color.b, 1 - deathTime / TIME_TO_DEATH );
        batch.draw(region, x, y, region.getRegionWidth(), region.getRegionHeight());
        batch.setColor(color);
    }

    public Rectangle getBoundingBox() {
        TextureRegion region = getStateFrame();
        boundingBox.set(x, y, region.getRegionWidth(), region.getRegionHeight());
        return boundingBox;
    }

    private void setState(State state) {
        if (this.state != state) {
            this.state = state;
            stateTime = 0;

            switch (state) {
                case BREAK_FLOOR:
                    SoundCache.play(SoundCache.SOUND_FALLING_TILE);
                    break;

                case SCREAM:
                    SoundCache.play(SoundCache.SOUND_SCREAM);
                    break;
            }
        }
    }

    private void updateState(float delta) {
        stateTime += delta;
        if (state.isEnded(stateTime)) {
            switch (state) {
                case BREAK_FLOOR:
                    gameScreen.getFloor().dropTile(x + getWidth() / 2, y + getHeight() / 2);
                    break;

                case SCREAM:
                    gameScreen.getPriestProcessor().panic(x + getWidth() / 2, y + getHeight() / 2);
                    break;

                case TRAPPED:
                    setState(State.STAND);
                    break;

//                case DYING:
//                    gameScreen.getStateMachine().changeState(GameStates.GAME_OVER);
//                    break;
            }
            setState(State.STAND);
        }
    }

    /**
     * Update position according to current speed
     * @param delta time
     * @return whether target is reached or not
     */
    private boolean updatePosition(float delta) {
        if (action != Action.NONE) {
            setPosition(getX() + vx * delta, getY() + vy * delta);
            float diffX = x - targetX;
            float diffY = y - targetY;
            boolean isTargetReached = (diffX == 0 || Math.signum(diffX) == Math.signum(vx))
                    && (diffY == 0 || Math.signum(diffY) == Math.signum(vy));
            if (isTargetReached) {
                setPosition(targetX, targetY);
                vx = 0;
                vy = 0;
                if (action != Action.TRAPPED) {
                    setState(State.STAND);
                }
            }

            return isTargetReached;
        } else {
            return false;
        }
    }

    private TextureRegion getStateFrame() {
        return state.getFrame(stateTime);
    }

    public void setTrapped() {
        setTarget(Action.TRAPPED, gameScreen.getFloor().getWidthInPixels() / 2, gameScreen.getFloor().getHeightInPixels() / 2);
    }

    public void release() {
        action = Action.NONE;
    }

    public Action getAction() {
        return action;
    }

}
