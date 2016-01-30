package com.ggj16.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.ggj16.game.data.ImageCache;

/**
 * Created by st on 1/30/16.
 */
public class Floor {

    private static final String TEXTURE_FLOOR = "floor";
    private static final String TEXTURE_PIT = "pit";

    private static final float BLOCKED = -1;
    private static final float INVISIBLE = 0;
    private static final float VISIBLE = 1;

    private float[][] tiles;

    private int width, height;
    private int tileWidth, tileHeight;
    private TextureRegion textureFloor, texturePit, tetureGlow;

    private Rectangle visible;

    private final float cutWidth = 60;

    public Floor(int width, int height) {
        width += 2;
        height += 2;
        this.width = width;
        this.height = height;
        tiles = new float[height][width];

        textureFloor = ImageCache.getTexture(TEXTURE_FLOOR);
        texturePit = ImageCache.getTexture(TEXTURE_PIT);
        tileWidth = textureFloor.getRegionWidth();
        tileHeight = textureFloor.getRegionHeight();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                tiles[i][j] = i == 0 || j == 0 || i == height - 1 || j == width - 1 ? INVISIBLE : VISIBLE;
            }
        }

        visible = new Rectangle(tileWidth, tileHeight, getWidthInPixels() - tileWidth * 2, getHeightInPixels() - tileHeight * 2);
    }

    public void draw(Batch batch, int offsetX, int offsetY, int screenWidth, int screenHeight) {
        // calculate viewport size
        int startX = (offsetX / tileWidth - 1) * tileWidth;
        int startY = (offsetY / tileHeight - 1) * tileHeight;
        int endX = offsetX + screenWidth + tileWidth;
        int endY = offsetY + screenHeight + tileHeight;

        for (int i = startY; i < endY; i += tileHeight) {
            for (int j = startX; j < endX; j += tileWidth) {
                int tiley = i / tileHeight;
                int tilex = j / tileWidth;
                if (tiley < height && tiley >= 0
                        && tilex < width && tilex >= 0
                        && tiles[tiley][tilex] == VISIBLE) {
                    batch.draw(textureFloor, j, i, tileWidth, tileHeight);
                }
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
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.rect(visible.x, visible.y, visible.width, visible.height);
    }

    public void cut(ChalkLine chalkLine) {
        if (chalkLine.start.y == chalkLine.end.y) {
            // horizontal
//            if (tileX < getWidthInPixels() / 2) {
//                // cut left
//                visible.x += tileWidth;
//                visible.width -= tileWidth;
//            } else {
//                // cut right
//                visible.width -= tileWidth;
//            }
            Gdx.app.log("Test", "Vertical");
            if (chalkLine.start.y < getHeightInPixels() / 2) {
                visible.y += cutWidth;
                visible.height -= cutWidth;
            } else {
                // cut top
                visible.height -= cutWidth;
            }
        }
        if (chalkLine.start.x == chalkLine.end.x) {
            // vertical
            Gdx.app.log("Test", "Vertical");
            if (chalkLine.start.x < getWidthInPixels() / 2) {
                visible.x -= cutWidth;
                visible.width -= cutWidth;
            } else {
                // cut top
                visible.width -= cutWidth;
            }
        }
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
}
