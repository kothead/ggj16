package com.ggj16.game.processor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Array;
import com.ggj16.game.screen.GameScreen;
import com.ggj16.game.util.Utils;
import com.ggj16.game.view.ChalkPriest;
import com.ggj16.game.view.Floor;
import com.ggj16.game.view.LassoPriest;
import com.ggj16.game.view.Priest;

import java.util.Iterator;

/**
 * Created by kettricken on 30.01.2016.
 */
public class PriestProcessor {

    private static final int PANIC_HALF_WIDTH = 300;
    private static final int PANIC_HALF_HEIGHT = 300;
    private static final int WAVE_PAUSE = 3;
    Array<Priest> priests = new Array<Priest>();
    Floor floor;
    GameScreen gameScreen;
    float waveDelay = 0;

    Rectangle panicAttack = null;

    public PriestProcessor(Floor floor, GameScreen gameScreen) {
        this.floor = floor;
        this.gameScreen = gameScreen;
    }

    public void startWave() {
        Action action = new Action() {
            @Override
            public boolean act(float delta) {
                int chalkAmount = Utils.randInt(2, 8);
                int magnetAmount = Utils.randInt(1, 4);
                generatePriests(chalkAmount, magnetAmount);
                return true;
            }
        };
        gameScreen.getViewProcessor().showWaveTable(action);
    }

    public void update(float delta) {
        if (priests.size == 0) {
            waveDelay += delta;
            if (waveDelay >= WAVE_PAUSE) {
                startWave();
                waveDelay = 0;
            }
        }

        Iterator<Priest> iter = priests.iterator();
        while (iter.hasNext()) {
            Priest priest = iter.next();
            if (priest instanceof ChalkPriest) {
                ChalkPriest chalkPriest = (ChalkPriest) priest;
                chalkPriest.process(delta);
            } else if (priest instanceof LassoPriest) {
                LassoPriest lassoPriest = (LassoPriest) priest;
                lassoPriest.process(delta);
            }
            if (floor.onPit(priest)) {
                priest.fall();
            }
            if (!priest.isAlive()) {
                iter.remove();
            }
        }
    }

    public void generatePriests(int chalkAmount, int magnetAmount) {
        for (int i = 0; i < chalkAmount; i++) {
            ChalkPriest priest = new ChalkPriest(gameScreen);
            priest.startDrawing();
            priests.add(priest);
        }
        /*for (int i = 0; i < magnetAmount; i++) {
            LassoPriest priest = new LassoPriest(gameScreen);
            priests.add(priest);
        }*/
    }

    public void draw(ShapeRenderer shapeRenderer) {
        for (Priest priest : priests) {
            priest.draw(shapeRenderer);
        }
    }

    public void draw(SpriteBatch spriteBatch) {
        for (Priest priest : priests) {
            priest.draw(spriteBatch);
        }
    }

    public Priest findTouchedPriest(float x, float y) {
        for (Priest priest: priests) {
            if (priest.getBoundingBox().contains(x, y)) {
                return priest;
            }
        }
        return null;
    }

    public void panic(float x, float y) {
        if (panicAttack == null) {
            panicAttack = new Rectangle(x - PANIC_HALF_WIDTH, y - PANIC_HALF_HEIGHT,
                    PANIC_HALF_WIDTH * 2, PANIC_HALF_HEIGHT * 2);
        } else {
            panicAttack.set(x - PANIC_HALF_WIDTH, y - PANIC_HALF_HEIGHT,
                    PANIC_HALF_WIDTH * 2, PANIC_HALF_HEIGHT * 2);
        }
        for (Priest priest: priests) {
            if (panicAttack.contains(priest.getX(), priest.getY())) {
                priest.panicStrike(x, y);
            }
        }
    }

    public void clear() {
        priests.clear();
    }
}
