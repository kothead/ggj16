package com.ggj16.game.processor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.ggj16.game.view.ChalkPriest;
import com.ggj16.game.view.Floor;
import com.ggj16.game.view.LassoPriest;
import com.ggj16.game.view.Priest;

/**
 * Created by kettricken on 30.01.2016.
 */
public class PriestProcessor {

    Array<Priest> priests = new Array<Priest>();
    Floor floor;

    public PriestProcessor(Floor floor) {
        this.floor = floor;
    }

    public void update(float delta) {
        for (Priest priest : priests) {
            if (priest instanceof ChalkPriest) {
                ChalkPriest chalkPriest = (ChalkPriest) priest;
                chalkPriest.process(delta);
            } else if (priest instanceof LassoPriest) {
                LassoPriest lassoPriest = (LassoPriest) priest;
                lassoPriest.process(delta);
            }
            if (floor.onPit(priest)) {

            }
        }
    }

    public void generatePriests(int amount) {
//        for (int i = 0; i < amount; i++) {
//            ChalkPriest priest = new ChalkPriest(floor);
//            priest.startDrawing();
//            priests.add(priest);
//        }
        for (int i = 0; i < 1; i++) {
            LassoPriest priest = new LassoPriest(floor);
            priests.add(priest);
        }
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
}
