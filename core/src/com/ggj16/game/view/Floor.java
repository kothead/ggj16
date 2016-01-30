package com.ggj16.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    public Floor(int width, int height) {
        this.width = width + 2;
        this.height = height + 2;
        tiles = new float[this.height][this.width];

        textureFloor = ImageCache.getTexture(TEXTURE_FLOOR);
        texturePit = ImageCache.getTexture(TEXTURE_PIT);
        tileWidth = textureFloor.getRegionWidth();
        tileHeight = textureFloor.getRegionHeight();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                tiles[i][j] = i == 0 || j == 0 || i == height - 1 || j == width - 1 ? INVISIBLE : VISIBLE;
            }
        }
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
}
