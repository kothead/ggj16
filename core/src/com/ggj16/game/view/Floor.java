package com.ggj16.game.view;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.ggj16.game.data.ImageCache;

/**
 * Created by st on 1/30/16.
 */
public class Floor {

	private static final float FALL_SPEED = 10;
    private static final float RISE_SPEED = 3;
    private static final int MAX_FALLEN_TILES = 3;
    private static final String TEXTURE_FLOOR = "floor-tile-v7";
    private static final String TEXTURE_HOLE = "floor-tile-hole-fire";

    private static final float BLOCKED = -1;
    private static final float INVISIBLE = 0;
    private static final float VISIBLE = 1;

    private float[][] tiles;
    private Array<Integer> falling, rising;

    private int width, height;
    private int tileWidth, tileHeight;
    private TextureRegion textureFloor;
    private Animation holeAnimation;
    private float stateTime;

    private Rectangle visible;

    private final float cutWidth = 60;

    public Floor(int width, int height) {
        width += 2;
        height += 2;
        this.width = width;
        this.height = height;
        tiles = new float[height][width];
        falling = new Array<Integer>();
        rising = new Array<Integer>();

        textureFloor = ImageCache.getTexture(TEXTURE_FLOOR);
        tileWidth = textureFloor.getRegionWidth();
        tileHeight = textureFloor.getRegionHeight();

        TextureRegion[] regions = ImageCache.getFrames(TEXTURE_HOLE, 1, 3);
        holeAnimation = new Animation(0.1f, new Array<TextureRegion>(regions), Animation.PlayMode.LOOP);

        initFloor();


    }

