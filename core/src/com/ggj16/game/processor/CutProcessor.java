package com.ggj16.game.processor;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.ggj16.game.model.Direction;
import com.ggj16.game.view.ChalkPriest;
import com.ggj16.game.view.Floor;
import com.ggj16.game.view.Priest;

/**
 * Created by kettricken on 30.01.2016.
 */
public class CutProcessor {

    Array<Priest> priests = new Array<Priest>();
    Floor floor;

    public CutProcessor(Floor floor) {
        this.floor = floor;
    }

    public void update(float delta) {
        for (Priest priest : priests) {
            if (priest instanceof ChalkPriest) {
                ChalkPriest chalkPriest = (ChalkPriest) priest;
                chalkPriest.update();
                if (chalkPriest.getDirection() == Direction.DOWN) {
                    if (chalkPriest.getChalkLine().getEnd().y >= floor.getVisibleTop()) {
                        floor.cut(chalkPriest.getChalkLine().getEnd().x, 0);
                    }
                }
            }
        }
    }

    public void addPriest(ChalkPriest priest) {
        priest.startDrawing();
        priests.add(priest);
    }

    public void draw(ShapeRenderer shapeRenderer) {
        for (Priest priest : priests) {
            priest.draw(shapeRenderer);
        }
    }
}
