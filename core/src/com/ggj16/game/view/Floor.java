package com.ggj16.game.view;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.ggj16.game.data.ImageCache;

/**
 * Created by st on 1/30/16.
 */
public class Floor {

	private static final float FALL_SPEED = 100;
    private static final float RISE_SPEED = 50;
    private static final int MAX_FALLEN_TILES = 3;
    private static final String TEXTURE_FLOOR = "floor";
    private static final String TEXTURE_PIT = "pit";

    private static final float BLOCKED = -1;
    private static final float INVISIBLE = 0;
    private static final float VISIBLE = 1;

    private float[][] tiles;
    private Array<Integer> falling, rising;

    private int width, height;
    private int tileWidth, tileHeight;
    private TextureRegion textureFloor, texturePit, tetureGlow;

    public Floor(int width, int height) {
        width += 2;
        height += 2;
        this.width = width;
        this.height = height;
        tiles = new float[height][width];
        falling = new Array<Integer>();
        rising = new Array<Integer>();

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

    public void process(float delta) {
        for (int i = 0; i < falling.size; i++) {
            int x = falling.get(i);
            int y = falling.get(i + 1);
            if (tiles[y][x] > INVISIBLE) {
                tiles[y][x] = tiles[y][x] - FALL_SPEED * delta;
                if (tiles[y][x] < INVISIBLE) tiles[y][x] = INVISIBLE;
            }
        }

        for (int i = 0; i < rising.size; i++) {
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

    public void dropTile(int x, int y) {
        x /= tileWidth;
        y /= tileHeight;

        falling.add(x);
        falling.add(y);
        while (falling.size * 2 > MAX_FALLEN_TILES) {
            int fallenx = falling.get(0);
            int falleny = falling.get(1);
            falling.removeIndex(0);
            falling.removeIndex(0);
            rising.add(x);
            rising.add(y);
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
