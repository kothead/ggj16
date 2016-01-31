package com.ggj16.game.processor;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.ggj16.game.data.ImageCache;
import com.ggj16.game.data.SkinCache;
import com.ggj16.game.screen.GameScreen;
import com.ggj16.game.state.MessageType;
import com.ggj16.game.util.Utils;

/**
 * Created by kettricken on 30.01.2016.
 */
public class ViewProcessor {

    private final GameScreen gameScreen;
    private final Stage stage;

    private Table pauseTable = new Table();
    private Table gameOverTable = new Table();
    private Table waveTable = new Table();

    public ViewProcessor(GameScreen gameScreen, Stage stage) {
        this.stage = stage;
        this.gameScreen = gameScreen;
    }

    public void initViews() {
        gameOverTable.setFillParent(true);
        pauseTable.setFillParent(true);
        waveTable.setFillParent(true);
        Image gameOver = new Image(ImageCache.getTexture("game_over"));
        Image pause = new Image(ImageCache.getTexture("pause"));
        Image continueBtn = new Image(ImageCache.getTexture("continue"));
        Image exitBtn = new Image(ImageCache.getTexture("exit"));
        Label restart = new Label("Tap to restart", SkinCache.getDefaultSkin());
        Image wave = new Image(ImageCache.getTexture("wave"));
        gameOverTable.add(gameOver);
        gameOverTable.row();
        gameOverTable.add(restart);
        pauseTable.add(pause).colspan(2).center().spaceBottom(50);
        pauseTable.row();
        pauseTable.add(continueBtn).spaceRight(50);
        pauseTable.add(exitBtn);
        waveTable.add(wave).center();

        continueBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Utils.sendMessage(gameScreen, gameScreen, MessageType.BACK_PRESS);
            }
        });

        exitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                gameScreen.getGame().exit();
            }
        });

        gameOverTable.addAction(Actions.alpha(0));
        stage.addActor(gameOverTable);
    }

    public void showPauseTable() {
        stage.addActor(pauseTable);
        pauseTable.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.25f)));
    }

    public void hidePauseTable() {
        pauseTable.addAction(Actions.fadeOut(0.25f));
    }

    public void showGameOverTable() {
        gameOverTable.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.25f)/*, act*/));
    }

    public void hideGameOverTable() {
        gameOverTable.addAction(Actions.fadeOut(0.25f));
    }

    public void showWaveTable(Action action) {
        stage.addActor(waveTable);
        waveTable.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.20f), action, Actions.delay(0.5f),
                Actions.fadeOut(0.20f)));
    }
}