    public void initFloor() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                tiles[i][j] = i == 0 || j == 0 || i == height - 1 || j == width - 1 ? BLOCKED : VISIBLE;
            }
        }

        visible = new Rectangle(tileWidth, tileHeight, getWidthInPixels() - tileWidth * 2, getHeightInPixels() - tileHeight * 2);

        falling.clear();
    }

    public void process(float delta) {
        stateTime += delta;

        for (int i = 0; i < falling.size; i += 2) {
            int x = falling.get(i);
            int y = falling.get(i + 1);
            if (tiles[y][x] > INVISIBLE) {
                tiles[y][x] = tiles[y][x] - FALL_SPEED * delta;
                if (tiles[y][x] < INVISIBLE) tiles[y][x] = INVISIBLE;
            }
        }

        for (int i = 0; i < rising.size; i += 2) {
            int x = rising.get(i);
            int y = rising.get(i + 1);
            if (tiles[y][x] < VISIBLE) {
                tiles[y][x] = tiles[y][x] + RISE_SPEED * delta;
                if (tiles[y][x] > VISIBLE) {
                    tiles[y][x] = VISIBLE;
                    rising.removeIndex(i);
                    rising.removeIndex(i);
                }
            }
        }
    }

    public void draw(Batch batch, int offsetX, int offsetY, int screenWidth, int screenHeight) {
        // calculate viewport size
        int startX = (offsetX / tileWidth - 1) * tileWidth;
        int startY = offsetY - tileHeight;
        int endX = offsetX + screenWidth + tileWidth;
        int endY = (offsetY + screenHeight) / tileHeight * (tileHeight + 1);

        for (int i = endY; i >= startY; i -= tileHeight) {
            for (int j = startX; j < endX; j += tileWidth) {
                int tiley = i / tileHeight;
                int tilex = j / tileWidth;
                if (tiley < height && tiley >= 0
                        && tilex < width && tilex >= 0) {

                    float visibility = tiles[tiley][tilex];
                    if (visibility == BLOCKED) {
                        visibility = VISIBLE;
                    }

                    if (visibility >= INVISIBLE && visibility < VISIBLE) {
                        batch.draw(holeAnimation.getKeyFrame(stateTime), j, i, tileWidth, tileHeight);
                    }
                    if (visibility > INVISIBLE) {
                        batch.draw(textureFloor, j, i + tileHeight * (visibility - VISIBLE), tileWidth, tileHeight);
                    }
                }
            }
        }


    }

    public void dropTile(float posx, float posy) {
        int x = (int) (posx / tileWidth);
        int y = (int) (posy / tileHeight);

        if (tiles[y][x] > INVISIBLE) {
            falling.add(x);
            falling.add(y);
            while (falling.size > MAX_FALLEN_TILES * 2) {
                rising.add(falling.removeIndex(0));
                rising.add(falling.removeIndex(0));
            }
        }
    }

    public int getWidthInPixels() {
        return width * tileWidth;
    }

    public int getWidthInTiles() {
        return width;
    }

    public int getHeightInPixels() {
        return height * tileHeight;
    }

    public int getHeightInTiles() {
        return height;
    }

    public void draw(ShapeRenderer shapeRenderer, int offsetX, int offsetY, int screenWidth, int screenHeight) {
        // x, y are the bottom left corner
//        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(1, 0, 0, 1);
//        shapeRenderer.rect(visible.x, visible.y, visible.width, visible.height);
    }

    public boolean cut(ChalkLine chalkLine) {
        if (chalkLine == null) {
            return false;
        }
        if (chalkLine.start.y == chalkLine.end.y) {
            if (chalkLine.start.y < getHeightInPixels() / 2) {
                if (visible.y < chalkLine.start.y) {
                    visible.y += cutWidth;
                    visible.height -= cutWidth;
                }
            } else {
                if (visible.y + visible.getHeight() > chalkLine.start.y) {
                    visible.height -= cutWidth;
                }
            }
        }
        if (chalkLine.start.x == chalkLine.end.x) {
            if (chalkLine.start.x < getWidthInPixels() / 2) {
                if (visible.x < chalkLine.start.x) {
                    visible.x += cutWidth;
                    visible.width -= cutWidth;
                }
            } else {
                if (visible.x + visible.getWidth() > chalkLine.start.x) {
                    visible.width -= cutWidth;
                }
            }
        }
        return true;
    }

    public float getVisibleTop() {
        return visible.y + visible.height;
    }

    public float getVisibleBottom() {
        return visible.y;
    }

    public float getVisibleRight() {
        return visible.x + visible.getWidth();
    }

    public float getVisibleLeft() {
        return visible.x;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public float getCutWidth() {
        return cutWidth;
    }

    public float getVisibleWidthInPixels() {
        return visible.getWidth();
    }

    public float getVisibleHeightInPixels() {
        return visible.getHeight();
    }

    public boolean isPentagramCircled() {
        if (getVisibleLeft() < getWidthInPixels() / 2 - tileWidth ||
                getVisibleRight() > getWidthInPixels() / 2 + tileWidth ||
                getVisibleTop() > getHeightInPixels() / 2 + tileWidth ||
                getVisibleBottom() < getHeightInPixels() / 2 - tileWidth) {
        //if (visible.getWidth() + cutWidth <= tileWidth * 3 && visible.getHeight() + cutWidth <= tileHeight * 3) {
            return false;
        }
        return true;
    }

    public float getLeftVisibleWidth() {
        return getWidthInPixels() / 2 - visible.getX();
    }

    public float getRightVisibleWidth() {
        return visible.getX() + visible.getWidth() - getWidthInPixels() / 2;
    }

    public float getTopVisibleHeight() {
        return visible.getY() + visible.getHeight() - getHeightInPixels() / 2;
    }

    public float getBottomVisibleHeight() {
        return getHeightInPixels() / 2 - visible.getY();
    }

    public boolean onPit(Priest priest) {
        int tilex = (int) ((priest.getX() + priest.getWidth() / 2) / tileWidth);
        int tiley = (int) ((priest.getY() + priest.getHeight() / 2) / tileHeight);

        return tiley >= 0 && tiley < height
                && tilex >= 0 && tilex < width
                && tiles[tiley][tilex] >= INVISIBLE
                && tiles[tiley][tilex] < VISIBLE;
    }

    public boolean onPentagram(Player player) {
        return false;
    }
}
