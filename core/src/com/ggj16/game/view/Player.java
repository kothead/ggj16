package com.ggj16.game.view;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ggj16.game.data.ImageCache;

/**
 * Created by st on 1/30/16.
 */
public class Player {

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

    private int x, y;
    private State state;
    private float stateTime;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void draw(Batch batch, float delta) {
        updateState(delta);
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

    private TextureRegion getStateFrame() {
        return state.getFrame(stateTime);
    }
}
