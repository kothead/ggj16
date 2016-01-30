package com.ggj16.game.processor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.ggj16.game.model.Direction;
import com.ggj16.game.view.ChalkPriest;
import com.ggj16.game.view.Floor;
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
                if (chalkPriest.getDirection() == Direction.DOWN) {
//                    if (chalkPriest.getChalkLine().getEnd().y >= floor.getVisibleTop()) {
//                        floor.cut(chalkPriest.getChalkLine().getEnd().x, 0);
//                    }
                }
            }
        }
    }

    public void generatePriest() {
        ChalkPriest priest = new ChalkPriest(floor);
        priest.startDrawing();
        priests.add(priest);
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
